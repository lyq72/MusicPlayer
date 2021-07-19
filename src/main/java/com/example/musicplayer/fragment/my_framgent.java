package com.example.musicplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.my_adapter;
import com.example.musicplayer.playingActivity;
import com.example.musicplayer.utils.MediaUtils;
import com.example.musicplayer.vo.MusicInfo;

import java.util.ArrayList;
import static com.example.musicplayer.PlayService.MYLIST;


public class my_framgent extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "MYCAT";
    private ListView mymusic_listview;
    private ArrayList<MusicInfo> musicInfos;
    private MainActivity mainActivity;
    private my_adapter my_adapter;
    public ImageView album_imgv;
    public TextView title_tv,artist_tv;
    public ImageView pause_imgv,next_imgv;
    public boolean isPause=false;
    private int position = 0;
    private SearchView searchView;
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    private static my_framgent my_framgent;
    private my_framgent(){

    }


    public static my_framgent newInstance() {
        if (my_framgent==null){
            my_framgent = new my_framgent();
        }
        return my_framgent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_framgent, container, false);
        Log.d(TAG, "onCreateView1: ");
        mymusic_listview = view.findViewById(R.id.mymusic_listview);
        album_imgv=view.findViewById(R.id.nf_album_imgv);
        artist_tv=view.findViewById(R.id.nf_artist_tv);
        title_tv=view.findViewById(R.id.nf_title_tv);
        pause_imgv=view.findViewById(R.id.nf_pause_imgv);
        next_imgv=view.findViewById(R.id.nf_next_imgv);
        searchView=view.findViewById(R.id.searchview);
        pause_imgv.setOnClickListener(this);
        next_imgv.setOnClickListener(this);
        mymusic_listview.setOnItemClickListener(this);
        album_imgv.setOnClickListener(this);
        mymusicData();
        //绑定播放服务
        mainActivity.bindPlayService();
        //搜索
        mymusic_listview.setTextFilterEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("查询");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                ListAdapter adapter=mymusic_listview.getAdapter();
                if(adapter instanceof Filterable){
                    Filter filter=((Filterable) adapter).getFilter();
                    if(query==null||query.length()==0){
                        filter.filter(null);
                    }else {
                        filter.filter(query);
                    }
                }
                return false;
            }

            //搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                ListAdapter adapter=mymusic_listview.getAdapter();
                if(adapter instanceof Filterable){
                    Filter filter=((Filterable) adapter).getFilter();
                    if(newText==null||newText.length()==0){
                        filter.filter(null);
                    }else {
                        filter.filter(newText);
                    }
                }
                return true;
            }
        });
        mymusic_listview.setAdapter(my_adapter);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mainActivity.bindPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
        //解除绑定播放服务
        mainActivity.unbindPlayService();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //接触播放服务
        mainActivity.unbindPlayService();
    }

    //加载本地列表
    private void mymusicData() {
        musicInfos = MediaUtils.getMusicInfo(mainActivity);
        my_adapter = new my_adapter(mainActivity, musicInfos);
        mymusic_listview.setAdapter(my_adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("position:", String.valueOf(position));
        if(mainActivity.playService.getChangePlayList()!=MYLIST){
            mainActivity.playService.setMusicInfos(musicInfos);
            mainActivity.playService.setChangePlayList(MYLIST);
        }
        mainActivity.playService.play(position);
        pause_imgv.setImageResource(R.mipmap.ic_play_bar_btn_pause);
        Intent intent=new Intent(mainActivity, playingActivity.class);
        intent.putExtra("isPause",isPause);
        startActivity(intent);
    }

    //回掉播放状态下的UI设置，就是下面的那个框框
    public void changeUIStatusOnPlay(int position){
        if(position>=0&&position<mainActivity.playService.musicInfos.size()){
            MusicInfo musicInfo=mainActivity.playService.musicInfos.get(position);
            title_tv.setText(musicInfo.getTitle());
            artist_tv.setText(musicInfo.getArtist());
            if (mainActivity.playService.isPlaying()){
                pause_imgv.setImageResource(R.mipmap.ic_play_bar_btn_pause);
            }else{
                pause_imgv.setImageResource(R.mipmap.ic_play_bar_btn_play);
            }
            Bitmap imageBitmap =MediaUtils.getArtwork(mainActivity,musicInfo.getId(),musicInfo.getAlbumId(),true,true);
            album_imgv.setImageBitmap(imageBitmap);
            this.position = position;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nf_pause_imgv:
                if(mainActivity.playService.isPlaying()){
                    pause_imgv.setImageResource(R.mipmap.ic_play_bar_btn_play);
                    mainActivity.playService.pause();
                    isPause=true;
                }else {
                    pause_imgv.setImageResource(R.mipmap.ic_play_bar_btn_pause);
                    if(isPause){
                        mainActivity.playService.start();
                    } else {
                        mainActivity.playService.play(0);
                    }
                    isPause=false;
                }
                break;
            case R.id.nf_next_imgv:
                mainActivity.playService.next();
                break;
            case R.id.nf_album_imgv:
                Intent intent=new Intent(mainActivity, playingActivity.class);
                intent.putExtra("isPause",isPause);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
