package com.gin.xjh.shin_music.activities;

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

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.FragmentAdapter;
import com.gin.xjh.shin_music.adapter.MusicListRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.fragments.FragmentLyrics;
import com.gin.xjh.shin_music.fragments.FragmentMusic;
import com.gin.xjh.shin_music.netrequest.GetNetMusicDetail;
import com.gin.xjh.shin_music.service.MusicService;
import com.gin.xjh.shin_music.user.UserState;
import com.gin.xjh.shin_music.utils.DensityUtil;
import com.gin.xjh.shin_music.utils.MusicUtil;
import com.gin.xjh.shin_music.utils.NetStateUtil;
import com.gin.xjh.shin_music.utils.TimesUtil;
import com.gin.xjh.shin_music.view.LyricView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mGoBack, mChangeStyle, mIcComment, mSetting;
    private ImageView mPreSong, mMusicPlay, mNextSong, mCycleStyle, mSongSheet, mLike;
    private TextView mSongName, mSingerName, mChangeFlag, mNowTime, mEndTime;
    private SeekBar mtTmeSeekbar;
    private ViewPager mFragmentVP;


    private List<Fragment> mFragmentList = new ArrayList<>();
    private FragmentAdapter mAdapter;
    private int mIndex = 0;

    private SongBroadCast mSongBroadCast;
    private LocalBroadcastManager mBroadcastManager;

    private MusicListRecyclerViewAdapter mMusiclistRecyclerViewAdapter;

    private boolean isChange = false;

    public static final String MUSIC_ACTION_CHANGE = "MusicNotificaion.To.Change";

    private static final int UPDATEUI = 200;

    private final long INTERVAL = 500L; //防止连续点击的时间间隔
    private long lastClickTime = 0L; //上一次点击的时间
    private volatile String mLastSongName = null;
    private volatile Long mLastSongId = null;
    private volatile int mLasttime = 0;
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
            if (msg.what == UPDATEUI && !isChange && MusicUtil.isPlayMusic()) {
                String timeStr = null;
                int time = MusicUtil.getPlayTime();
                if(MusicUtil.getNowSong().isOnline()){
                    if (NetStateUtil.getNetWorkState(MusicPlayActivity.this) == NetStateUtil.NO_STATE) {
                        playOrpause();
                        Toast.makeText(MusicPlayActivity.this, "当前网络无法播放", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (NetStateUtil.getNetWorkState(MusicPlayActivity.this) == NetStateUtil.DATA_STATE && UserState.isUse_4G() == false) {
                        playOrpause();
                        Toast.makeText(MusicPlayActivity.this, "请允许4G播放后尝试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (mLasttime == -1) {
                    mLasttime = 0;
                    Toast.makeText(MusicPlayActivity.this, "\"" + MusicUtil.getNowSong().getSongName() + "\"无版权无法播放", Toast.LENGTH_SHORT).show();
                    if (isNext) {
                        nextSong(true);
                    } else {
                        preSong();
                    }
                    return;
                }
                if (mLasttime == time) {
                    if (time == 0) {
                        mLasttime = -1;
                        UIHandler.sendEmptyMessageDelayed(UPDATEUI, 1000);//自己给自己刷新
                    } else {
                        mLasttime = 0;
                        Toast.makeText(MusicPlayActivity.this, "\"" + MusicUtil.getNowSong().getSongName() + "\"无版权无法播放", Toast.LENGTH_SHORT).show();
                        if (isNext) {
                            nextSong(true);
                        } else {
                            preSong();
                        }
                    }
                } else {
                    try {
                        timeStr = TimesUtil.longToString(time, "mm:ss");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mtTmeSeekbar.setProgress(time);
                    mLasttime = time;
                    mNowTime.setText(timeStr);
                    if (time >= MusicUtil.getNowSong().getSongTime()) {
                        nextSong(false);
                        return;
                    }
                    isNext = true;
                    UIHandler.sendEmptyMessageDelayed(UPDATEUI, 1000);//自己给自己刷新
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        mSongBroadCast = new SongBroadCast();
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MUSIC_ACTION_CHANGE);
        mBroadcastManager.registerReceiver(mSongBroadCast, intentFilter);
        initView();
        initEvent();
        changSong();
    }

    private void initView() {
        mGoBack = findViewById(R.id.go_back);
        mChangeStyle = findViewById(R.id.change_style);
        mIcComment = findViewById(R.id.ic_comment);
        mSetting = findViewById(R.id.settings);
        mPreSong = findViewById(R.id.leftto);
        mMusicPlay = findViewById(R.id.music_play);
        mNextSong = findViewById(R.id.rightto);
        mCycleStyle = findViewById(R.id.cycle_style);
        mSongSheet = findViewById(R.id.song_sheet);
        mSongName = findViewById(R.id.Song_Name);
        mSingerName = findViewById(R.id.Singer_Name);
        mChangeFlag = findViewById(R.id.change_flag);
        mNowTime = findViewById(R.id.nowtime);
        mEndTime = findViewById(R.id.endtime);
        mtTmeSeekbar = findViewById(R.id.time_seekbar);
        mFragmentVP = findViewById(R.id.fragment_VP);
        mLike = findViewById(R.id.ilike);
    }

    private void initEvent() {
        mFragmentList.add(new FragmentMusic());
        mFragmentList.add(new FragmentLyrics());
        mIndex = 0;
        mAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragmentList);
        mFragmentVP.setAdapter(mAdapter);

        mGoBack.setOnClickListener(this);
        mChangeStyle.setOnClickListener(this);
        mIcComment.setOnClickListener(this);
        mSetting.setOnClickListener(this);
        mCycleStyle.setOnClickListener(this);
        mPreSong.setOnClickListener(this);
        mMusicPlay.setOnClickListener(this);
        mNextSong.setOnClickListener(this);
        mSongSheet.setOnClickListener(this);
        mLike.setOnClickListener(this);

        MediaPlayer mMediaPlayer = MusicUtil.getMediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {//自动播放完后
                if(MusicUtil.getNowSong().isOnline()){
                    if (NetStateUtil.getNetWorkState(MusicPlayActivity.this) == NetStateUtil.NO_STATE) {
                        Toast.makeText(MusicPlayActivity.this, "当前网络无法播放", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (NetStateUtil.getNetWorkState(MusicPlayActivity.this) == NetStateUtil.DATA_STATE && UserState.isUse_4G() == false) {
                        Toast.makeText(MusicPlayActivity.this, "请允许4G播放后尝试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (MusicUtil.isPlayMusic()) {
                    Song song = MusicUtil.getNowSong();
                    if (!(song.getSongName().equals(mLastSongName) && song.getSongId().equals(mLastSongId))) {
                        Intent startIntent;
                        if (isNext) {
                            startIntent = new Intent(MusicPlayActivity.this, MusicService.class);
                            startIntent.putExtra("action", MusicService.AUTONEXTMUSIC);
                        } else {
                            startIntent = new Intent(MusicPlayActivity.this, MusicService.class);
                            startIntent.putExtra("action", MusicService.PREVIOUSMUSIC);
                        }
                        mLastSongName = song.getSongName();
                        mLastSongId = song.getSongId();
                        startService(startIntent);
                    } else {
                        mLastSongName = song.getSongName();
                        mLastSongId = song.getSongId();
                    }
                }
            }
        });

        mtTmeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MusicUtil.setSeekTo(progress);
                    mLasttime = progress;
                    Intent intent = new Intent(LyricView.LYRIC_ACTION_PLAY);
                    mBroadcastManager.sendBroadcast(intent);
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
                mLasttime = seekBar.getProgress();
                isChange = false;
                //恢复UI刷新
                mLasttime = 0;
                UIHandler.sendEmptyMessage(UPDATEUI);
            }
        });

        if (MusicUtil.isPlayMusic()) {
            mMusicPlay.setImageResource(R.drawable.btn_music_pause);
            //恢复UI刷新
            mLasttime = 0;
            UIHandler.sendEmptyMessage(UPDATEUI);
        }
        switch (MusicUtil.getPlay_state()) {
            case MusicUtil.SINGLE_CYCLE:
                mCycleStyle.setImageResource(R.drawable.btn_single_cycle);
                break;

            case MusicUtil.ORDER_CYCLE:
                mCycleStyle.setImageResource(R.drawable.btn_order_cycle);
                break;

            case MusicUtil.DISORDERLY_CYCLE:
                mCycleStyle.setImageResource(R.drawable.btn_disorderly_cycle);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                Intent intent = new Intent(LyricView.LYRIC_ACTION_PAUSE);
                mBroadcastManager.sendBroadcast(intent);
                finish();
                break;
            case R.id.change_style:
                mIndex ^= 1;
                if (mIndex == 0) {
                    mChangeFlag.setText("词");
                } else {
                    mChangeFlag.setText("CD");
                }
                mFragmentVP.setCurrentItem(mIndex);
                break;
            case R.id.ic_comment:
                if (MusicUtil.getListSize() > 0) {
                    Song song = MusicUtil.getNowSong();
                    if (song != null && song.isOnline()) {
                        if (NetStateUtil.getNetWorkState(MusicPlayActivity.this) == NetStateUtil.DATA_STATE && UserState.isUse_4G() == false) {
                            Toast.makeText(MusicPlayActivity.this, "请允许4G播放后尝试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(getString(R.string.SONG), song);
                        Intent ic_comment_intent = new Intent(this, AllCommentActivity.class);
                        ic_comment_intent.putExtra(getString(R.string.SONG), bundle);
                        startActivity(ic_comment_intent);
                    } else {
                        Toast.makeText(MusicPlayActivity.this, "该歌曲不支持评论功能", Toast.LENGTH_SHORT).show();
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
                    mCycleStyle.setImageResource(R.drawable.btn_single_cycle);
                    Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
                } else if (MusicUtil.getPlay_state() == MusicUtil.ORDER_CYCLE) {
                    mCycleStyle.setImageResource(R.drawable.btn_order_cycle);
                    Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
                } else {
                    mCycleStyle.setImageResource(R.drawable.btn_disorderly_cycle);
                    Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                }
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USER), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.PLAY_STATE),MusicUtil.getPlay_state());
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
                nextSong(true);
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
                if (NetStateUtil.getNetWorkState(MusicPlayActivity.this) == NetStateUtil.NO_STATE) {
                    Toast.makeText(MusicPlayActivity.this, "当前无网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                Song song = MusicUtil.getNowSong();
                if (song == null || !song.isOnline()) {
                    Toast.makeText(this, "当前歌曲无法进行该项操作", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!UserState.getState()) {
                    Toast.makeText(this, "请登录后进行该项操作", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (islike) {
                    islike = false;
                    UserState.removeLikeSong(MusicPlayActivity.this, mLike, song);
                } else {
                    islike = true;
                    UserState.addLikeSong(MusicPlayActivity.this, mLike, song);
                }
                break;
        }
    }

    private void preSong() {
        if (MusicUtil.getListSize() == 0) {
            Toast.makeText(this, "当前列表不存在歌曲，无法播放", Toast.LENGTH_SHORT).show();
            return;
        } else if (!MusicUtil.isPlayMusic()) {
            mMusicPlay.setImageResource(R.drawable.btn_music_pause);
        }
        mLastSongName = MusicUtil.getNowSong().getSongName();
        mLastSongId = MusicUtil.getNowSong().getSongId();
        Intent startIntent2 = new Intent(this, MusicService.class);
        startIntent2.putExtra("action", MusicService.PREVIOUSMUSIC);
        startService(startIntent2);
    }

    private void playOrpause() {
        if (MusicUtil.getListSize() == 0) {
            Toast.makeText(this, "当前列表不存在歌曲，无法播放", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent startIntent1 = new Intent(this, MusicService.class);
        startIntent1.putExtra("action", MusicService.PLAYORPAUSE);
        startService(startIntent1);
        if (!MusicUtil.isPlayMusic()) {
            mMusicPlay.setImageResource(R.drawable.btn_music_pause);
            Intent playintent = new Intent(FragmentMusic.MUSIC_ACTION_PLAY);
            mBroadcastManager.sendBroadcast(playintent);
            playintent = new Intent(LyricView.LYRIC_ACTION_PLAY);
            mBroadcastManager.sendBroadcast(playintent);
            //恢复UI刷新
            mLasttime = 0;
            UIHandler.sendEmptyMessage(UPDATEUI);
        } else {
            mMusicPlay.setImageResource(R.drawable.btn_music_play);
            Intent playintent = new Intent(FragmentMusic.MUSIC_ACTION_PAUSE);
            mBroadcastManager.sendBroadcast(playintent);
            playintent = new Intent(LyricView.LYRIC_ACTION_PAUSE);
            mBroadcastManager.sendBroadcast(playintent);
            //停止UI刷新
            UIHandler.removeMessages(UPDATEUI);
        }
    }

    private void nextSong(boolean flag) {
        if (MusicUtil.getListSize() == 0) {
            Toast.makeText(this, "当前列表不存在歌曲，无法播放", Toast.LENGTH_SHORT).show();
            return;
        } else if (!MusicUtil.isPlayMusic()) {
            mMusicPlay.setImageResource(R.drawable.btn_music_pause);
        }
        mLastSongName = MusicUtil.getNowSong().getSongName();
        mLastSongId = MusicUtil.getNowSong().getSongId();
        mLasttime = 0;
        Intent startIntent3 = new Intent(this, MusicService.class);
        if (flag) {
            startIntent3.putExtra("action", MusicService.NEXTMUSIC);
        } else {
            startIntent3.putExtra("action", MusicService.AUTONEXTMUSIC);
        }
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
        if (islike) {
            like.setText(R.string.LIKE);
        } else {
            like.setText(R.string.UNLIKE);
        }
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetStateUtil.getNetWorkState(MusicPlayActivity.this) == NetStateUtil.NO_STATE) {
                    Toast.makeText(MusicPlayActivity.this, "当前无网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (song != null && song.isOnline() && UserState.getState()) {
                    if (islike) {
                        islike = false;
                        UserState.removeLikeSong(MusicPlayActivity.this, mLike, song);
                        like.setText(R.string.UNLIKE);
                    } else {
                        islike = true;
                        UserState.addLikeSong(MusicPlayActivity.this, mLike, song);
                        like.setText(R.string.LIKE);
                    }
                } else if (song == null || !song.isOnline()) {
                    Toast.makeText(MusicPlayActivity.this, "当前歌曲未拥有此功能", Toast.LENGTH_SHORT).show();
                    bottomDialog.dismiss();
                } else {
                    Toast.makeText(MusicPlayActivity.this, "未登录，无法喜欢歌曲", Toast.LENGTH_SHORT).show();
                    bottomDialog.dismiss();
                }
            }
        });
        ic_comment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发消息告知弹出评论
                if (song != null && song.isOnline()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.SONG), song);
                    Intent ic_comment_intent = new Intent(MusicPlayActivity.this, AllCommentActivity.class);
                    ic_comment_intent.putExtra(getString(R.string.SONG), bundle);
                    startActivity(ic_comment_intent);
                } else {
                    Toast.makeText(MusicPlayActivity.this, "该歌曲不支持评论功能", Toast.LENGTH_SHORT).show();
                }
                bottomDialog.dismiss();
            }
        });
        if (song != null && song.isOnline()) {
            ic_comment2.setTextColor(getResources().getColor(R.color.Check));
            like.setTextColor(getResources().getColor(R.color.Check));
            ic_comment2.setClickable(true);
            like.setClickable(true);
        } else {
            ic_comment2.setTextColor(getResources().getColor(R.color.UnCheck));
            like.setTextColor(getResources().getColor(R.color.UnCheck));
            ic_comment2.setClickable(false);
            like.setClickable(false);
        }
        TextView ic_delete = contentView.findViewById(R.id.ic_delete);
        ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = MusicUtil.getListSize() - 1;
                if (size == 0) {
                    MusicUtil.playorpause();
                    MusicUtil.removeSong(0);
                    Intent intent = new Intent(MusicPlayActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    int num = MusicUtil.getIndex();
                    MusicUtil.removeSong(num);
                    MusicUtil.autonext();
                    Intent Musicintent = new Intent(MusicPlayActivity.MUSIC_ACTION_CHANGE);
                    mBroadcastManager.sendBroadcast(Musicintent);
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
        mMusiclistRecyclerViewAdapter = new MusicListRecyclerViewAdapter(this, MusicUtil.getSongList(), play_style_num);
        music_list_rv.setLayoutManager(new LinearLayoutManager(this));
        music_list_rv.setItemAnimator(new DefaultItemAnimator());//默认动画
        music_list_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        music_list_rv.setAdapter(mMusiclistRecyclerViewAdapter);

        if (MusicUtil.getPlay_state() == MusicUtil.SINGLE_CYCLE) {
            play_style_img.setImageResource(R.drawable.btn_single_cycle);
            play_style_name.setText("单曲循环");
        } else if (MusicUtil.getPlay_state() == MusicUtil.ORDER_CYCLE) {
            play_style_img.setImageResource(R.drawable.btn_order_cycle);
            play_style_name.setText("顺序播放");
        } else {
            play_style_img.setImageResource(R.drawable.btn_disorderly_cycle);
            play_style_name.setText("随机播放");
        }
        play_style_num.setText("" + MusicUtil.getListSize());
        play_style_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.changeType();
                if (MusicUtil.getPlay_state() == MusicUtil.SINGLE_CYCLE) {
                    mCycleStyle.setImageResource(R.drawable.btn_single_cycle);
                    play_style_img.setImageResource(R.drawable.btn_single_cycle);
                    play_style_name.setText("单曲循环");
                    Toast.makeText(MusicPlayActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                } else if (MusicUtil.getPlay_state() == MusicUtil.ORDER_CYCLE) {
                    mCycleStyle.setImageResource(R.drawable.btn_order_cycle);
                    play_style_img.setImageResource(R.drawable.btn_order_cycle);
                    play_style_name.setText("顺序播放");
                    Toast.makeText(MusicPlayActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                } else {
                    mCycleStyle.setImageResource(R.drawable.btn_disorderly_cycle);
                    play_style_img.setImageResource(R.drawable.btn_disorderly_cycle);
                    play_style_name.setText("随机播放");
                    Toast.makeText(MusicPlayActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                }

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USER), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.PLAY_STATE),MusicUtil.getPlay_state());
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
            mNowTime.setText("00:00");
            mSongName.setText("未知");
            mSingerName.setText("未知");
            mtTmeSeekbar.setProgress(0);
        } else {
            mSongName.setText(song.getSongName());
            mSingerName.setText(song.getSingerName());
            islike = false;
            if (UserState.getState()) {
                islike = UserState.isLikeSong(song);
            }
            if (islike) {
                mLike.setImageResource(R.drawable.btn_like_song);
            } else {
                mLike.setImageResource(R.drawable.btn_unlike_song);
            }
            try {
                if (song.getSongTime() == 0) {
                    new GetNetMusicDetail().getJson(this);
                } else {
                    mEndTime.setText(TimesUtil.longToString(song.getSongTime(), "mm:ss"));
                }
                if (MusicUtil.isPlayMusic()) {
                    mtTmeSeekbar.setProgress(MusicUtil.getPlayTime());
                } else {
                    mtTmeSeekbar.setProgress(0);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mtTmeSeekbar.setMax(song.getSongTime());
            Intent playintent = new Intent(FragmentLyrics.LYRIC_ACTION_CHANGE);
            mBroadcastManager.sendBroadcast(playintent);
            if(MusicUtil.isPlayMusic()){
                //恢复UI刷新
                mLasttime = 0;
                UIHandler.sendEmptyMessage(UPDATEUI);
                mMusicPlay.setImageResource(R.drawable.btn_music_pause);
                Intent intent1 = new Intent(FragmentMusic.MUSIC_ACTION_CHANGE);
                mBroadcastManager.sendBroadcast(intent1);
            } else {
                mMusicPlay.setImageResource(R.drawable.btn_music_play);
                //暂停UI刷新
                UIHandler.removeMessages(UPDATEUI);
                Intent intent1 = new Intent(FragmentMusic.MUSIC_ACTION_PAUSE);
                mBroadcastManager.sendBroadcast(intent1);
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
        mBroadcastManager.unregisterReceiver(mSongBroadCast);
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