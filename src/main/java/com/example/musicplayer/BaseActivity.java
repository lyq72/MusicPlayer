package com.example.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public abstract class BaseActivity extends FragmentActivity {
    public PlayService playService;
    private boolean isbound=false; //是否绑定


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.PlayBinder playBinder= (PlayService.PlayBinder) service;
            playService=playBinder.getPlayService();
            playService.setMusicUpdateListener(musicUpdateListener);
            musicUpdateListener.onChanger(playService.getCurrentPostion());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService=null;
            isbound=false;
        }
    };
    private PlayService.MusicUpdateListener musicUpdateListener=new PlayService.MusicUpdateListener() {
        @Override
        public void onPublish(int progress) {
            publish(progress);
        }

        @Override
        public void onChanger(int position) {
            Log.d("MYCAT","postion3");
            change(position);
        }
    };
    public abstract void publish(int progress);
    public abstract void change(int position);

    //binder服务的绑定
    public void  bindPlayService(){
        if(!isbound){
        Intent intent=new Intent(this,PlayService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        isbound=true;
            Log.i("bindPlayService=ture","binder的绑定成功");
        }
    }
    //解绑
    public void unbindPlayService(){
        if (isbound) {
            unbindService(conn);
            isbound=false;
        }
    }

}
