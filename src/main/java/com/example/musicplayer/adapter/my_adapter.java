package com.example.musicplayer.adapter;

import android.content.Context;
import android.net.wifi.aware.SubscribeConfig;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.utils.MediaUtils;
import com.example.musicplayer.vo.MusicInfo;

import org.jsoup.select.NodeFilter;

import java.io.File;
import java.util.ArrayList;

public class my_adapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<MusicInfo> musicInfos;
    private ArrayList<MusicInfo>musicValues;
    private final Object mLock=new Object();
    MainActivity mainActivity;
    private  MyFilter myFilter;
    public my_adapter(Context context, ArrayList<MusicInfo> musicInfos){
        this.context=context;
        this.musicInfos=musicInfos;
    }
    public void setMusicInfos(ArrayList<MusicInfo>MusicInfos){
        this.musicInfos=musicInfos;
    }
    @Override
    public int getCount() {
        return musicInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return musicInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){

            convertView= LayoutInflater.from(context).inflate(R.layout.mymusic_item,null);
            vh=new ViewHolder();
            //vh.imag=convertView.findViewById(R.id.musicitem_album_imgv);
            vh.tv_title=convertView.findViewById(R.id.musicitem_title_tv);
            vh.tv_artist=convertView.findViewById(R.id.musicitem_artist_tv);
            vh.tv_time=convertView.findViewById(R.id.musicitem_time_tv);
            convertView.setTag(vh);
        }
        vh= (ViewHolder) convertView.getTag();
        MusicInfo musicInfo=musicInfos.get(position);
        vh.tv_title.setText(musicInfo.getTitle());
        vh.tv_artist.setText(musicInfo.getArtist());
        vh.tv_time.setText(MediaUtils.formatTime(musicInfo.getDuration()));
        return convertView;
    }
    static class ViewHolder{
        ImageView imag;
        TextView tv_title;
        TextView tv_artist;
        TextView tv_time;
    }
    public Filter getFilter(){
        if(myFilter==null){
            myFilter= new MyFilter();
        }
        return myFilter;
    }
    class MyFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.i("MYCAT","Myfilter");
            FilterResults results=new FilterResults();
            if(musicValues==null){
                synchronized (mLock){
                    musicValues=new ArrayList<MusicInfo>(musicInfos);
                }
            }
            if(constraint==null||constraint.length()==0){
                synchronized (mLock){
                    ArrayList<MusicInfo>list=new ArrayList<MusicInfo>(musicValues);
                    results.values=list;
                    results.count=list.size();
                }
            }else {
                String constraintString=constraint.toString().toLowerCase();
                final ArrayList<MusicInfo>values=musicValues;
                final int count=values.size();
                final ArrayList<MusicInfo> newValues=new ArrayList<MusicInfo>(count);
                for(MusicInfo value:values ){
                    String title=value.getTitle().toLowerCase();
                    if(title.indexOf(constraintString)!=-1){
                        newValues.add(value);
                    }
                    results.values=newValues;
                    results.count=newValues.size();
                }
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            musicInfos= (ArrayList<MusicInfo>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            }else {
                notifyDataSetInvalidated();
            }

        }
    }
}
