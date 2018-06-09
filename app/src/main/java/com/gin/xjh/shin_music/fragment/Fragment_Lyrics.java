package com.gin.xjh.shin_music.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gin.xjh.shin_music.Net_Request.getNetMusicLrc;
import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.album_details_Activity;
import com.gin.xjh.shin_music.bean.Album;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.gin.xjh.shin_music.view.LyricView;

/**
 * Created by Gin on 2018/4/23.
 */

public class Fragment_Lyrics extends Fragment implements View.OnClickListener {

    private SeekBar volumeSeekBar;
    private ImageView albumdetails;
    private AudioManager mAudioManager;
    private MyVolumeReceiver myVolumeReceiver;
    private LyricView lyricView;
    private TextView hint;

    public static final String LYRIC_ACTION_CHANGE = "Lyric.To.Change";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyrics, null);
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        myVolumeReceiver = new MyVolumeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        intentFilter.addAction(LYRIC_ACTION_CHANGE);
        broadcastManager.registerReceiver(myVolumeReceiver, intentFilter);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        volumeSeekBar = view.findViewById(R.id.volumeSeekBar);
        albumdetails = view.findViewById(R.id.albumdetails);
        lyricView = view.findViewById(R.id.lyricView);
        hint = view.findViewById(R.id.lyric_hint);

        albumdetails.setOnClickListener(this);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//最大音量
        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(streamMaxVolume);
        volumeSeekBar.setProgress(streamVolume);
    }

    private void initEvent() {
        new getNetMusicLrc().getJson(lyricView, hint);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.albumdetails:
                //调转到专辑
                Song song = MusicUtil.getNowSong();
                if (song != null && song.isOnline()) {
                    Intent intent = new Intent(getContext(), album_details_Activity.class);
                    Bundle bundle = new Bundle();
                    Album album = new Album(song.getAlbumName(), song.getAlbumUrl(), song.getAlbumTime(), song.getAlbumId(), song.getSingerName());
                    bundle.putSerializable("album", album);
                    intent.putExtra("album", bundle);
                    intent.putExtra("isAlbum", true);
                    startActivity(intent);
                }

                break;
        }
    }

    /**
     * 处理音量变化时的界面显示
     *
     * @author long
     */
    public class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "android.media.VOLUME_CHANGED_ACTION":
                    //如果音量发生变化则更改seekbar的位置
                    if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                        int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 当前的媒体音量
                        volumeSeekBar.setProgress(currVolume);
                    }
                    break;
                case LYRIC_ACTION_CHANGE:
                    hint.setVisibility(View.VISIBLE);
                    new getNetMusicLrc().getJson(lyricView, hint);
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(myVolumeReceiver);
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (volumeSeekBar != null) {
            int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 当前的媒体音量
            volumeSeekBar.setProgress(currVolume);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
