package com.gin.xjh.shin_music.fragment;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Song;
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
    public static final String MUSIC_ACTION_CHANGE = "MusicNotificaion.To.CHANGEMUSIC";
    private CDBroadCast cdBroadCast = null;

    private ObjectAnimator objAnim = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, null);
        cdBroadCast = new CDBroadCast();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MUSIC_ACTION_PLAY);
        intentFilter.addAction(MUSIC_ACTION_PAUSE);
        intentFilter.addAction(MUSIC_ACTION_CHANGE);
        broadcastManager.registerReceiver(cdBroadCast,intentFilter);
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
        objAnim = ObjectAnimator.ofFloat(mAlbum, "rotation", 0.0f, 360.0f);
        //设定动画的旋转周期
        objAnim.setDuration(20000);
        //设置动画的插值器，这个为匀速旋转
        objAnim.setInterpolator(new LinearInterpolator());
        //设置动画为无限重复
        objAnim.setRepeatCount(-1);
        //设置动画重复模式
        objAnim.setRepeatMode(ObjectAnimator.RESTART);

    }

    private void initEvent() {
        mAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到专辑
            }
        });
        if (MusicUtil.isPlayMusic()) {
            objAnim.start();
        }
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
                    changeAlbum();
                    if (objAnim.isPaused()) {
                        objAnim.resume();
                    } else {
                        objAnim.start();
                    }
                    break;
                case MUSIC_ACTION_PAUSE:
                    objAnim.pause();
                    break;
                case MUSIC_ACTION_CHANGE:
                    changeAlbum();
                    objAnim.end();
                    objAnim.start();
                    break;
            }
        }
    }

    private void changeAlbum() {
        Song song = MusicUtil.getNowSong();
        if (song.isOnline()) {
            Picasso.with(getContext()).load(song.getAlbumUrl())
                    .placeholder(R.drawable.album)
                    .error(R.drawable.album)
                    .into(mAlbum);
        } else {
            mAlbum.setImageBitmap(getAlbumArt(song.getAlbumId()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 控件被移除时，取消动画
        objAnim.cancel();
        mAlbum.clearAnimation();// 清除此ImageView身上的动画
    }

    /**
     * 根据专辑ID获取专辑封面图
     *
     * @param AlbumId 专辑ID
     * @return
     */
    private Bitmap getAlbumArt(String AlbumId) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = getContext().getContentResolver().query(Uri.parse(mUriAlbums + "/" + AlbumId), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = null;
        }
        return bm;
    }
}
