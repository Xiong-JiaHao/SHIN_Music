package com.gin.xjh.shin_music;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gin.xjh.shin_music.adapter.recommendmusicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.LikeSong;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class personal_menu_Activity extends BaseActivity implements View.OnClickListener {

    private ImageView go_back,User_img,User_Sex;
    private TextView User_Name,User_QQ,User_Sign,list_hint;
    private RecyclerView recyclerView;
    private User user;
    private List<Song> mSongList = new ArrayList<>();
    private recommendmusicRecyclerViewAdapter mRecommendmusicRecyclerViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        user = (User) intent.getBundleExtra("user").get("user");
        setContentView(R.layout.personal_menu);
        initView();
        initEvent();
        updateBmobLikeEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        User_img = findViewById(R.id.User_img);
        User_Name = findViewById(R.id.User_Name);
        User_Sex = findViewById(R.id.User_sex);
        User_QQ = findViewById(R.id.User_QQ);
        User_Sign = findViewById(R.id.User_sign);
        list_hint = findViewById(R.id.list_hint);
        recyclerView = findViewById(R.id.personal_RecyclerView);
    }

    private void initEvent() {
        go_back.setOnClickListener(this);

        User_Name.setText(user.getUserName());
        User_QQ.setText("QQ:" + user.getUserQQ());
        User_Sign.setText("个人简介：" + user.getPersonal_profile());
        User_Sex.setVisibility(View.VISIBLE);
        switch (user.getUserSex()) {
            case 0:
                User_Sex.setImageResource(R.drawable.man);
                break;
            case 1:
                User_Sex.setImageResource(R.drawable.woman);
                break;
            case 2:
                User_Sex.setImageResource(R.drawable.alien);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                finish();
                break;


        }
    }

    private void updateBmobLikeEvent() {
        BmobQuery<LikeSong> query = new BmobQuery<>();
        query.addWhereEqualTo("UserId", user.getUserId());
        query.findObjects(new FindListener<LikeSong>() {
            @Override
            public void done(List<LikeSong> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    Song song;
                    LikeSong likeSong;
                    for (int i = 0; i < list.size(); i++) {
                        likeSong = list.get(i);
                        song = likeSong.getSong();
                        mSongList.add(song);
                    }
                    updateUI();
                } else {
                    if(user.isPublic_song()){
                        list_hint.setText("该用户无喜欢歌曲");
                    }
                    else{
                        list_hint.setText("该用户歌单未公开");
                    }
                }
            }
        });

    }

    private void updateUI() {
        list_hint.setVisibility(View.GONE);
        if (mSongList != null && mSongList.size() > 0) {
            mRecommendmusicRecyclerViewAdapter = new recommendmusicRecyclerViewAdapter(this, mSongList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());//默认动画
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(mRecommendmusicRecyclerViewAdapter);
        } else {
            list_hint.setText("无喜欢歌曲，请添加后查看");
        }
    }
}
