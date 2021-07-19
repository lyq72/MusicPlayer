package com.example.musicplayer.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.onlineAdapter;
import com.example.musicplayer.utils.Constant;
import com.example.musicplayer.vo.searchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link online_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class online_fragment extends Fragment {
    private static online_fragment online_fragment;
    private MainActivity mainActivity;
    private ListView listView;
    private LinearLayout load_layout;
    private ArrayList<searchResult> searchResults = new ArrayList<searchResult>();
    private onlineAdapter onlineAdapter;
    private int page = 1;//搜索音乐的页码
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    private online_fragment() {

    }


    public static online_fragment newInstance() {
        if (online_fragment == null) {
            online_fragment = new online_fragment();
        }
        return online_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_fragment, container, false);
        Log.d("MYCAT", "onCreateView22222: ");
        listView = view.findViewById(R.id.top_list);
        load_layout = view.findViewById(R.id.load_layout);
        loadNetData();
        return view;
    }

    private void loadNetData() {
        load_layout.setVisibility(View.VISIBLE);
        new LoadDataTask().execute(Constant.BAIDU_URL + Constant.BAIDU_DAYHOT);
    }

    class LoadDataTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            load_layout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            searchResults.clear();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer==1){
                onlineAdapter = new onlineAdapter(mainActivity,searchResults);
                System.out.println(searchResults);
                listView.setAdapter(onlineAdapter);
            }
            load_layout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];
            try {
                //使用Jsoup组件请求网络，并解析音乐数据
                Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(6*1000).get();
                //System.out.println(doc);

                Elements songTitles = doc.select("span.song-title");
                Elements artists = doc.select("span.author_list");
                for (int i = 0; i < songTitles.size(); i++) {
                    searchResult searchResult = new searchResult();
                    Elements urls = songTitles.get(i).getElementsByTag("a");
                    searchResult.setUrl(urls.get(0).attr("href"));
                    searchResult.setMusicName(urls.get(0).text());
                    //
                    Elements artistElements = artists.get(i).getElementsByTag("a");
                    searchResult.setArtist(artistElements.get(0).text());

                    searchResult.setAlbum("热歌榜");
                    searchResults.add(searchResult);

            }

                System.out.println(searchResults);

            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
                return 1;

        }
    }
}