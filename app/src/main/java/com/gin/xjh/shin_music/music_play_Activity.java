package com.gin.xjh.shin_music;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import com.gin.xjh.shin_music.User.User_state;
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
    private ImageView leftto, music_play, rightto, cycle_style, song_sheet, ilike;
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

    private final long INTERVAL = 500L; //防止连续点击的时间间隔
    private long lastClickTime = 0L; //上一次点击的时间
    private volatile String lastSongName = null;
    private volatile Long lastSongId = null;
    private volatile int lasttime = 0;
    private volatile boolean isNext = true;
    private volatile boolean islike;

    private boolean filter() {
        long time = System.currentTimeMillis();
        if ((time - lastClickTime) > INTERVAL) {
            lastClickTime = time;
            return false;
        }
        lastClickTime = time;
        return true;
    }

    //实时刷新UI
    private Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATEUI && !isChange) {
                String timeStr = null;
                int time = MusicUtil.getPlayTime();
                if (lasttime == time) {
                    lasttime = 0;
                    Toast.makeText(music_play_Activity.this, "\"" + MusicUtil.getNowSong().getSongName() + "\"无版权无法播放", Toast.LENGTH_SHORT).show();
                    if (isNext) {
                        isNext = true;
                        nextSong();
                    } else {
                        preSong();
                    }
                } else {
                    try {
                        timeStr = TimesUtil.longToString(time, "mm:ss");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    time_seekbar.setProgress(time);
                    lasttime = time;
                    nowtime.setText(timeStr);
                    UIHandler.sendEmptyMessageDelayed(UPDATEUI, 1000);//自己给自己刷新
                }
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
        ilike = findViewById(R.id.ilike);
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
        ilike.setOnClickListener(this);

        MediaPlayer mMediaPlayer = MusicUtil.getMediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {//自动播放完后
                Song song = MusicUtil.getNowSong();
                if(lastSongId==null){
                    lastSongName = song.getSongName();
                    lastSongId = song.getSongId();
                }
                if (!(song.getSongName().equals(lastSongName) && song.getSongId().equals(lastSongId))) {
                    Intent startIntent;
                    if (isNext) {
                        startIntent = new Intent(music_play_Activity.this, MusicService.class);
                        startIntent.putExtra("action", MusicService.AUTONEXTMUSIC);
                    } else {
                        startIntent = new Intent(music_play_Activity.this, MusicService.class);
                        startIntent.putExtra("action", MusicService.PREVIOUSMUSIC);
                    }
                    startService(startIntent);
                    lastSongName = song.getSongName();
                    lastSongId = song.getSongId();
                }
            }
        });

        time_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MusicUtil.setSeekTo(progress);
                    lasttime = progress;
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
                lasttime = seekBar.getProgress();
                isChange = false;
                //恢复UI刷新
                lasttime = 0;
                UIHandler.sendEmptyMessage(UPDATEUI);
            }
        });

        if (MusicUtil.isPlayMusic()) {
            music_play.setImageResource(R.drawable.music_stop);
            //恢复UI刷新
            lasttime = 0;
            UIHandler.sendEmptyMessage(UPDATEUI);
        }
        switch (MusicUtil.getPlay_state()) {
            case MusicUtil.SINGLE_CYCLE:
                cycle_style.setImageResource(R.drawable.single_cycle);
                break;

            case MusicUtil.ORDER_CYCLE:
                cycle_style.setImageResource(R.drawable.order_cycle);
                break;

            case MusicUtil.DISORDERLY_CYCLE:
                cycle_style.setImageResource(R.drawable.disorderly_cycle);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                Intent intent = new Intent(LyricView.LYRIC_ACTION_PAUSE);
                broadcastManager.sendBroadcast(intent);
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
                    Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
                } else if (MusicUtil.getPlay_state() == MusicUtil.ORDER_CYCLE) {
                    cycle_style.setImageResource(R.drawable.order_cycle);
                    Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
                } else {
                    cycle_style.setImageResource(R.drawable.disorderly_cycle);
                    Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                }
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("play_state",MusicUtil.getPlay_state());
                editor.commit();
                break;
            case R.id.leftto:
                if (filter()) {
                    return;
                }
                isNext = false;
                preSong();
                break;
            case R.id.music_play:
                if (filter()) {
                    return;
                }
                playOrpause();
                break;
            case R.id.rightto:
                if (filter()) {
                    return;
                }
                isNext = true;
                nextSong();
                break;
            case R.id.song_sheet:
                if (MusicUtil.getListSize() > 0) {
                    showListbottomDialog();
                }
                break;
            case R.id.ilike:
                if (filter()) {
                    return;
                }
                Song song = MusicUtil.getNowSong();
                if (song == null || !song.isOnline()) {
                    return;
                }
                if (!User_state.getState()) {
                    Toast.makeText(this, "请登录后进行该项操作", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (islike) {
                    User_state.removeLikeSongList(music_play_Activity.this, ilike, song);
                } else {
                    User_state.addLikeSongList(music_play_Activity.this, ilike, song);
                }
                break;
        }
    }

    private void preSong() {
        if (MusicUtil.getListSize() == 0) {
            Toast.makeText(this, "当前列表不存在歌曲，无法播放", Toast.LENGTH_SHORT).show();
            return;
        } else if (!MusicUtil.isPlayMusic()) {
            music_play.setImageResource(R.drawable.music_stop);
        }
        Intent startIntent2 = new Intent(this, MusicService.class);
        startIntent2.putExtra("action", MusicService.PREVIOUSMUSIC);
        startService(startIntent2);
    }

    private void playOrpause() {
        if (MusicUtil.getListSize() == 0) {
            Toast.makeText(this, "当前列表不存在歌曲，无法播放", Toast.LENGTH_SHORT).show();
            return;
        } else if (!MusicUtil.isPlayMusic()) {
            music_play.setImageResource(R.drawable.music_stop);
            Intent playintent = new Intent(Fragment_Music.MUSIC_ACTION_PLAY);
            broadcastManager.sendBroadcast(playintent);
            playintent = new Intent(LyricView.LYRIC_ACTION_PLAY);
            broadcastManager.sendBroadcast(playintent);
            //恢复UI刷新
            lasttime = 0;
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
    }

    private void nextSong() {
        if (MusicUtil.getListSize() == 0) {
            Toast.makeText(this, "当前列表不存在歌曲，无法播放", Toast.LENGTH_SHORT).show();
            return;
        } else if (!MusicUtil.isPlayMusic()) {
            music_play.setImageResource(R.drawable.music_stop);
        }
        lasttime = 0;
        Intent startIntent3 = new Intent(this, MusicService.class);
        startIntent3.putExtra("action", MusicService.NEXTMUSIC);
        startService(startIntent3);
    }

    @SuppressLint("ResourceAsColor")
    private void showSettingsbottomDialog() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        bottomDialog.setCanceledOnTouchOutside(true);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_content_circle_inplay, null);
        TextView ic_comment2 = contentView.findViewById(R.id.ic_comment);
        final TextView like = contentView.findViewById(R.id.like);
        final Song song = MusicUtil.getNowSong();
        if (song.isOnline()) {
            ic_comment2.setTextColor(R.color.Check);
            like.setTextColor(R.color.Check);
        }
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (song != null && song.isOnline() && User_state.getState()) {
                    if (islike) {
                        User_state.removeLikeSongList(music_play_Activity.this, ilike, song);
                        like.setText(R.string.unlike);
                    } else {
                        User_state.addLikeSongList(music_play_Activity.this, ilike, song);
                        like.setText(R.string.like);
                    }
                }
            }
        });
        ic_comment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发消息告知弹出评论
                if (song != null && song.isOnline()) {
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
                    Toast.makeText(music_play_Activity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                } else if (MusicUtil.getPlay_state() == MusicUtil.ORDER_CYCLE) {
                    cycle_style.setImageResource(R.drawable.order_cycle);
                    play_style_img.setImageResource(R.drawable.order_cycle);
                    play_style_name.setText("顺序播放");
                    Toast.makeText(music_play_Activity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                } else {
                    cycle_style.setImageResource(R.drawable.disorderly_cycle);
                    play_style_img.setImageResource(R.drawable.disorderly_cycle);
                    play_style_name.setText("随机播放");
                    Toast.makeText(music_play_Activity.this, "随机播放", Toast.LENGTH_SHORT).show();
                }

                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("play_state",MusicUtil.getPlay_state());
                editor.commit();
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
        params.height = 1000;//使得List的高为1000
        bottomDialog.getWindow().setAttributes(layoutParams);
    }

    private void changSong() {
        Song song = MusicUtil.getNowSong();
        if (song == null) {
            nowtime.setText("00:00");
            Song_Name.setText("未知");
            Singer_Name.setText("未知");
            time_seekbar.setProgress(0);
        } else {
            Song_Name.setText(song.getSongName());
            Singer_Name.setText(song.getSingerName());
            islike = false;
            if (User_state.getState()) {
                islike = User_state.isLikeSong(song);
            }
            if (islike) {
                ilike.setImageResource(R.drawable.likesong);
            } else {
                ilike.setImageResource(R.drawable.unlikesong);
            }
            try {
                if (song.getSongTime() == 0) {
                    new getNetMusicDetail().getJson(this);
                } else {
                    endtime.setText(TimesUtil.longToString(song.getSongTime(), "mm:ss"));
                }
                if (MusicUtil.isPlayMusic()) {
                    time_seekbar.setProgress(MusicUtil.getPlayTime());
                } else {
                    time_seekbar.setProgress(0);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            time_seekbar.setMax(song.getSongTime());
            Intent playintent = new Intent(Fragment_Lyrics.LYRIC_ACTION_CHANGE);
            broadcastManager.sendBroadcast(playintent);
            if(MusicUtil.isPlayMusic()){
                //恢复UI刷新
                lasttime = 0;
                UIHandler.sendEmptyMessage(UPDATEUI);
                music_play.setImageResource(R.drawable.music_stop);
                Intent intent1 = new Intent(Fragment_Music.MUSIC_ACTION_CHANGE);
                broadcastManager.sendBroadcast(intent1);
            } else {
                music_play.setImageResource(R.drawable.music_play);
                //暂停UI刷新
                UIHandler.removeMessages(UPDATEUI);
                Intent intent1 = new Intent(Fragment_Music.MUSIC_ACTION_PAUSE);
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
        //停止UI刷新
        UIHandler.removeMessages(UPDATEUI);
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        changSong();
        super.onRestart();
    }
}