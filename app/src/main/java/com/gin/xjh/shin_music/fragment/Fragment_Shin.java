package com.gin.xjh.shin_music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.albumItemAdapter;
import com.gin.xjh.shin_music.bean.Album;

import java.util.ArrayList;
import java.util.List;

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
        View view = inflater.inflate(R.layout.fragment_shin,null);
        gridView = view.findViewById(R.id.gridview_shin);
        initData();
        initEvent();
        return view;
    }

    private void initData() {
        dataList = new ArrayList<>();
        for (int i = 0; i <10; i++) {
            dataList.add(new Album("大爷们","ss"));
        }
    }

    private void initEvent() {
        //GridView
        adapter = new albumItemAdapter(getContext(),dataList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(getContext(),"check "+arg2,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
