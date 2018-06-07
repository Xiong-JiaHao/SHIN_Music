package com.gin.xjh.shin_music;

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

import com.gin.xjh.shin_music.User.User_state;
import com.gin.xjh.shin_music.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class login_menu_Activity extends BaseActivity implements View.OnClickListener {

    private ImageView go_back,User_img,User_Sex;
    private TextView User_Name,User_QQ,User_Sign;
    private LinearLayout edit_user, about, question, updata_password;
    private Button logout;
    private Switch fourg;


    private static int edit_requestCode = 0x110;
    private static int register_requestCode = 0x111;

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
        if (User_state.getState()) {
            updataLogin();
        } else {
            updataLogout();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                finish();
                break;
            case R.id.User_Name:
                if (!User_state.getState()) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(login_menu_Activity.this);
                    LayoutInflater inflater1 = LayoutInflater.from(login_menu_Activity.this);
                    View viewDialog1 = inflater1.inflate(R.layout.login_layout, null);
                    final EditText UserId = viewDialog1.findViewById(R.id.UserId);
                    final EditText UserPassword = viewDialog1.findViewById(R.id.User_Password);
                    builder1.setView(viewDialog1);
                    builder1.setPositiveButton("登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String id = UserId.getText().toString();
                            final String password = UserPassword.getText().toString();
                            BmobQuery<User> query = new BmobQuery<>();
                            query.addWhereEqualTo("UserId", id);
                            query.findObjects(new FindListener<User>() {
                                @Override
                                public void done(List<User> list, BmobException e) {
                                    if (e == null) {
                                        if (list.size() > 0) {
                                            for (User user : list) {
                                                String deciphering = "";
                                                int lena = password.length();
                                                int lenb = id.length();
                                                for (int i = 0; i < lena; i++) {
                                                    deciphering += password.charAt(i) % id.charAt(i % lenb);
                                                }
                                                if (deciphering.compareTo(user.getPassWord()) == 0) {
                                                    //修改全局变量，保存对象，并且刷新界面
                                                    User_state.Login(user);
                                                    updataLogin();
                                                } else {
                                                    Toast.makeText(login_menu_Activity.this, "密码错误，请确认后重新输入", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(login_menu_Activity.this, "未找到该用户名，请核对后重新输入", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    });
                    builder1.setNegativeButton("注册", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent registerintent = new Intent(login_menu_Activity.this, register_Activity.class);
                            startActivityForResult(registerintent, register_requestCode);
                        }
                    });
                    builder1.create();
                    builder1.show();
                }
                break;
            case R.id.edit_user:
                if (User_state.getState()) {
                    Intent editintent = new Intent(this, updata_Activity.class);
                    startActivityForResult(editintent, edit_requestCode);
                } else {
                    Toast.makeText(this, "请登录后再进行该项操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.updata_password:
                if (User_state.getState()) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(login_menu_Activity.this);
                    LayoutInflater inflater2 = LayoutInflater.from(login_menu_Activity.this);
                    View viewDialog2 = inflater2.inflate(R.layout.password_validate, null);
                    final EditText password = viewDialog2.findViewById(R.id.UserPassword);
                    builder2.setView(viewDialog2);
                    builder2.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (User_state.getLoginUser().getPassWord().compareTo(password.getText().toString()) == 0) {
                                Intent intent = new Intent(login_menu_Activity.this, updata_password_Activity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(login_menu_Activity.this, "密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder2.create();
                    builder2.show();
                } else {
                    Toast.makeText(this, "请登录后再进行该项操作", Toast.LENGTH_SHORT).show();
                }
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
                updataLogout();
                User_state.Logout();
                Toast.makeText(this, "退出成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String str = data.getStringExtra("User");
            if (str.compareTo("yes") == 0) {
                updataLogin();
            }
        }
    }

    private void updataLogin() {
        User user = User_state.getLoginUser();
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

    private void updataLogout() {
        User_Name.setText("登录/注册");
        User_QQ.setText("");
        User_Sign.setText("");
        User_Sex.setVisibility(View.GONE);
    }
}
