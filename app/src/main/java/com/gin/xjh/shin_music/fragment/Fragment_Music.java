package com.gin.xjh.shin_music.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
    public static final String MUSIC_ACTION_CHANGE = "MusicNotificaion.To.CHANGEMUSIC";
    private CDBroadCast cdBroadCast = null;

    private float currentValue = 0f;
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

    }

    private void initEvent() {
        if (MusicUtil.isPlayMusic()) {
            startAnimation();
        } else {
            pauseAnimation();
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
                    Picasso.with(getContext()).load(MusicUtil.getNowSong().getAlbumUrl())
                            .placeholder(R.drawable.album)
                            .error(R.drawable.album)
                            .into(mAlbum);
                    startAnimation();
                    break;
                case MUSIC_ACTION_PAUSE:
                    pauseAnimation();
                    break;
                case MUSIC_ACTION_CHANGE:
                    stopAnimation();
                    startAnimation();
                    break;
            }
        }
    }


    /**
     * 开始动画
     */
    public void startAnimation() {

        // 设置动画，从上次停止位置开始,这里是顺时针旋转360度
        objAnim = ObjectAnimator.ofFloat(mAlbum, "Rotation",
                currentValue - 360, currentValue);
        // 设置持续时间
        objAnim.setDuration(20000);
        //设置插值器
        objAnim.setInterpolator(new LinearInterpolator());
        // 设置循环播放
        objAnim.setRepeatCount(ObjectAnimator.INFINITE);
        // 设置动画监听
        objAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // TODO Auto-generated method stub
                // 监听动画执行的位置，以便下次开始时，从当前位置开始
                currentValue = (Float) animation.getAnimatedValue();

            }
        });
        objAnim.start();
    }

    /**
     * 停止动画
     */
    public void stopAnimation() {
        objAnim.end();
        currentValue = 0;// 重置起始位置
    }

    /**
     * 暂停动画
     */
    public void pauseAnimation() {
        objAnim.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 控件被移除时，取消动画
        objAnim.cancel();
        mAlbum.clearAnimation();// 清除此ImageView身上的动画
    }
}
