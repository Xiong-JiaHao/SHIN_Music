package com.gin.xjh.shin_music;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class concernList_Activity extends BaseActivity implements View.OnClickListener {

    private ImageView go_back;
    private RecyclerView concern_rv;
    private TextView hint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.concernlist_layout);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                finish();
                break;
        }
    }
}
