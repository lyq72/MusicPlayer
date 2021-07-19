package com.example.musicplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.musicplayer.utils.Constant;
import com.lidroid.xutils.DbUtils;

public class dbHelper extends Application {
    public static SharedPreferences sp;
    public static DbUtils db;

    @Override
    public void onCreate() {
        super.onCreate();
        sp=getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        db=DbUtils.create(getApplicationContext(),Constant.DB_NAME);

    }
}
