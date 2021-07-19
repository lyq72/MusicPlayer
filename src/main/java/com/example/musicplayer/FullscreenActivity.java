package com.example.musicplayer;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private static final int START_ACTIVITY=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fullscreen);

        //启动服务器
        startService(new Intent(this,PlayService.class));

        handler.sendEmptyMessageDelayed(START_ACTIVITY,3000);

    }
    private Handler handler=new Handler(){
        public void handleMessage(Message message){
            super.handleMessage(message);
            switch (message.what){
                case START_ACTIVITY:
                    startActivity(new Intent(FullscreenActivity.this,MainActivity.class));
                    finish();
                    break;
            }
        }
    };
}

