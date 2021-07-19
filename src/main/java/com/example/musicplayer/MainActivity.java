package com.example.musicplayer;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.musicplayer.adapter.SectionsPagerAdapter;
import com.example.musicplayer.fragment.my_framgent;
import com.example.musicplayer.fragment.online_fragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MYCAT";
    private View view1, view2;
    private ViewPager viewPager;
    private List<View> viewList;
    private ImageView mylike;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.my_framgent, null);
        view2 = inflater.inflate(R.layout.online_fragment, null);
        viewList = new ArrayList<>();//将要分页显示的view装入数组中
        viewList.add(view1);
        viewList.add(view2);
        mylike=findViewById(R.id.main_mylike);
        mylike.setOnClickListener(this);
        Log.d(TAG, "onCreateView: ");
        //把Fragment添加到List集合里面
        PagerAdapter mPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void publish(int progress) {
        //更新进度条

    }

    @Override
    public void change(int position) {
        //切换状态播放位置
        if(viewPager.getCurrentItem()==0) {
            Log.d(TAG, "position2");
            my_framgent.newInstance().changeUIStatusOnPlay(position);
        }else if(viewPager.getCurrentItem()==1){
            //online_fragment.newInstance().changeUIStatusOnPlay(position);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.main_mylike){
            Intent intent=new Intent(this,MyLikeActivity.class);
            startActivity(intent);
        }
    }
}
