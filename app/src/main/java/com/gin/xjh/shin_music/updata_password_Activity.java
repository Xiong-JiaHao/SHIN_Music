package com.gin.xjh.shin_music;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gin.xjh.shin_music.User.User_state;
import com.gin.xjh.shin_music.bean.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class updata_password_Activity extends Activity implements View.OnClickListener {


    private ImageView go_back;
    private EditText UserPassword, AgainPassword;
    private Button submit;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updata_password_layout);
        initView();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        UserPassword = findViewById(R.id.UserPassword);
        AgainPassword = findViewById(R.id.AgainPassword);
        submit = findViewById(R.id.submit);
    }

    private void initEvent() {
        go_back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                finish();
                break;
            case R.id.submit:
                final String password = UserPassword.getText().toString();
                String againpassword = AgainPassword.getText().toString();
                if (password.compareTo(againpassword) == 0) {
                    User user = new User();
                    user.setPassWord(password);
                    user.update(User_state.getLoginUser().getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(updata_password_Activity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                User users = User_state.getLoginUser();
                                users.setPassWord(password);
                                User_state.Login(users);
                                finish();
                            } else {
                                Toast.makeText(updata_password_Activity.this, "更新失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "两次密码不正确,请重新输入", Toast.LENGTH_SHORT).show();
                    UserPassword.setText("");
                    AgainPassword.setText("");
                }
                break;
        }
    }
}
