package com.gin.xjh.shin_music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.AlbumItemAdapter;
import com.gin.xjh.shin_music.bean.Album;
import com.gin.xjh.shin_music.util.NetStateUtil;

import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Gin on 2018/4/23.
 */

public class FragmentShin extends Fragment {

    private GridView gridView;
    private List<Album> dataList;
    private AlbumItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shin_fragment, null);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        gridView = view.findViewById(R.id.gridview_shin);
    }


    private void initData() {
        if (NetStateUtil.getNetWorkState(getContext()) == NetStateUtil.NO_STATE) {
            Toast.makeText(getContext(), "当前无网络...", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Album> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(86400000);//缓存有1天的有效期
        query.findObjects(new FindListener<Album>() {
            @Override
            public void done(List<Album> list, BmobException e) {
                if(list!=null){
                    Collections.sort(list, new SortByTime());
                    dataList = list;
                    initEvent();
                }
            }
        });

    }

    private void initEvent() {
        //GridView
        adapter = new AlbumItemAdapter(getContext(), dataList);
        gridView.setAdapter(adapter);
    }

    private class SortByTime implements java.util.Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            Album a = (Album) o1;
            Album b = (Album) o2;
            if (a.getAlbumId() == -1) {
                return -1;
            }
            if (b.getAlbumId() == -1) {
                return 1;
            }
            return -a.getTimes().compareTo(b.getTimes());
        }
    }
}
