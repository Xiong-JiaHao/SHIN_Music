package com.gin.xjh.shin_music.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.gin.xjh.shin_music.view.cd_ImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by Gin on 2018/4/23.
 */

public class Fragment_Music extends Fragment {

    private cd_ImageView mAlbum;

    public static final String MUSIC_NOTIFICATION_ACTION_PLAY = "MusicNotificaion.To.PLAY";
    public static final String MUSIC_NOTIFICATION_ACTION_PAUSE = "MusicNotificaion.To.PAUSE";
    public static final String MUSIC_NOTIFICATION_ACTION_CHANGEIMG = "MusicNotificaion.To.CHANGEIMG";

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
        Picasso.with(getContext()).load(MusicUtil.getNowSong().getAlbumUrl())
                .placeholder(R.drawable.album)
                .error(R.drawable.album)
                .into(mAlbum);
    }

    private void initEvent() {
        if (MusicUtil.isPlayMusic()) {
            mAlbum.start();
        } else {
            mAlbum.pause();
        }

    }


    public class CDBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MUSIC_NOTIFICATION_ACTION_PLAY:
                    Picasso.with(getContext()).load(MusicUtil.getNowSong().getAlbumUrl())
                            .placeholder(R.drawable.album)
                            .error(R.drawable.album)
                            .into(mAlbum);
                    mAlbum.start();
                    break;
                case MUSIC_NOTIFICATION_ACTION_PAUSE:
                    mAlbum.pause();
                    break;
                case MUSIC_NOTIFICATION_ACTION_CHANGEIMG:
                    Picasso.with(getContext()).load(MusicUtil.getNowSong().getAlbumUrl())
                            .placeholder(R.drawable.album)
                            .error(R.drawable.album)
                            .into(mAlbum);
                    break;
            }
        }
    }

}
