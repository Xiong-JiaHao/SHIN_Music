package com.gin.xjh.shin_music.activity;

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
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.RecommendMusicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Follow;
import com.gin.xjh.shin_music.bean.LikeSong;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.user.UserState;
import com.gin.xjh.shin_music.util.ListDataSaveUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PersonalMenuActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mGoBack, mUserImg, mUserSex, mConcern;
    private TextView mUserName, mUserQQ, mUserSign, mListHint;
    private RecyclerView mRecyclerView;
    private User mUser;
    private List<Song> mSongList = new ArrayList<>();
    private RecommendMusicRecyclerViewAdapter mRecommendmusicRecyclerViewAdapter;
    private boolean isConcern = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mUser = (User) intent.getBundleExtra("user").get("user");
        setContentView(R.layout.personal_menu_activity);
        initView();
        initEvent();
        updateBmobLikeEvent();
    }

    private void initView() {
        mGoBack = findViewById(R.id.go_back);
        mUserImg = findViewById(R.id.User_img);
        mUserName = findViewById(R.id.User_Name);
        mUserSex = findViewById(R.id.User_sex);
        mUserQQ = findViewById(R.id.User_QQ);
        mUserSign = findViewById(R.id.User_sign);
        mConcern = findViewById(R.id.concern);
        mListHint = findViewById(R.id.list_hint);
        mRecyclerView = findViewById(R.id.personal_RecyclerView);
    }

    private void initEvent() {
        mGoBack.setOnClickListener(this);
        mConcern.setOnClickListener(this);

        mUserName.setText(mUser.getUserName());
        mUserQQ.setText("QQ:" + mUser.getUserQQ());
        mUserSign.setText("个人简介：" + mUser.getPersonal_profile());
        mUserSex.setVisibility(View.VISIBLE);
        switch (mUser.getUserSex()) {
            case 0:
                mUserSex.setImageResource(R.drawable.sel_sex_man);
                break;
            case 1:
                mUserSex.setImageResource(R.drawable.sel_sex_woman);
                break;
            case 2:
                mUserSex.setImageResource(R.drawable.sel_sex_alien);
                break;
        }
        if (UserState.getState()) {
            if (UserState.getConcernList() == null) {
                updateBmobConcernEvent();
            } else {
                isConcern = UserState.isConcern(mUser);
                if (isConcern) {
                    mConcern.setImageResource(R.drawable.btn_concern_red);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                Intent intent = new Intent();
                intent.putExtra("btn_about", isConcern);
                setResult(97, intent);
                finish();
                break;
            case R.id.concern:
                if (UserState.getState()) {
                    toConcern();
                } else {
                    Toast.makeText(this, "当前未登录，请登录后重试", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void toConcern() {
        isConcern = !isConcern;
        if (isConcern) {
            mConcern.setImageResource(R.drawable.btn_concern_red);
            UserState.addConcern(this, mConcern, mUser);
        } else {
            mConcern.setImageResource(R.drawable.btn_concern_gray);
            UserState.removeConcern(this, mConcern, mUser);
        }
    }

    private void updateBmobLikeEvent() {
        BmobQuery<LikeSong> query = new BmobQuery<>();
        query.addWhereEqualTo("UserId", mUser.getUserId());
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
                    if (mUser.isPublic_song()) {
                        mListHint.setText("该用户无喜欢歌曲");
                    }
                    else{
                        mListHint.setText("该用户歌单未公开");
                    }
                }
            }
        });

    }

    private void updateBmobConcernEvent() {
        BmobQuery<Follow> query = new BmobQuery<>();
        query.addWhereEqualTo("UserId", UserState.getLoginUser().getUserId());//按当前登录的ID进行查找
        query.include("FollowUser");
        query.findObjects(new FindListener<Follow>() {
            @Override
            public void done(List<Follow> list, BmobException e) {
                if (list != null && list.size() != 0) {
                    User listuser;
                    Follow concernUser;
                    List<User> concernList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        concernUser = list.get(i);
                        listuser = concernUser.getFollowUser();
                        listuser.setObjectId(concernUser.getObjectId());
                        concernList.add(listuser);
                    }
                    UserState.setConcernList(concernList);
                    isConcern = UserState.isConcern(mUser);
                    if (isConcern) {
                        mConcern.setImageResource(R.drawable.btn_concern_red);
                    }
                    ListDataSaveUtil.setUserList("concernUser", concernList);
                } else {
                    UserState.setConcernList(null);
                    ListDataSaveUtil.setUserList("concernUser", null);
                }
            }
        });
    }

    private void updateUI() {
        mListHint.setVisibility(View.GONE);
        if (mSongList != null && mSongList.size() > 0) {
            mRecommendmusicRecyclerViewAdapter = new RecommendMusicRecyclerViewAdapter(this, mSongList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());//默认动画
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mRecyclerView.setAdapter(mRecommendmusicRecyclerViewAdapter);
        } else {
            mListHint.setText("无喜欢歌曲，请添加后查看");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.putExtra("btn_about", isConcern);
        setResult(97, intent);
    }
}
