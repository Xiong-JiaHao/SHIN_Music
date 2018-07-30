package com.gin.xjh.shin_music.activity;

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

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.netrequest.FindNetMusic;
import com.gin.xjh.shin_music.adapter.MusicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Song;

import java.util.List;

public class MusicDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView go_back;
    private TextView find_name, mFind_hint;
    private RecyclerView music_rv;

    private List<Song> mSongList;
    private MusicRecyclerViewAdapter mMusicListViewAdapter;

    private boolean isOnline;
    private String mFind_Name;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_details_activity);
        initView();
        Intent intent = getIntent();
        isOnline = intent.getBooleanExtra("isOnline", true);
        mFind_Name = intent.getStringExtra("name");
        if (!isOnline) {
            mSongList = (List<Song>) intent.getBundleExtra("songlist").get("songlist");
        }
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        find_name = findViewById(R.id.find_name);
        music_rv = findViewById(R.id.music_rv);
        mFind_hint = findViewById(R.id.mFind_hint);
    }

    private void initEvent() {
        find_name.setText(mFind_Name);
        if (isOnline) {
            new FindNetMusic().getJson(mFind_Name, music_rv, mFind_hint, this);
        } else {
            mMusicListViewAdapter = new MusicRecyclerViewAdapter(this, mSongList);
            music_rv.setLayoutManager(new LinearLayoutManager(this));
            music_rv.setItemAnimator(new DefaultItemAnimator());//默认动画
            music_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            music_rv.setAdapter(mMusicListViewAdapter);

            mFind_hint.setVisibility(View.GONE);
        }
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
