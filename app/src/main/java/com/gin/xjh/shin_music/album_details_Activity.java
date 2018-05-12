package com.gin.xjh.shin_music;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gin.xjh.shin_music.adapter.musicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Album;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.util.Constant;
import com.gin.xjh.shin_music.util.NetUtile;
import com.gin.xjh.shin_music.util.TimesUtil;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class album_details_Activity extends Activity implements View.OnClickListener {

    private ImageView go_back,album_img;
    private TextView album_name, album_singer, album_times, album_hint;
    private RecyclerView album_rv;

    private List<Song> mSongList;
    private musicRecyclerViewAdapter mMusicListViewAdapter;
    private Album album;
    private String id, name;
    private boolean isAlbum;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_details);
        Intent intent = getIntent();
        isAlbum = intent.getBooleanExtra("isAlbum", true);
        if (isAlbum) {
            album = (Album) intent.getBundleExtra("album").get("album");
        } else {
            id = intent.getStringExtra("id");
            name = intent.getStringExtra("name");
        }
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        album_img = findViewById(R.id.album_img);
        album_name = findViewById(R.id.album_name);
        album_singer = findViewById(R.id.album_singer);
        album_times = findViewById(R.id.album_times);
        album_rv = findViewById(R.id.album_rv);
        album_hint = findViewById(R.id.album_hint);
    }

    private void initData() {
        mSongList = new ArrayList<>();
    }

    private void initEvent() {
        go_back.setOnClickListener(this);
        if (isAlbum) {
            Picasso.with(this).load(album.getAlbumUrl())
                    .placeholder(R.drawable.album)
                    .error(R.drawable.album)
                    .into(album_img);
            album_singer.setText("歌手：" + album.getSinger());
            album_name.setText(album.getAlbumName());
            try {
                album_times.setText("发行时间：" + TimesUtil.longToString(album.getTimes(), "yyyy-MM-dd"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //updateBmobEvent();
        } else {
            album_name.setText(name);
            //updateOnlineEvent();
        }
    }

    private void updateBmobEvent() {
        String str = Constant.URL_BASE + "/album?id=" + album.getAlbumId();
        Log.d("ginshin", NetUtile.getJson(str));
//        try {
//            String str = Constant.URL_BASE+"/album?id=" + album.getAlbumId();
//            JSONArray jsonArray = new JSONArray(NetUtile.getJson(str));
//            for (int i = 0; i< jsonArray.length(); i++) {
//                //循环遍历，依次取出JSONObject对象
//                //用getInt和getString方法取出对应键值
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                int stu_no = jsonObject.getInt("stu_no");
//                String stu_name = jsonObject.getString("stu_name");
//                String stu_sex = jsonObject.getString("stu_sex");
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void updateOnlineEvent() {
        String str = Constant.URL_BASE + "/top/list?idx=" + id;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                finish();
                break;
        }
    }
}
