package com.gin.xjh.shin_music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gin.xjh.shin_music.Net_Request.getNetMusicList;
import com.gin.xjh.shin_music.adapter.musicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Album;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.util.TimesUtil;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class album_details_Activity extends BaseActivity implements View.OnClickListener {

    private ImageView go_back,album_img;
    private RecyclerView album_rv;
    private TextView album_name, album_singer, album_times, album_hint;

    private List<Song> mSongList;
    private musicRecyclerViewAdapter mMusicRecyclerViewAdapter;
    private Album album;
    private String name,url;
    private int id;
    private boolean isAlbum;

    private Context mContext;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_details);
        Intent intent = getIntent();
        isAlbum = intent.getBooleanExtra("isAlbum", true);
        if (isAlbum) {
            album = (Album) intent.getBundleExtra("album").get("album");
        } else {
            id = intent.getIntExtra("id",0);
            name = intent.getStringExtra("name");
            url=intent.getStringExtra("url");
        }
        initView();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        album_img = findViewById(R.id.album_img);
        album_name = findViewById(R.id.album_name);
        album_singer = findViewById(R.id.album_singer);
        album_times = findViewById(R.id.album_times);
    }

    private void initEvent() {
        go_back.setOnClickListener(this);
        mContext = this;
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
            updateBmobEvent();
        } else {
            Picasso.with(this).load(url)
                .placeholder(R.drawable.album)
                .error(R.drawable.album)
                .into(album_img);
            album_name.setTextSize(25);
            album_name.setText(name);
            updateOnlineEvent();
        }
    }

    private void updateBmobEvent() {
        album_rv = findViewById(R.id.album_rv);
        album_hint=findViewById(R.id.album_hint);
        BmobQuery<Song> query = new BmobQuery<>();
        query.addWhereEqualTo("AlbumName", album.getAlbumName());
        query.findObjects(new FindListener<Song>() {
            @Override
            public void done(List<Song> list, BmobException e) {
                if (e == null) {
                    mSongList = list;
                    for (int i = 0; i < mSongList.size(); i++) {
                        mSongList.get(i).setAlbumId(album.getAlbumId());
                        mSongList.get(i).setAlbumTime(album.getTimes());
                    }
                    album_hint.setVisibility(View.GONE);
                    mMusicRecyclerViewAdapter = new musicRecyclerViewAdapter(mContext, mSongList);
                    album_rv.setLayoutManager(new LinearLayoutManager(mContext));
                    album_rv.setItemAnimator(new DefaultItemAnimator());//默认动画
                    album_rv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                    album_rv.setAdapter(mMusicRecyclerViewAdapter);
                }
            }
        });

    }

    private void updateOnlineEvent() {
        new getNetMusicList().getJson(id, findViewById(R.id.album_rv), findViewById(R.id.album_hint), this);
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
