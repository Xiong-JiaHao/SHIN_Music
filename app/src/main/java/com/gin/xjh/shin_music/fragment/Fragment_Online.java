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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.recommendmusicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gin on 2018/4/23.
 */

public class Fragment_Online extends Fragment {

    private List<Song> mSongList = null;
    private RecyclerView mRecyclerView;
    private recommendmusicRecyclerViewAdapter mRecommendmusicRecyclerViewAdapter;

    private GridView gridView;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter adapter;

    private EditText mFind = null;
    private ImageView mCheck;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_music, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.fragment_recommend_music_list);
        gridView = view.findViewById(R.id.Online_music_gv);
        mFind = view.findViewById(R.id.find_online_name);
        mCheck = view.findViewById(R.id.find_Onlinemusic);
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

        dataList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("img", R.drawable.test);
            map.put("text", "摇滚");
            dataList.add(map);
        }

    }

    private void initEvent() {

        //RecyclerView
        mRecommendmusicRecyclerViewAdapter = new recommendmusicRecyclerViewAdapter(getContext(), mSongList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//默认动画
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mRecommendmusicRecyclerViewAdapter);


        //GridView
        String[] from = {"img", "text"};
        int[] to = {R.id.music_img, R.id.music_text};
        adapter = new SimpleAdapter(getContext(), dataList, R.layout.online_music_item, from, to);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(getContext(), "check " + arg2, Toast.LENGTH_SHORT).show();
            }
        });

        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });
    }

    /**
     * 查找数据
     */
    private void find() {
        Toast.makeText(getContext(), "find", Toast.LENGTH_SHORT).show();
    }
}
