package com.gin.xjh.shin_music.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gin.xjh.shin_music.R;

/**
 * Created by Gin on 2018/4/23.
 */

public class Fragment_Online extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_music,null);
        return view;
    }
}
