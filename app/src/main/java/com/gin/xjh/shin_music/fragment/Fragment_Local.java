package com.gin.xjh.shin_music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.musicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gin on 2018/4/23.
 */

public class Fragment_Local extends Fragment {
    private List<Song> mSongList;
    private RecyclerView mRecyclerView;
    private musicRecyclerViewAdapter mMusicListViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music,null);
        mRecyclerView = view.findViewById(R.id.fragment_local_music_list);
        initData();
        initEvent();
        return view;
    }

    private void initData() {
        /**
         * 测试
         */
        if(mSongList==null){
            mSongList=new ArrayList<>();
        }
        else{
            mSongList.clear();
        }

        mSongList=new ArrayList<>();
        for(int i=0;i<10;i++){
            mSongList.add(new Song("反正我信了","信","反正我信了","1111"));
        }
    }

    private void initEvent(){
        mMusicListViewAdapter = new musicRecyclerViewAdapter(getContext(),mSongList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//默认动画
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mMusicListViewAdapter);
    }
}
