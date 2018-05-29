package com.gin.xjh.shin_music.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.gin.xjh.shin_music.view.cd_ImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by Gin on 2018/4/23.
 */

public class Fragment_Music extends Fragment {

    private cd_ImageView mAlbum;

    public static final String MUSIC_ACTION_PLAY = "MusicNotificaion.To.PLAY";
    public static final String MUSIC_ACTION_PAUSE = "MusicNotificaion.To.PAUSE";

    private CDBroadCast cdBroadCast = null;

    private Animation animation;
    private LinearInterpolator lin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, null);
        cdBroadCast = new CDBroadCast();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MUSIC_ACTION_PLAY);
        intentFilter.addAction(MUSIC_ACTION_PAUSE);
        broadcastManager.registerReceiver(cdBroadCast,intentFilter);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        mAlbum = view.findViewById(R.id.album);
        if (MusicUtil.getListSize() > 0) {
            Picasso.with(getContext()).load(MusicUtil.getNowSong().getAlbumUrl())
                    .placeholder(R.drawable.album)
                    .error(R.drawable.album)
                    .into(mAlbum);
        }

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.img_animation);
        lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
    }

    private void initEvent() {
        if (MusicUtil.isPlayMusic()) {
            start();
        } else {
            pause();
        }

    }

    private void start() {
        mAlbum.clearAnimation();
        mAlbum.setAnimation(animation);
    }

    private void pause() {
        mAlbum.clearAnimation();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(cdBroadCast);
        mAlbum.clearAnimation();
        super.onDestroy();
    }

    public class CDBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MUSIC_ACTION_PLAY:
                    Picasso.with(getContext()).load(MusicUtil.getNowSong().getAlbumUrl())
                            .placeholder(R.drawable.album)
                            .error(R.drawable.album)
                            .into(mAlbum);
                    start();
                    break;
                case MUSIC_ACTION_PAUSE:
                    pause();
                    break;
            }
        }
    }


}
