package com.gin.xjh.shin_music;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

public class add_question extends BaseActivity implements View.OnClickListener {

    private ImageView go_back;



    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_question);
        initView();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
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
