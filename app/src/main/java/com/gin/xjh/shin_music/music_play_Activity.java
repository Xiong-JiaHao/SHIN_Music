package com.gin.xjh.shin_music;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
    private int Index;

    private SongBroadCast mSongBroadCast;

    private List<Song> mSongList;
    private musiclistRecyclerViewAdapter musiclistRecyclerViewAdapter;


    public static final String MUSIC_ACTION_CHANGE = "MusicNotificaion.To.Change";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);
        mSongList = MusicUtil.getSongList();
        mSongBroadCast = new SongBroadCast();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MUSIC_ACTION_CHANGE);
        broadcastManager.registerReceiver(mSongBroadCast, intentFilter);
        initView();
        changSong();
        initEvent();
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

        time_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (MusicUtil.isPlayMusic()) {
            music_play.setImageResource(R.drawable.music_stop);
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
                fragment_VP.setCurrentItem(Index);
                break;
            case R.id.ic_comment:
                Intent ic_comment_intent = new Intent(this, All_comment.class);
                startActivity(ic_comment_intent);
                break;
            case R.id.settings:
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
                Intent playintent1 = new Intent(Fragment_Music.MUSIC_ACTION_PLAY);
                android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(playintent1);
                Intent startIntent2 = new Intent(this, MusicService.class);
                startIntent2.putExtra("action", MusicService.PREVIOUSMUSIC);
                startService(startIntent2);
                break;
            case R.id.music_play:
                if (MusicUtil.getListSize() == 0) {
                    Toast.makeText(this, "当前列表不存在歌曲，无法播放", Toast.LENGTH_SHORT).show();
                } else if (!MusicUtil.isPlayMusic()) {
                    music_play.setImageResource(R.drawable.music_stop);
                    Intent playintent2 = new Intent(Fragment_Music.MUSIC_ACTION_PLAY);
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(playintent2);
                } else {
                    music_play.setImageResource(R.drawable.music_play);
                    Intent playintent2 = new Intent(Fragment_Music.MUSIC_ACTION_PAUSE);
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(playintent2);
                }
                Intent startIntent1 = new Intent(this, MusicService.class);
                startIntent1.putExtra("action", MusicService.PLAYORPAUSE);
                startService(startIntent1);
                break;
            case R.id.rightto:
                if (!MusicUtil.isPlayMusic()) {
                    music_play.setImageResource(R.drawable.music_stop);
                }
                Intent playintent3 = new Intent(Fragment_Music.MUSIC_ACTION_PLAY);
                android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(playintent3);
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

    private void showSettingsbottomDialog() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        bottomDialog.setCanceledOnTouchOutside(true);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_content_circle_inplay, null);
        TextView ic_comment2 = contentView.findViewById(R.id.ic_comment);
        ic_comment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发消息告知弹出评论
                Intent ic_comment_intent = new Intent(music_play_Activity.this, All_comment.class);
                startActivity(ic_comment_intent);
                bottomDialog.dismiss();
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
    }

    private void showListbottomDialog() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        bottomDialog.setCanceledOnTouchOutside(true);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_content_circle_setting, null);
        RecyclerView music_list_rv = contentView.findViewById(R.id.music_list_rv);
        musiclistRecyclerViewAdapter = new musiclistRecyclerViewAdapter(this, mSongList);
        music_list_rv.setLayoutManager(new LinearLayoutManager(this));
        music_list_rv.setItemAnimator(new DefaultItemAnimator());//默认动画
        music_list_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        music_list_rv.setAdapter(musiclistRecyclerViewAdapter);

        final TextView play_style_name = contentView.findViewById(R.id.play_style_name);
        TextView play_style_num = contentView.findViewById(R.id.play_style_num);
        final ImageView play_style_img = contentView.findViewById(R.id.play_style_img);

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
                if (song.getSongTime() == null) {
                    new getNetMusicDetail().getJson(this);
                } else {
                    endtime.setText(TimesUtil.longToString(song.getSongTime(), "mm:ss"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Intent intent1 = new Intent(Fragment_Music.MUSIC_ACTION_PLAY);
            android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
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
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSongBroadCast);
        super.onDestroy();
    }
}
