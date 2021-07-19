package com.example.musicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.musicplayer.BaseActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.vo.searchResult;

import java.util.ArrayList;

public class onlineAdapter extends BaseAdapter {
    private static final String TAG = "MYCAT";
    private Context context;
    private ArrayList<searchResult> searchResults;
    public onlineAdapter(Context ctx,ArrayList<searchResult> searchResults) {
        this.context = ctx;
        this.searchResults = searchResults;
    }

    public ArrayList<searchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<searchResult> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            Log.d(TAG, String.valueOf(LayoutInflater.from(context)==null));
            convertView = LayoutInflater.from(context).inflate(R.layout.onlinemusic_item,null);
            vh = new ViewHolder();
            vh.textView1_title = convertView.findViewById(R.id.textView1_title);
            vh.textView2_singer =convertView.findViewById(R.id.textView2_singer);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        searchResult result = searchResults.get(position);
        vh.textView1_title.setText(result.getMusicName());
        vh.textView2_singer.setText(result.getArtist());
        return convertView;
    }

    static class ViewHolder{
        TextView textView1_title;
        TextView textView2_singer;
    }

}
