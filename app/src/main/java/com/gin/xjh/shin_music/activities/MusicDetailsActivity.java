package com.gin.xjh.shin_music.activities;

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
import com.gin.xjh.shin_music.adapter.MusicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.netrequest.FindNetMusic;

import java.util.List;

public class MusicDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mGoBack;
    private TextView mFindName, mFindHint;
    private RecyclerView mMusicRv;

    private List<Song> mSongList;
    private MusicRecyclerViewAdapter mMusicListViewAdapter;

    private boolean isOnline;
    private String mFindNameStr;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_details);
        initView();
        Intent intent = getIntent();
        isOnline = intent.getBooleanExtra(getString(R.string.IS_ONLINE), true);
        mFindNameStr = intent.getStringExtra(getString(R.string.NAME));
        if (!isOnline) {
            mSongList = (List<Song>) intent.getBundleExtra(getString(R.string.SONG_LIST)).get(getString(R.string.SONG_LIST));
        }
        initEvent();
    }

    private void initView() {
        mGoBack = findViewById(R.id.go_back);
        mFindName = findViewById(R.id.find_name);
        mMusicRv = findViewById(R.id.music_rv);
        mFindHint = findViewById(R.id.mFind_hint);
    }

    private void initEvent() {
        mFindName.setText(mFindNameStr);
        if (isOnline) {
            new FindNetMusic().getJson(mFindNameStr, mMusicRv, mFindHint, this);
        } else {
            mMusicListViewAdapter = new MusicRecyclerViewAdapter(this, mSongList);
            mMusicRv.setLayoutManager(new LinearLayoutManager(this));
            mMusicRv.setItemAnimator(new DefaultItemAnimator());//默认动画
            mMusicRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mMusicRv.setAdapter(mMusicListViewAdapter);

            mFindHint.setVisibility(View.GONE);
        }
        mGoBack.setOnClickListener(this);

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
