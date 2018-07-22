package com.gin.xjh.shin_music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.musicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.music_details_Activity;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.zhy.m.permission.MPermissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gin on 2018/4/23.
 */

public class Fragment_Local extends Fragment {
    private List<Song> mSongList;
    private RecyclerView mRecyclerView;
    private musicRecyclerViewAdapter mMusicListViewAdapter;

    private EditText mFind = null;
    private ImageView mCheck;

    private TextView mMusic_hint;
    private Handler mMainHandler;

    private void obtainMainHandler() {
        if (mMainHandler != null) {
            return;
        }
        mMainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                initEvent();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, null);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.fragment_local_music_list);
        mFind = view.findViewById(R.id.find_local_name);
        mCheck = view.findViewById(R.id.find_Localmusic);
        mMusic_hint = view.findViewById(R.id.music_hint);

        obtainMainHandler();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSongList = MusicUtil.getLocalMusic(getContext());
                Message msg = new Message();
                mMainHandler.sendMessage(msg);
            }
        }).start();

    }

    private void initEvent() {
        if (mSongList != null) {
            mMusicListViewAdapter = new musicRecyclerViewAdapter(getContext(), mSongList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());//默认动画
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            mRecyclerView.setAdapter(mMusicListViewAdapter);

            mMusic_hint.setVisibility(View.GONE);
        } else {
            mMusic_hint.setText("当前未扫描到歌曲");
        }

        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });
    }

    private void find() {
        String name = mFind.getText().toString();
        if (name.compareTo("") == 0 || name.length() == 0) {
            Toast.makeText(getContext(), "请输入搜索名称再点击按钮", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Song> mFindSongList = new ArrayList<>();
        Pattern pattern = Pattern.compile(name);
        for (Song song : mSongList) {
            Matcher matcher = pattern.matcher(song.getSongName());
            if (matcher.find()) {
                mFindSongList.add(song);
            }
        }
        mFind.setText("");
        Intent intent = new Intent(getContext(), music_details_Activity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("songlist", (Serializable) mFindSongList);
        intent.putExtra("songlist", bundle);
        intent.putExtra("name", name);
        intent.putExtra("isOnline", false);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (mFind != null) {
            mFind.setText("");
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
