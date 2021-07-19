package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.musicplayer.adapter.my_adapter;
import com.example.musicplayer.vo.MusicInfo;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;

import static com.example.musicplayer.PlayService.LISTLIST;
import static com.example.musicplayer.PlayService.MYLIST;

public class MyLikeActivity extends BaseActivity implements AdapterView.OnItemClickListener , AdapterView.OnItemLongClickListener {
    public ListView mylikeList;
    public dbHelper dbHelper;
    public ArrayList<MusicInfo>likemusicInfos;
    public my_adapter my_adapter;
    public boolean isChange=false; //当前播放列表是否为收藏列表
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_like);
        mylikeList=findViewById(R.id.mylikeList);
        mylikeList.setOnItemClickListener(this);
        mylikeList.setOnItemLongClickListener(this);
        initData();
    }

    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }

    private void initData() {
        try {
            likemusicInfos= (ArrayList<MusicInfo>) dbHelper.db.findAll(MusicInfo.class);
            my_adapter=new my_adapter(this,likemusicInfos);
            mylikeList.setAdapter(my_adapter);
        }catch (Exception e){
            System.out.println("写入不了我喜欢的音乐到列表");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(playService.getChangePlayList()!=LISTLIST){
            playService.setMusicInfos(likemusicInfos);
            playService.setChangePlayList(LISTLIST);
        }
        playService.play(position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        MusicInfo likemusicInfo=null;
        likemusicInfo = likemusicInfos.get(position);
        System.out.println(likemusicInfo);
        try {
            dbHelper.db.deleteById(MusicInfo.class, likemusicInfo.getId()); //删了
            Toast.makeText(MyLikeActivity.this, "取消收藏成功！",Toast.LENGTH_SHORT).show();
            initData();
            my_adapter.notifyDataSetChanged();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }
}
