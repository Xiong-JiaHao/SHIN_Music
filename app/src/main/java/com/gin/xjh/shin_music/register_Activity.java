package com.gin.xjh.shin_music;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gin.xjh.shin_music.User.User_state;
import com.gin.xjh.shin_music.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class register_Activity extends BaseActivity implements View.OnClickListener {


    private ImageView go_back, checkid, checkpassword, checkagainpassword;
    private EditText User_Id, User_Name, Password, AgainPassWord, User_QQ, Personal_profile;
    private String user_Id, user_Name, password, againpassword, user_QQ, personal_profile;
    private RadioGroup UserSex;
    private int usersex;
    private Button Submit;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initView();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        User_Id = findViewById(R.id.UserId);
        User_Name = findViewById(R.id.UserName);
        Password = findViewById(R.id.UserPassword);
        AgainPassWord = findViewById(R.id.AgainPassword);
        User_QQ = findViewById(R.id.UserQQ);
        Personal_profile = findViewById(R.id.Personal_profile);
        UserSex = findViewById(R.id.UserSex);
        Submit = findViewById(R.id.submit);
        checkid = findViewById(R.id.checkid);
        checkpassword = findViewById(R.id.checkpassword);
        checkagainpassword = findViewById(R.id.checkagainpassword);
    }

    private void initEvent() {
        go_back.setOnClickListener(this);
        Submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                finish();
                break;
            case R.id.submit:
                if (check()) {
                    final User user = new User(user_Id, user_Name, password, user_QQ, usersex, personal_profile);
                    user.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                User_state.Login(user);
                                Intent intent = new Intent();
                                intent.putExtra("User", "yes");
                                Toast.makeText(register_Activity.this, "注册成功，正在登录...", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(register_Activity.this, "注册失败，请重新注册，如果还失败请联系我们，联系方式详见关于", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }
    }

    private boolean check() {
        user_Id = User_Id.getText().toString();
        password = Password.getText().toString();
        againpassword = AgainPassWord.getText().toString();
        if(user_Id.compareTo("")==0||user_Id.length()==0){
            Toast.makeText(this, "请输入用户名后进行注册", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(password.compareTo("")==0||password.length()==0){
            Toast.makeText(this, "请输入密码后进行注册", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(againpassword.compareTo("")==0||againpassword.length()==0){
            Toast.makeText(this, "请输入确认密码后进行注册", Toast.LENGTH_SHORT).show();
            return false;
        }
        final String[] str = {""};
        checkagainpassword.setVisibility(View.VISIBLE);
        checkpassword.setVisibility(View.VISIBLE);
        checkid.setVisibility(View.VISIBLE);
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("UserId", user_Id);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    str[0] += "用户名已有，请重新选择";
                    User_Id.setText("");
                    checkid.setImageResource(R.drawable.fork);
                }
            }
        });
        if (password.compareTo(againpassword) != 0) {
            str[0] += "\n两次密码不正确";
            Password.setText("");
            AgainPassWord.setText("");
            checkagainpassword.setImageResource(R.drawable.fork);
        }
        if (str[0].compareTo("") == 0) {
            user_QQ = User_QQ.getText().toString();
            personal_profile = Personal_profile.getText().toString();
            user_Name = User_Name.getText().toString();
            switch (UserSex.getCheckedRadioButtonId()) {
                case R.id.man:
                    usersex = 0;
                    break;
                case R.id.woman:
                    usersex = 1;
                    break;
                case R.id.alien:
                    usersex = 2;
                    break;
                default:
                    usersex = 0;
            }
            if (user_QQ.compareTo("") == 0) {
                user_QQ = "未知哦";
            } else if (user_Name.compareTo("") == 0) {
                user_Name = user_Id;
            } else if (personal_profile.compareTo("") == 0) {
                personal_profile = "Ta很神秘，什么都没有写";
            }
            return true;
        } else {
            Toast.makeText(this, str[0], Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
