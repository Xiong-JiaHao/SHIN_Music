package com.gin.xjh.shin_music;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class login_menu_Activity extends Activity implements View.OnClickListener {

    private ImageView go_back,User_img,User_Sex;
    private TextView User_Name,User_QQ,User_Sign;
    private LinearLayout edit_user, about, question, updata_password;
    private Button logout;
    private Switch fourg;


    private static int MyrequestCode = 0x110;

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
        updata_password = findViewById(R.id.updata_password);
        fourg = findViewById(R.id.fourg);
    }

    private void initEvent() {
        go_back.setOnClickListener(this);
        User_Name.setOnClickListener(this);
        edit_user.setOnClickListener(this);
        about.setOnClickListener(this);
        question.setOnClickListener(this);
        logout.setOnClickListener(this);
        updata_password.setOnClickListener(this);
        fourg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(login_menu_Activity.this, "Check", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(login_menu_Activity.this, "UnCheck", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                finish();
                break;
            case R.id.User_Name:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(login_menu_Activity.this);
                LayoutInflater inflater1 = LayoutInflater.from(login_menu_Activity.this);
                View viewDialog1 = inflater1.inflate(R.layout.login_layout, null);
                EditText UserId = viewDialog1.findViewById(R.id.UserId);
                EditText UserPassword = viewDialog1.findViewById(R.id.User_Password);
                builder1.setView(viewDialog1);
                builder1.setPositiveButton("登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(login_menu_Activity.this, "login", Toast.LENGTH_SHORT).show();
                    }
                });
                builder1.setNegativeButton("注册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent registerintent = new Intent(login_menu_Activity.this, register_Activity.class);
                        startActivity(registerintent);
                    }
                });
                builder1.create();
                builder1.show();
                break;
            case R.id.edit_user:
                Intent editintent = new Intent(this, updata_Activity.class);
                startActivityForResult(editintent, MyrequestCode);
                break;
            case R.id.updata_password:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(login_menu_Activity.this);
                LayoutInflater inflater2 = LayoutInflater.from(login_menu_Activity.this);
                View viewDialog2 = inflater2.inflate(R.layout.password_validate, null);
                EditText password = viewDialog2.findViewById(R.id.UserPassword);
                builder2.setView(viewDialog2);
                builder2.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean flag = true;
                        if (flag) {
                            Intent intent = new Intent(login_menu_Activity.this, updata_password_Activity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(login_menu_Activity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder2.create();
                builder2.show();
                break;
            case R.id.fourg:
                Toast.makeText(this, "4g", Toast.LENGTH_SHORT).show();
                break;
            case R.id.about:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(login_menu_Activity.this);
                LayoutInflater inflater3 = LayoutInflater.from(login_menu_Activity.this);
                View viewDialog3 = inflater3.inflate(R.layout.about, null);
                builder3.setView(viewDialog3);
                builder3.create();
                builder3.show();
                break;
            case R.id.question:
                Intent questionintent = new Intent(this, add_question.class);
                startActivity(questionintent);
                break;
            case R.id.logout:
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MyrequestCode && resultCode == RESULT_OK) {
            Toast.makeText(this, "更新完成", Toast.LENGTH_SHORT).show();
        }
    }
}
