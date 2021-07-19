package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.musicplayer.utils.MediaUtils;
import com.example.musicplayer.vo.MusicInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//音乐播放的服务组件 功能：播放，暂停，上下首，获取播放进度条
public class PlayService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private MediaPlayer mediaPlayer; //当前播放进度
    public ArrayList<MusicInfo> musicInfos;
    private int currentPostion;
    public boolean isPause=false;
    public MusicUpdateListener musicUpdateListener;//生名一个属性
    boolean isPause(){return isPause;}
    private ExecutorService executorService= Executors.newSingleThreadExecutor();//单线程时
    //播放模式
    private int play_mode=1;
    public static final int ORDER_PLAY=1;
    public static final int SINGLE_PLAY=2;
    private int ChangePlayList=1;
    public static final int MYLIST=1;
    public static final int LISTLIST=2;

    public int getChangePlayList() {
        return ChangePlayList;
    }

    public void setChangePlayList(int changePlayList) {
        ChangePlayList = changePlayList;
    }

    public int getPlay_mode() {
        return play_mode;
    }

    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    public PlayService() {
    }

    public void setMusicInfos(ArrayList<MusicInfo> musicInfos) {
        this.musicInfos = musicInfos;
    }

    public int getCurrentPostion(){
        return currentPostion;
    }

    //播放顺序的设定
    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (play_mode){
            case ORDER_PLAY:
                next();
                break;
            case SINGLE_PLAY:
                play(currentPostion);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    //调用PlayService当前的对象
    class PlayBinder extends Binder{
        public PlayService getPlayService(){
            return PlayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
      //  throw new UnsupportedOperationException("Not yet implemented");
        return new PlayBinder();
    }
    public void onCreate(){
        super.onCreate();
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        musicInfos=MediaUtils.getMusicInfo(this);
        //开始调用更新状态啦
        executorService.execute(updateStatusRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(executorService!=null&&!executorService.isShutdown()){
            executorService.isShutdown();
            executorService=null;
        }
    }

    //更新状态
    Runnable updateStatusRunnable=new Runnable() {
        @Override
        public void run() {
            while (true){
                if(musicUpdateListener!=null&&mediaPlayer!=null&&mediaPlayer.isPlaying()){
                    //不断调用获得线程当前进度的方法
                    musicUpdateListener.onPublish(getCurrentProgress());
                }
                try {
                Thread.sleep(500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    };
    //功能方法
    //从头播放
    public void play(int postion) {
        MusicInfo musicInfo=null;
        if (postion<=0&&postion>musicInfos.size()) {
            postion=0;
        }
        try {
            musicInfo = musicInfos.get(postion);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse(musicInfo.getUrl()));
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentPostion=postion;
            Log.i("playserve.play=ture","成功啦");
        }catch (Exception e){
            Log.i("playserve.play=false","出错啦！！");
        }
            if(musicUpdateListener!=null){
                Log.d("MYCAT","postion4");
                musicUpdateListener.onChanger(currentPostion);
            }
        }


    public void pause(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause=true;
        }
    }

    public void next(){
        if(currentPostion+1>=musicInfos.size()){
            currentPostion=0;
        }else {
            currentPostion++;
        }
        play(currentPostion);
    }

    public void pre(){
        if(currentPostion-1<0){
            currentPostion=musicInfos.size()-1;
        }else {
            currentPostion--;
        }
        play(currentPostion);
    }
    //直接播放当前的
    public void start(){
        if(!mediaPlayer.isPlaying()&&mediaPlayer!=null){
            mediaPlayer.start();

        }
    }
    //播放状态
    public boolean isPlaying(){
        if (mediaPlayer!=null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }
    //获取当前进度
    public int getCurrentProgress(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    public int getDuration(){
        return mediaPlayer.getDuration();
    }
    //调换位置
    public void seekTo(int msec){
        mediaPlayer.seekTo(msec);
    }
    //更新状态的接口
    public interface MusicUpdateListener{
        public void onPublish(int progress);//进度条更新
        public void onChanger(int position);//切换歌曲位置
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }
}
