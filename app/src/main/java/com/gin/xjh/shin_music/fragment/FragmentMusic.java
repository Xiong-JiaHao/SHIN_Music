package com.gin.xjh.shin_music.fragment;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.activity.AlbumDetailsActivity;
import com.gin.xjh.shin_music.bean.Album;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.util.BitmapUtil;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.gin.xjh.shin_music.view.CDImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by Gin on 2018/4/23.
 */

public class FragmentMusic extends Fragment {

    private CDImageView mAlbum;

    public static final String MUSIC_ACTION_PLAY = "MusicNotificaion.To.PLAY";
    public static final String MUSIC_ACTION_PAUSE = "MusicNotificaion.To.PAUSE";
    public static final String MUSIC_ACTION_CHANGE = "MusicNotificaion.To.CHANGEMUSIC";
    private CDBroadCast mCDBroadCast = null;

    private ObjectAnimator mObjAnim = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, null);
        mCDBroadCast = new CDBroadCast();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MUSIC_ACTION_PLAY);
        intentFilter.addAction(MUSIC_ACTION_PAUSE);
        intentFilter.addAction(MUSIC_ACTION_CHANGE);
        broadcastManager.registerReceiver(mCDBroadCast,intentFilter);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        mAlbum = view.findViewById(R.id.album);
        if (MusicUtil.getListSize() > 0) {
            changeAlbum();
        }

        //设定动画作用于的控件，以及什么动画，旋转的开始角度和结束角度
        mObjAnim = ObjectAnimator.ofFloat(mAlbum, "rotation", 0.0f, 360.0f);
        //设定动画的旋转周期
        mObjAnim.setDuration(20000);
        //设置动画的插值器，这个为匀速旋转
        mObjAnim.setInterpolator(new LinearInterpolator());
        //设置动画为无限重复
        mObjAnim.setRepeatCount(-1);
        //设置动画重复模式
        mObjAnim.setRepeatMode(ObjectAnimator.RESTART);

    }

    private void initEvent() {
        mAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调转到专辑
                Song song = MusicUtil.getNowSong();
                if (song != null && song.isOnline()) {
                    Intent intent = new Intent(getContext(), AlbumDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    Album album = new Album(song.getAlbumName(), song.getAlbumUrl(), song.getAlbumTime(), song.getAlbumId(), song.getSingerName());
                    bundle.putSerializable(getString(R.string.ALBUM), album);
                    intent.putExtra(getString(R.string.ALBUM), bundle);
                    intent.putExtra(getString(R.string.IS_ALBUM), true);
                    startActivity(intent);
                }
            }
        });
        if (MusicUtil.isPlayMusic()) {
            mObjAnim.start();
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mCDBroadCast);
        mAlbum.clearAnimation();
        super.onDestroy();
    }

    public class CDBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MUSIC_ACTION_PLAY:
                    changeAlbum();
                    if (mObjAnim.isPaused()) {
                        mObjAnim.resume();
                    } else {
                        mObjAnim.start();
                    }
                    break;
                case MUSIC_ACTION_PAUSE:
                    mObjAnim.pause();
                    break;
                case MUSIC_ACTION_CHANGE:
                    changeAlbum();
                    mObjAnim.end();
                    mObjAnim.start();
                    break;
            }
        }
    }

    private void changeAlbum() {
        Song song = MusicUtil.getNowSong();
        if (song.isOnline()) {
            if (song.getAlbumUrl() != null) {
                Picasso.get()
                        .load(song.getAlbumUrl())
                        .error(R.drawable.def_album)
                        .into(mAlbum);
            } else {
                mAlbum.setImageResource(R.drawable.def_album);
            }
        } else {
            Bitmap bitmap = BitmapUtil.getAlbumArt(song);
            if (bitmap == null) {
                mAlbum.setImageResource(R.drawable.def_album);
            } else {
                mAlbum.setImageBitmap(bitmap);
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 控件被移除时，取消动画
        mObjAnim.cancel();
        mAlbum.clearAnimation();// 清除此ImageView身上的动画
    }
}
