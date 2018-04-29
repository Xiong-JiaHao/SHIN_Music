package com.gin.xjh.shin_music;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gin.xjh.shin_music.adapter.musicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Song;

import java.util.ArrayList;
import java.util.List;

public class album_details_Activity extends Activity implements View.OnClickListener {

    private ImageView go_back,album_img;
    private TextView album_name,album_singer,album_times;
    private RecyclerView album_rv;

    private List<Song> mSongList;
    private musicRecyclerViewAdapter mMusicListViewAdapter;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_details);
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
    }

    private void initData() {
        /**
         * 测试
         */
        if (mSongList == null) {
            mSongList = new ArrayList<>();
        } else {
            mSongList.clear();
        }

        mSongList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mSongList.add(new Song("反正我信了", "信", "反正我信了", "1111"));
        }
    }

    private void initEvent() {
        mMusicListViewAdapter = new musicRecyclerViewAdapter(this, mSongList);
        album_rv.setLayoutManager(new LinearLayoutManager(this));
        album_rv.setItemAnimator(new DefaultItemAnimator());//默认动画
        album_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        album_rv.setAdapter(mMusicListViewAdapter);

        go_back.setOnClickListener(this);

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
