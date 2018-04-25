package com.gin.xjh.shin_music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gin on 2018/4/23.
 */

public class Fragment_Shin extends Fragment {

    private GridView gridView;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter adapter;

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
            Map<String, Object> map=new HashMap<>();
            map.put("img", R.drawable.dayemen);
            map.put("text","大爷们");
            dataList.add(map);
        }
    }

    private void initEvent() {
        //GridView
        String[] from={"img","text"};
        int[] to={R.id.shin_img,R.id.shin_text};
        adapter=new SimpleAdapter(getContext(), dataList, R.layout.fragment_shin_item, from, to);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(getContext(),"check "+arg2,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
