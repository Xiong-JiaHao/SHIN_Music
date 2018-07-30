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

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.user.UserState;
import com.gin.xjh.shin_music.adapter.ConcernRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Follow;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.util.ListDataSaveUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ConcernListActivity extends BaseActivity implements View.OnClickListener, ConcernRecyclerViewAdapter.IonSlidingViewClickListener {

    private ImageView go_back;
    private RecyclerView concern_rv;
    private TextView hint;
    private List<User> mDate = new ArrayList<>();
    private ConcernRecyclerViewAdapter mConcernRecyclerViewAdapter;
    private volatile int index;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.concernlist_activity);
        initView();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        concern_rv = findViewById(R.id.concern_rv);
        hint = findViewById(R.id.hint);
    }

    private void initEvent() {
        go_back.setOnClickListener(this);
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
        switch (v.getId()){
            case R.id.go_back:
                finish();
                break;
        }
    }

    private void updateBmob() {
        BmobQuery<Follow> query = new BmobQuery<>();
        query.addWhereEqualTo("UserId", UserState.getLoginUser().getUserId());//按当前登录的ID进行查找
        query.include("FollowUser");
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
                    ListDataSaveUtil.setUserList("concernUser", concernList);
                    for (int i = 0; i < concernList.size(); i++) {
                        mDate.add(concernList.get(i));
                    }
                    updateUI();
                } else {
                    UserState.setConcernList(null);
                    ListDataSaveUtil.setUserList("concernUser", null);
                    hint.setText("当前未关注其他人");
                }
            }
        });
    }

    private void updateUI() {
        concern_rv.setLayoutManager(new LinearLayoutManager(this));
        mConcernRecyclerViewAdapter = new ConcernRecyclerViewAdapter(this, mDate);
        concern_rv.setItemAnimator(new DefaultItemAnimator());//默认动画
        concern_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        concern_rv.setAdapter(mConcernRecyclerViewAdapter);
        hint.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, PersonalMenuActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", UserState.getConcernList().get(position));
        intent.putExtra("user", bundle);
        index = position;
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
                    mConcernRecyclerViewAdapter.removeData(index, false);
                } else if (!UserState.isConcern(mDate.get(index))) {
                    mConcernRecyclerViewAdapter.removeData(index, false);
                }
            } else {
                boolean isConcern = data.getBooleanExtra("concern", true);
                if (!isConcern) {
                    mConcernRecyclerViewAdapter.removeData(index, false);
                }
            }
        }
    }
}
