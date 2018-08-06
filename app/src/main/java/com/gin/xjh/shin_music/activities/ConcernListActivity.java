package com.gin.xjh.shin_music.activities;

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

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.ConcernRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Follow;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.user.UserState;
import com.gin.xjh.shin_music.utils.ListDataSaveUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ConcernListActivity extends BaseActivity implements View.OnClickListener, ConcernRecyclerViewAdapter.IonSlidingViewClickListener {

    private ImageView mGoBack;
    private RecyclerView mConcernRv;
    private TextView mHint;
    private List<User> mDate = new ArrayList<>();
    private ConcernRecyclerViewAdapter mConcernRecyclerViewAdapter;
    private volatile int mIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concernlist);
        initView();
        initEvent();
    }

    private void initView() {
        mGoBack = findViewById(R.id.go_back);
        mConcernRv = findViewById(R.id.concern_rv);
        mHint = findViewById(R.id.hint);
    }

    private void initEvent() {
        mGoBack.setOnClickListener(this);
        List<User> users = UserState.getConcernList();
        if (users == null || users.size() == 0) {
            updateBmob();
        } else {
            for (int i = 0; i < users.size(); i++) {
                mDate.add(users.get(i));
            }
            updateUI();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                finish();
                break;
        }
    }

    private void updateBmob() {
        BmobQuery<Follow> query = new BmobQuery<>();
        query.addWhereEqualTo(getString(R.string.USERID), UserState.getLoginUser().getUserId());//按当前登录的ID进行查找
        query.include(getString(R.string.FOLLOW_USER));
        query.findObjects(new FindListener<Follow>() {
            @Override
            public void done(List<Follow> list, BmobException e) {
                if (list != null && list.size() != 0) {
                    User user;
                    Follow concernUser;
                    List<User> concernList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        concernUser = list.get(i);
                        user = concernUser.getFollowUser();
                        user.setObjectId(concernUser.getObjectId());
                        concernList.add(user);
                    }
                    UserState.setConcernList(concernList);
                    ListDataSaveUtil.setUserList(getString(R.string.CONCERN_USER), concernList);
                    for (int i = 0; i < concernList.size(); i++) {
                        mDate.add(concernList.get(i));
                    }
                    updateUI();
                } else {
                    UserState.setConcernList(null);
                    ListDataSaveUtil.setUserList(getString(R.string.CONCERN_USER), null);
                    mHint.setText("当前未关注其他人");
                }
            }
        });
    }

    private void updateUI() {
        mConcernRv.setLayoutManager(new LinearLayoutManager(this));
        mConcernRecyclerViewAdapter = new ConcernRecyclerViewAdapter(this, mDate);
        mConcernRv.setItemAnimator(new DefaultItemAnimator());//默认动画
        mConcernRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mConcernRv.setAdapter(mConcernRecyclerViewAdapter);
        mHint.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, PersonalMenuActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.USER), UserState.getConcernList().get(position));
        intent.putExtra(getString(R.string.USER), bundle);
        mIndex = position;
        startActivityForResult(intent, 97);
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        mConcernRecyclerViewAdapter.removeData(position, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 97) {
            if (data == null) {
                if (UserState.getConcernList() == null) {
                    mConcernRecyclerViewAdapter.removeData(mIndex, false);
                } else if (!UserState.isConcern(mDate.get(mIndex))) {
                    mConcernRecyclerViewAdapter.removeData(mIndex, false);
                }
            } else {
                boolean isConcern = data.getBooleanExtra(getString(R.string.ABOUT), true);
                if (!isConcern) {
                    mConcernRecyclerViewAdapter.removeData(mIndex, false);
                }
            }
        }
    }
}
