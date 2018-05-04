package com.gin.xjh.shin_music;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gin.xjh.shin_music.adapter.commentRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Comment;

import java.util.ArrayList;
import java.util.List;

public class All_comment extends Activity implements View.OnClickListener {

    private ImageView go_back, write_comment;
    private RecyclerView comment_rv;

    private List<Comment> mCommentList = null;
    private commentRecyclerViewAdapter mCommentRecyclerViewAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_comment);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        write_comment = findViewById(R.id.write_comment);
        comment_rv = findViewById(R.id.comment_rv);
    }

    private void initData() {
        if (mCommentList == null) {
            mCommentList = new ArrayList<>();
        } else {
            mCommentList.clear();
        }
        for (int i = 0; i < 20; i++) {
            mCommentList.add(new Comment("ginshin", "ginshin", "hhhhhhhhhhhhhhhhhhhhhhhhhhh", "2018-5-4 18:06"));
        }
    }

    private void initEvent() {
        go_back.setOnClickListener(this);
        write_comment.setOnClickListener(this);

        mCommentRecyclerViewAdapter = new commentRecyclerViewAdapter(this, mCommentList);
        comment_rv.setLayoutManager(new LinearLayoutManager(this));
        comment_rv.setItemAnimator(new DefaultItemAnimator());//默认动画
        comment_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        comment_rv.setAdapter(mCommentRecyclerViewAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                finish();
                break;
            case R.id.write_comment:
                AlertDialog.Builder builder = new AlertDialog.Builder(All_comment.this);
                LayoutInflater inflater = LayoutInflater.from(All_comment.this);
                View viewDialog = inflater.inflate(R.layout.add_comment, null);
                EditText Personal_profile = viewDialog.findViewById(R.id.Personal_profile);
                builder.setView(viewDialog);
                builder.setTitle("添加评论(100字以内)：");
                builder.setPositiveButton("提交评论", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(All_comment.this, "add", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create();
                builder.show();
                break;
        }
    }
}
