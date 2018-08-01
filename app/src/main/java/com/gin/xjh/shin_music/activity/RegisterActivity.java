package com.gin.xjh.shin_music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.user.UserState;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {


    private ImageView mGoBack, mCheckid, mCheckPassword, mCheckagAinPassword;
    private EditText mUserId, mUserName, mPassword, mAgainPassWord, mUserQQ, mPersonalProfile;
    private String mUserIdStr, mUserNameStr, mPasswordStr, mAgainPasswordStr, mUserQQStr, mPersonalProfileStr;
    private RadioGroup mUserSex;
    private int mUserSexIndex;
    private Button mSubmit;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initView();
        initEvent();
    }

    private void initView() {
        mGoBack = findViewById(R.id.go_back);
        mUserId = findViewById(R.id.UserId);
        mUserName = findViewById(R.id.UserName);
        mPassword = findViewById(R.id.UserPassword);
        mAgainPassWord = findViewById(R.id.AgainPassword);
        mUserQQ = findViewById(R.id.UserQQ);
        mPersonalProfile = findViewById(R.id.Personal_profile);
        mUserSex = findViewById(R.id.UserSex);
        mSubmit = findViewById(R.id.submit);
        mCheckid = findViewById(R.id.checkid);
        mCheckPassword = findViewById(R.id.checkpassword);
        mCheckagAinPassword = findViewById(R.id.checkagainpassword);
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
                submit();
                break;
        }
    }

    private void submit() {
        mUserIdStr = mUserId.getText().toString();
        mPasswordStr = mPassword.getText().toString();
        mAgainPasswordStr = mAgainPassWord.getText().toString();
        if (mUserIdStr.compareTo("") == 0 || mUserIdStr.length() == 0) {
            Toast.makeText(this, "请输入用户名后进行注册", Toast.LENGTH_SHORT).show();
            return;
        } else if (mPasswordStr.compareTo("") == 0 || mPasswordStr.length() == 0) {
            Toast.makeText(this, "请输入密码后进行注册", Toast.LENGTH_SHORT).show();
            return;
        } else if (mAgainPasswordStr.compareTo("") == 0 || mAgainPasswordStr.length() == 0) {
            Toast.makeText(this, "请输入确认密码后进行注册", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] str = {""};
        mCheckagAinPassword.setVisibility(View.VISIBLE);
        mCheckPassword.setVisibility(View.VISIBLE);
        mCheckid.setVisibility(View.VISIBLE);
        if (mPasswordStr.compareTo(mAgainPasswordStr) != 0) {
            str[0] += "\n两次密码不正确";
            mPassword.setText("");
            mAgainPassWord.setText("");
            mCheckagAinPassword.setImageResource(R.drawable.sel_fork);
        }
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("UserId", mUserIdStr);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        str[0] += "用户名已有，请重新选择";
                        mUserId.setText("");
                        mCheckid.setImageResource(R.drawable.sel_fork);
                    }
                    if (str[0] == "" && str[0].length() == 0) {
                        mUserQQStr = mUserQQ.getText().toString();
                        mPersonalProfileStr = mPersonalProfile.getText().toString();
                        mUserNameStr = mUserName.getText().toString();
                        switch (mUserSex.getCheckedRadioButtonId()) {
                            case R.id.man:
                                mUserSexIndex = 0;
                                break;
                            case R.id.woman:
                                mUserSexIndex = 1;
                                break;
                            case R.id.alien:
                                mUserSexIndex = 2;
                                break;
                            default:
                                mUserSexIndex = 0;
                        }
                        if (mUserQQStr == "" || mUserQQStr.length() == 0) {
                            mUserQQStr = "未知哦";
                        }
                        if (mUserNameStr.length() == 0 || mUserNameStr == "") {
                            mUserNameStr = mUserIdStr;
                        }
                        if (mPersonalProfileStr.length() == 0 || mPersonalProfileStr == "") {
                            mPersonalProfileStr = "Ta很神秘，什么都没有写";
                        }
                        mPasswordStr = "";
                        int lena = mAgainPasswordStr.length();
                        int lenb = mUserIdStr.length();
                        for (int i = 0; i < lena; i++) {
                            mPasswordStr += mAgainPasswordStr.charAt(i) % mUserIdStr.charAt(i % lenb);
                        }
                        register();
                    } else {
                        Toast.makeText(RegisterActivity.this, str[0], Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void register() {
        final User user = new User(mUserIdStr, mUserNameStr, mPasswordStr, mUserQQStr, mUserSexIndex, mPersonalProfileStr);
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    user.setObjectId(s);
                    UserState.Login(user);
                    Intent intent = new Intent();
                    intent.putExtra("User", "yes");
                    Toast.makeText(RegisterActivity.this, "注册成功，正在登录...", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败，请重新注册，如果还失败请联系我们，联系方式详见关于", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
