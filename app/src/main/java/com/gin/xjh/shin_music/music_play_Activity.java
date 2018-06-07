package com.gin.xjh.shin_music;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.Net_Request.getNetMusicDetail;
import com.gin.xjh.shin_music.adapter.FragmentAdapter;
import com.gin.xjh.shin_music.adapter.musiclistRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.fragment.Fragment_Lyrics;
import com.gin.xjh.shin_music.fragment.Fragment_Music;
import com.gin.xjh.shin_music.service.MusicService;
import com.gin.xjh.shin_music.util.DensityUtil;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.gin.xjh.shin_music.util.TimesUtil;
import com.gin.xjh.shin_music.view.LyricView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class music_play_Activity extends AppCompatActivity implements View.OnClickListener {

    private ImageView go_back, change_style, ic_comment, setting;
    private ImageView leftto, music_play, rightto, cycle_style, song_sheet;
    private TextView Song_Name, Singer_Name, change_flag, nowtime, endtime;
    private SeekBar time_seekbar;
    private ViewPager fragment_VP;


    private List<Fragment> fragments = new ArrayList<>();
    private FragmentAdapter adapter;
    private int Index = 0;

    private SongBroadCast mSongBroadCast;
    private LocalBroadcastManager broadcastManager;

    private musiclistRecyclerViewAdapter musiclistRecyclerViewAdapter;

    private boolean isChange = false;


    public static final String MUSIC_ACTION_CHANGE = "MusicNotificaion.To.Change";

    private static final int UPDATEUI = 200;

    //实时刷新UI
    private Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATEUI && !isChange) {
                String timeStr = null;
                int time = MusicUtil.getPlayTime();
                try {
                    timeStr = TimesUtil.longToString(time, "mm:ss");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                time_seekbar.setProgress(time);
                nowtime.setText(timeStr);
                UIHandler.sendEmptyMessageDelayed(UPDATEUI, 1000);//自己给自己刷新
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);
        mSongBroadCast = new SongBroadCast();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MUSIC_ACTION_CHANGE);
        broadcastManager.registerReceiver(mSongBroadCast, intentFilter);
        initView();
        initEvent();
        changSong();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        change_style = findViewById(R.id.change_style);
        ic_comment = findViewById(R.id.ic_comment);
        setting = findViewById(R.id.settings);
        leftto = findViewById(R.id.leftto);
        music_play = findViewById(R.id.music_play);
        rightto = findViewById(R.id.rightto);
        cycle_style = findViewById(R.id.cycle_style);
        song_sheet = findViewById(R.id.song_sheet);
        Song_Name = findViewById(R.id.Song_Name);
        Singer_Name = findViewById(R.id.Singer_Name);
        change_flag = findViewById(R.id.change_flag);
        nowtime = findViewById(R.id.nowtime);
        endtime = findViewById(R.id.endtime);
        time_seekbar = findViewById(R.id.time_seekbar);
        fragment_VP = findViewById(R.id.fragment_VP);
    }

    private void initEvent() {
        fragments.add(new Fragment_Music());
        fragments.add(new Fragment_Lyrics());
        Index = 0;
        adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        fragment_VP.setAdapter(adapter);

        go_back.setOnClickListener(this);
        change_style.setOnClickListener(this);
        ic_comment.setOnClickListener(this);
        setting.setOnClickListener(this);
        cycle_style.setOnClickListener(this);
        leftto.setOnClickListener(this);
        music_play.setOnClickListener(this);
        rightto.setOnClickListener(this);
        song_sheet.setOnClickListener(this);

        MediaPlayer mMediaPlayer = MusicUtil.getMediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent startIntent = new Intent(music_play_Activity.this, MusicService.class);
                startIntent.putExtra("action", MusicService.AUTONEXTMUSIC);
                startService(startIntent);
            }
        });

        time_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MusicUtil.setSeekTo(progress);
                    Intent intent = new Intent(LyricView.LYRIC_ACTION_PLAY);
                    broadcastManager.sendBroadcast(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isChange = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(!MusicUtil.isPlayMusic()){
                    MusicUtil.playorpause();
                    MusicUtil.setSeekTo(seekBar.getProgress());
                    MusicUtil.playorpause();
                }
                else{
                    MusicUtil.setSeekTo(seekBar.getProgress());
                }
                isChange = false;
                //恢复UI刷新
                UIHandler.sendEmptyMessage(UPDATEUI);
            }
        });

        if (MusicUtil.isPlayMusic()) {
            music_play.setImageResource(R.drawable.music_stop);
            //恢复UI刷新
            UIHandler.sendEmptyMessage(UPDATEUI);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                finish();
                break;
            case R.id.change_style:
                Index ^= 1;
                if (Index == 0) {
                    change_flag.setText("词");
                } else {
                    change_flag.setText("CD");
                }
                fragment_VP.setCurrentItem(Index);
                break;
            case R.id.ic_comment:
                if (MusicUtil.getListSize() > 0) {
                    Song song = MusicUtil.getNowSong();
                    if (song == null || song.isOnline()) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("song", song);
                        Intent ic_comment_intent = new Intent(this, All_comment.class);
                        ic_comment_intent.putExtra("song", bundle);
                        startActivity(ic_comment_intent);
                    } else {
                        Toast.makeText(music_play_Activity.this, "该歌曲不支持评论功能", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.settings:
                if (MusicUtil.getListSize() > 0)
                    showSettingsbottomDialog();
                break;
            case R.id.cycle_style:
                MusicUtil.changeType();
                if (MusicUtil.getPlay_state() == MusicUtil.SINGLE_CYCLE) {
                    cycle_style.setImageResource(R.drawable.single_cycle);
                } else if (MusicUtil.getPlay_state() == MusicUtil.ORDER_CYCLE) {
                    cycle_style.setImageResource(R.drawable.order_cycle);
                } else {
                    cycle_style.setImageResource(R.drawable.disorderly_cycle);
                }
                break;
            case R.id.leftto:
                if (!MusicUtil.isPlayMusic()) {
                    music_play.setImageResource(R.drawable.music_stop);
                }
                Intent startIntent2 = new Intent(this, MusicService.class);
                startIntent2.putExtra("action", MusicService.PREVIOUSMUSIC);
                startService(startIntent2);
                break;
            case R.id.music_play:
                if (MusicUtil.getListSize() == 0) {
                    Toast.makeText(this, "当前列表不存在歌曲，无法播放", Toast.LENGTH_SHORT).show();
                    break;
                } else if (!MusicUtil.isPlayMusic()) {
                    music_play.setImageResource(R.drawable.music_stop);
                    Intent playintent = new Intent(Fragment_Music.MUSIC_ACTION_PLAY);
                    broadcastManager.sendBroadcast(playintent);
                    playintent = new Intent(LyricView.LYRIC_ACTION_PLAY);
                    broadcastManager.sendBroadcast(playintent);
                    //恢复UI刷新
                    UIHandler.sendEmptyMessage(UPDATEUI);
                } else {
                    music_play.setImageResource(R.drawable.music_play);
                    Intent playintent = new Intent(Fragment_Music.MUSIC_ACTION_PAUSE);
                    broadcastManager.sendBroadcast(playintent);
                    playintent = new Intent(LyricView.LYRIC_ACTION_PAUSE);
                    broadcastManager.sendBroadcast(playintent);
                    //停止UI刷新
                    UIHandler.removeMessages(UPDATEUI);
                }
                Intent startIntent1 = new Intent(this, MusicService.class);
                startIntent1.putExtra("action", MusicService.PLAYORPAUSE);
                startService(startIntent1);
                break;
            case R.id.rightto:
                if (!MusicUtil.isPlayMusic()) {
                    music_play.setImageResource(R.drawable.music_stop);
                }
                Intent startIntent3 = new Intent(this, MusicService.class);
                startIntent3.putExtra("action", MusicService.NEXTMUSIC);
                startService(startIntent3);
                break;
            case R.id.song_sheet:
                if (MusicUtil.getListSize() > 0) {
                    showListbottomDialog();
                }
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void showSettingsbottomDialog() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        bottomDialog.setCanceledOnTouchOutside(true);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_content_circle_inplay, null);
        TextView ic_comment2 = contentView.findViewById(R.id.ic_comment);
        final Song song = MusicUtil.getNowSong();
        if (song.isOnline()) {
            ic_comment2.setTextColor(R.color.Check);
        }
        ic_comment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发消息告知弹出评论
                if (song == null || song.isOnline()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("song", song);
                    Intent ic_comment_intent = new Intent(music_play_Activity.this, All_comment.class);
                    ic_comment_intent.putExtra("song", bundle);
                    startActivity(ic_comment_intent);
                } else {
                    Toast.makeText(music_play_Activity.this, "该歌曲不支持评论功能", Toast.LENGTH_SHORT).show();
                }
                bottomDialog.dismiss();
            }
        });
        TextView ic_delete = contentView.findViewById(R.id.ic_delete);
        ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = MusicUtil.getListSize() - 1;
                if (size == 0) {
                    MusicUtil.playorpause();
                    MusicUtil.removeSong(0);
                    Intent intent = new Intent(music_play_Activity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    int num = MusicUtil.getIndex();
                    MusicUtil.removeSong(num);
                    MusicUtil.setIndex(num - 1);
                    MusicUtil.autonext();
                    Intent Musicintent = new Intent(music_play_Activity.MUSIC_ACTION_CHANGE);
                    broadcastManager.sendBroadcast(Musicintent);
                    bottomDialog.dismiss();
                }
            }
        });
        TextView ic_singer = contentView.findViewById(R.id.ic_singer);
        TextView ic_album = contentView.findViewById(R.id.ic_album);
        ic_album.setText(song.getAlbumName());
        ic_singer.setText(song.getSingerName());
        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(this, 16f);
        params.bottomMargin = DensityUtil.dp2px(this, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
    }

    private void showListbottomDialog() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        bottomDialog.setCanceledOnTouchOutside(true);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_content_circle_setting, null);

        final TextView play_style_name = contentView.findViewById(R.id.play_style_name);
        TextView play_style_num = contentView.findViewById(R.id.play_style_num);
        final ImageView play_style_img = contentView.findViewById(R.id.play_style_img);

        RecyclerView music_list_rv = contentView.findViewById(R.id.music_list_rv);
        musiclistRecyclerViewAdapter = new musiclistRecyclerViewAdapter(this, MusicUtil.getSongList(), play_style_num);
        music_list_rv.setLayoutManager(new LinearLayoutManager(this));
        music_list_rv.setItemAnimator(new DefaultItemAnimator());//默认动画
        music_list_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        music_list_rv.setAdapter(musiclistRecyclerViewAdapter);

        if (MusicUtil.getPlay_state() == MusicUtil.SINGLE_CYCLE) {
            play_style_img.setImageResource(R.drawable.single_cycle);
            play_style_name.setText("单曲循环");
        } else if (MusicUtil.getPlay_state() == MusicUtil.ORDER_CYCLE) {
            play_style_img.setImageResource(R.drawable.order_cycle);
            play_style_name.setText("顺序播放");
        } else {
            play_style_img.setImageResource(R.drawable.disorderly_cycle);
            play_style_name.setText("随机播放");
        }
        play_style_num.setText("" + MusicUtil.getListSize());
        play_style_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.changeType();
                if (MusicUtil.getPlay_state() == MusicUtil.SINGLE_CYCLE) {
                    cycle_style.setImageResource(R.drawable.single_cycle);
                    play_style_img.setImageResource(R.drawable.single_cycle);
                    play_style_name.setText("单曲循环");
                } else if (MusicUtil.getPlay_state() == MusicUtil.ORDER_CYCLE) {
                    cycle_style.setImageResource(R.drawable.order_cycle);
                    play_style_img.setImageResource(R.drawable.order_cycle);
                    play_style_name.setText("顺序播放");
                } else {
                    cycle_style.setImageResource(R.drawable.disorderly_cycle);
                    play_style_img.setImageResource(R.drawable.disorderly_cycle);
                    play_style_name.setText("随机播放");
                }
            }
        });

        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(this, 16f);
        params.bottomMargin = DensityUtil.dp2px(this, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        WindowManager.LayoutParams layoutParams = bottomDialog.getWindow().getAttributes();
        params.height = 1000;
        bottomDialog.getWindow().setAttributes(layoutParams);
    }

    private void changSong() {
        Song song = MusicUtil.getNowSong();
        if (song == null) {
            Song_Name.setText("未知");
            Singer_Name.setText("未知");

        } else {
            Song_Name.setText(song.getSongName());
            Singer_Name.setText(song.getSingerName());
            try {
                if (song.getSongTime() == 0) {
                    new getNetMusicDetail().getJson(this);
                } else {
                    endtime.setText(TimesUtil.longToString(song.getSongTime(), "mm:ss"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            time_seekbar.setProgress(MusicUtil.getPlayTime());
            time_seekbar.setMax(MusicUtil.getSumTime());
            Intent playintent = new Intent(Fragment_Lyrics.LYRIC_ACTION_CHANGE);
            broadcastManager.sendBroadcast(playintent);
            if(MusicUtil.isPlayMusic()){
                Intent intent1 = new Intent(Fragment_Music.MUSIC_ACTION_CHANGE);
                broadcastManager.sendBroadcast(intent1);
            }
        }
    }

    public class SongBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MUSIC_ACTION_CHANGE:
                    changSong();
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止UI刷新
        UIHandler.removeMessages(UPDATEUI);
    }

    @Override
    protected void onDestroy() {
        broadcastManager.unregisterReceiver(mSongBroadCast);
        super.onDestroy();
        //停止UI刷新
        UIHandler.removeMessages(UPDATEUI);
    }

    @Override
    protected void onRestart() {
        changSong();
        super.onRestart();
    }
}
