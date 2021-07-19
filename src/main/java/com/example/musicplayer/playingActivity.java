package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayer.fragment.my_framgent;
import com.example.musicplayer.utils.MediaUtils;
import com.example.musicplayer.vo.MusicInfo;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

public class playingActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private TextView music_title,music_artist,music_current,music_total;
    private ImageView music_howplay,music_prev,music_pause,music_next,music_disc,music_down,music_like;
    private SeekBar music_seekbar;
    //private ArrayList<MusicInfo> musicInfos;
    private static final int UNDATE_TIME = 0x1;//更新播放事件的标记

    private dbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_playing);
        dbHelper= (dbHelper) getApplication();
        music_title=findViewById(R.id.music_title_tv);
        music_artist=findViewById(R.id.music_artist_tv);
        music_current=findViewById(R.id.music_current_tv);
        music_total=findViewById(R.id.music_total_tv);
        music_howplay=findViewById(R.id.music_howplay);
        music_prev=findViewById(R.id.music_prev);
        music_pause=findViewById(R.id.music_pause);
        music_next=findViewById(R.id.music_next);
        music_disc=findViewById(R.id.music_disc_imagv);
        music_down=findViewById(R.id.music_down_imgv);
        music_like=findViewById(R.id.xin);
        music_seekbar=findViewById(R.id.music_seekbar);
        //musicInfos= MediaUtils.getMusicInfo(this);
        bindPlayService();
        myHandler=new MyHandler(this);
        music_pause.setOnClickListener(this);
        music_next.setOnClickListener(this);
        music_prev.setOnClickListener(this);
        music_down.setOnClickListener(this);
        music_seekbar.setOnSeekBarChangeListener(this);
        music_howplay.setOnClickListener(this);
        music_like.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindPlayService();
    }

    private static MyHandler myHandler;

    //进度条拖动
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            playService.pause();
            playService.seekTo(progress);
            playService.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    static class MyHandler extends Handler{
        private playingActivity playingActivity;
        public MyHandler(playingActivity playingActivity){
            this.playingActivity=playingActivity;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(playingActivity!=null){
                switch (msg.what){
                    case UNDATE_TIME:
                        playingActivity.music_current.setText(MediaUtils.formatTime(msg.arg1));
                        break;
                }
            }
        }
    }
    @Override
    public void publish(int progress) {  //更新进度
       // music_current.setText(MediaUtils.formatTime(progress));
        Message msg=myHandler.obtainMessage(UNDATE_TIME);
        msg.arg1=progress;
        myHandler.sendMessage(msg);
        music_seekbar.setProgress(progress);
    }

    @Override
    public void change(int position) {
        MusicInfo musicInfo = playService.musicInfos.get(position);
            music_title.setText(musicInfo.getTitle());
            music_artist.setText(musicInfo.getArtist());
            Bitmap imageBitmap = MediaUtils.getArtwork(this, musicInfo.getId(), musicInfo.getAlbumId(), true, false);
            music_disc.setImageBitmap(imageBitmap);
            music_total.setText(MediaUtils.formatTime(musicInfo.getDuration()));
        music_seekbar.setProgress(0);
        music_seekbar.setMax((int) musicInfo.getDuration());
        if(this.playService.isPlaying()) {
            music_pause.setImageResource(R.mipmap.ic_play_bar_btn_pause);
        }
        try {
            MusicInfo likeMusicInfo=dbHelper.db.findFirst(Selector.from(MusicInfo.class).where("likemusicId","=",musicInfo.getLikemusicId())); //看看收藏表有没有
            if(likeMusicInfo!=null){
                music_like.setImageResource(R.mipmap.xin_hong);
            }else {
                music_like.setImageResource(R.mipmap.xin_bai);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.music_pause:
                if(playService.isPlaying()){
                    music_pause.setImageResource(R.mipmap.ic_play_bar_btn_play);
                    playService.pause();
                }else {
                    if(playService.isPause()){
                        music_pause.setImageResource(R.mipmap.ic_play_bar_btn_pause);
                        playService.start();
                    }else {
                        playService.play(playService.getCurrentPostion());
                    }
                }
                break;
            case R.id.music_next:
                playService.next();
                break;
            case R.id.music_prev:
                playService.pre();
                break;
            case R.id.music_down_imgv:
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.music_howplay:{
                switch (playService.getPlay_mode()){
                    case PlayService.ORDER_PLAY: {
                        music_howplay.setImageResource(R.mipmap.ic_play_btn_one);
                        playService.setPlay_mode(PlayService.SINGLE_PLAY);
                        break;
                    }
                    case PlayService.SINGLE_PLAY: {
                        music_howplay.setImageResource(R.mipmap.ic_play_btn_loop);
                        playService.setPlay_mode(PlayService.ORDER_PLAY);
                        break;
                    }
                }
                break;
            }
            case R.id.xin:{
                MusicInfo musicInfo=playService.musicInfos.get(playService.getCurrentPostion());
                try {
                    MusicInfo likeMusicInfo=dbHelper.db.findFirst(Selector.from(MusicInfo.class).where("likemusicId","=",musicInfo.getLikemusicId())); //看看收藏表有没有
                    System.out.println(musicInfo.getLikemusicId());
                    System.out.println(musicInfo);
                    if(likeMusicInfo==null){
                        musicInfo.setLikemusicId(musicInfo.getId());
                        dbHelper.db.save(musicInfo); //保存
                        System.out.println("save");
                        music_like.setImageResource(R.mipmap.xin_hong);
                    }else {
                        dbHelper.db.deleteById(MusicInfo.class, likeMusicInfo.getId()); //删了
                        System.out.println("delect");
                        music_like.setImageResource(R.mipmap.xin_bai);
                    }
                }catch (Exception e){
                    System.out.println("啊啊啊啊啊啊收藏时找不到对应位置");
                }
                break;
            }
            default:
                break;
        }
    }
}
