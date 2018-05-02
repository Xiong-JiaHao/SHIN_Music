package com.gin.xjh.shin_music;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class login_menu_Activity extends Activity implements View.OnClickListener {

    private ImageView go_back,User_img,User_Sex;
    private TextView User_Name,User_QQ,User_Sign;
    private LinearLayout edit_user,about,question;
    private Button logout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_menu);
        initView();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        User_img = findViewById(R.id.User_img);
        User_Name = findViewById(R.id.User_Name);
        User_Sex = findViewById(R.id.User_sex);
        User_QQ = findViewById(R.id.User_QQ);
        User_Sign = findViewById(R.id.User_sign);
        edit_user = findViewById(R.id.edit_user);
        about = findViewById(R.id.about);
        question = findViewById(R.id.question);
        logout = findViewById(R.id.logout);
    }

    private void initEvent() {
        go_back.setOnClickListener(this);
        User_Name.setOnClickListener(this);
        edit_user.setOnClickListener(this);
        about.setOnClickListener(this);
        question.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                finish();
                break;
            case R.id.User_Name:
                Toast.makeText(this, "User_Name", Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_user:
                Toast.makeText(this, "edit_user", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fourg:
                Toast.makeText(this, "4g", Toast.LENGTH_SHORT).show();
                break;
            case R.id.about:
                AlertDialog.Builder builder = new AlertDialog.Builder(login_menu_Activity.this);
                LayoutInflater inflater = LayoutInflater.from(login_menu_Activity.this);
                View viewDialog = inflater.inflate(R.layout.about, null);
                builder.setView(viewDialog);
                builder.create();
                builder.show();
                break;
            case R.id.question:
                Intent intent = new Intent(this,add_question.class);
                startActivity(intent);
                break;
            case R.id.logout:
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
