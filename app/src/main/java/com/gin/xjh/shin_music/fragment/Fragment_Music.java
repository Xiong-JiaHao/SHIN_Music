package com.gin.xjh.shin_music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.view.cd_ImageView;

/**
 * Created by Gin on 2018/4/23.
 */

public class Fragment_Music extends Fragment {

    private cd_ImageView mAlbum;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, null);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        mAlbum = view.findViewById(R.id.album);
    }

    private void initEvent() {
        mAlbum.start();
    }

}