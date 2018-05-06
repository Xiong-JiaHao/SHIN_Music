package com.gin.xjh.shin_music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.albumItemAdapter;
import com.gin.xjh.shin_music.bean.Album;

import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Gin on 2018/4/23.
 */

public class Fragment_Shin extends Fragment {

    private GridView gridView;
    private List<Album> dataList;
    private albumItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shin, null);
        initView(view);
        initData();
        //initEvent();
        return view;
    }

    private void initView(View view) {
        gridView = view.findViewById(R.id.gridview_shin);
    }


    private void initData() {
        BmobQuery<Album> query = new BmobQuery<>();
        query.findObjects(new FindListener<Album>() {
            @Override
            public void done(List<Album> list, BmobException e) {
                Collections.sort(list, new SortByTime());
                dataList = list;
                initEvent();
            }
        });

    }

    private void initEvent() {
        //GridView
        adapter = new albumItemAdapter(getContext(), dataList);
        gridView.setAdapter(adapter);
    }

    private class SortByTime implements java.util.Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            Album a = (Album) o1;
            Album b = (Album) o2;
            return -a.getTimes().compareTo(b.getTimes());
        }
    }
}
