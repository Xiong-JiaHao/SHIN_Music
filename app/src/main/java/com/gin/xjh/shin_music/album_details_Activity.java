package com.gin.xjh.shin_music;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gin.xjh.shin_music.adapter.musicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Album;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.util.DownloadBitmapUtil;
import com.gin.xjh.shin_music.util.TimesUtil;

import java.io.IOException;
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
            final Bitmap[] bitmap = {null};
            new Thread() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        bitmap[0] = DownloadBitmapUtil.getHttpBitmap(album.getAlbumUrl());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                album_img.setImageBitmap(bitmap[0]);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
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

        String str = "chouchou.can2.moe:3000/album?id=" + album.getAlbumId();
    }

    private void updateOnlineEvent() {

        String str = "chouchou.can2.moe:3000/top/list?idx=" + id;
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
