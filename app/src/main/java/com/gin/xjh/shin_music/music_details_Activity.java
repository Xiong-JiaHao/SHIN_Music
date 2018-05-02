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

public class music_details_Activity extends Activity implements View.OnClickListener {

    private ImageView go_back;
    private TextView find_name;
    private RecyclerView music_rv;

    private List<Song> mSongList;
    private musicRecyclerViewAdapter mMusicListViewAdapter;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_details);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        find_name = findViewById(R.id.find_name);
        music_rv = findViewById(R.id.music_rv);
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
        music_rv.setLayoutManager(new LinearLayoutManager(this));
        music_rv.setItemAnimator(new DefaultItemAnimator());//默认动画
        music_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        music_rv.setAdapter(mMusicListViewAdapter);

        go_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                finish();
                break;
        }
    }
}
