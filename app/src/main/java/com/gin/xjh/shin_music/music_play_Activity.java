package com.gin.xjh.shin_music;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.adapter.FragmentAdapter;
import com.gin.xjh.shin_music.fragment.Fragment_Lyrics;
import com.gin.xjh.shin_music.fragment.Fragment_Music;

import java.util.ArrayList;
import java.util.List;

public class music_play_Activity extends AppCompatActivity implements View.OnClickListener {

    private ImageView go_back, change_style, ic_comment, sz;
    private ImageView leftto, music_play, rightto, cycle_style, song_sheet;
    private TextView Song_Name, Singer_Name, change_flag, nowtime, endtime;
    private SeekBar time_seekbar;
    private ViewPager fragment_VP;

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentAdapter adapter;
    private int Index;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play);
        initView();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        change_style = findViewById(R.id.change_style);
        ic_comment = findViewById(R.id.ic_comment);
        sz = findViewById(R.id.sz);
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
        sz.setOnClickListener(this);
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
                Toast.makeText(this, "ic_comment", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sz:
                Toast.makeText(this, "sz", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cycle_style:
                Toast.makeText(this, "cycle_style", Toast.LENGTH_SHORT).show();
                break;
            case R.id.leftto:
                Toast.makeText(this, "leftto", Toast.LENGTH_SHORT).show();
                break;
            case R.id.music_play:
                Toast.makeText(this, "music_play", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rightto:
                Toast.makeText(this, "rightto", Toast.LENGTH_SHORT).show();
                break;
            case R.id.song_sheet:
                Toast.makeText(this, "song_sheet", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
