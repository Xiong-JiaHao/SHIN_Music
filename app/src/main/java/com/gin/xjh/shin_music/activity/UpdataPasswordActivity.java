package com.gin.xjh.shin_music.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.user.UserState;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UpdataPasswordActivity extends BaseActivity implements View.OnClickListener {


    private ImageView mGoBack;
    private EditText mUserPassword, mAgainPassword;
    private Button mSubmit;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updata_password_activity);
        initView();
        initEvent();
    }

    private void initView() {
        mGoBack = findViewById(R.id.go_back);
        mUserPassword = findViewById(R.id.UserPassword);
        mAgainPassword = findViewById(R.id.AgainPassword);
        mSubmit = findViewById(R.id.submit);
    }

    private void initEvent() {
        mGoBack.setOnClickListener(this);
        mSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                finish();
                break;
            case R.id.submit:
                String password = mUserPassword.getText().toString();
                String againpassword = mAgainPassword.getText().toString();
                if (password.compareTo(againpassword) == 0) {
                    User user = new User();
                    String userid = UserState.getLoginUser().getUserId();
                    password = "";
                    int lena = againpassword.length();
                    int lenb = userid.length();
                    for (int i = 0; i < lena; i++) {
                        password += againpassword.charAt(i) % userid.charAt(i % lenb);
                    }
                    user.setPassWord(password);
                    final String finalPassword = password;
                    user.update(UserState.getLoginUser().getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(UpdataPasswordActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                User users = UserState.getLoginUser();
                                users.setPassWord(finalPassword);
                                finish();
                            } else {
                                Toast.makeText(UpdataPasswordActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "两次密码不正确,请重新输入", Toast.LENGTH_SHORT).show();
                    mUserPassword.setText("");
                    mAgainPassword.setText("");
                }
                break;
        }
    }
}
