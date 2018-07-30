package com.gin.xjh.shin_music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.user.UserState;
import com.gin.xjh.shin_music.bean.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UpdataActivity extends BaseActivity implements View.OnClickListener {


    private ImageView go_back;
    private EditText UserName, UserQQ, UserSign;
    private RadioGroup UserSex;
    private RadioButton man, woman, alien;
    private Button submit;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updata_activity);
        initView();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        UserName = findViewById(R.id.UserName);
        UserQQ = findViewById(R.id.UserQQ);
        UserSex = findViewById(R.id.UserSex);
        UserSign = findViewById(R.id.UserSign);
        submit = findViewById(R.id.submit);
        man = findViewById(R.id.man);
        woman = findViewById(R.id.woman);
        alien = findViewById(R.id.alien);
    }

    private void initEvent() {
        go_back.setOnClickListener(this);
        submit.setOnClickListener(this);
        User user = UserState.getLoginUser();
        UserName.setHint(user.getUserName());
        UserQQ.setHint(user.getUserQQ());
        UserSign.setHint(user.getPersonal_profile());
        switch (user.getUserSex()) {
            case 0:
                man.setChecked(true);
                break;
            case 1:
                woman.setChecked(true);
                break;
            case 2:
                alien.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                Intent intent = new Intent();
                intent.putExtra("User", "no");
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.submit:
                boolean flag = false;
                final User user = new User();
                final String userName = UserName.getText().toString();
                String userQQ = UserQQ.getText().toString();
                String userSign = UserSign.getText().toString();
                if (userName.compareTo("") != 0) {
                    flag = true;
                    user.setUserName(userName);
                }
                if (userQQ.compareTo("") != 0) {
                    flag = true;
                    user.setUserQQ(userQQ);
                }
                if (userSign.compareTo("") != 0) {
                    flag = true;
                    user.setPersonal_profile(userSign);
                }
                int sex = 0;
                switch (UserSex.getCheckedRadioButtonId()) {
                    case R.id.woman:
                        sex = 1;
                        break;
                    case R.id.alien:
                        sex = 2;
                        break;
                }
                if (sex != UserState.getLoginUser().getUserSex()) {
                    flag = true;
                    user.setUserSex(sex);
                }
                if (flag) {
                    user.update(UserState.getLoginUser().getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                User upuser = UserState.getLoginUser();
                                if (user.getUserSex() != -1) {
                                    upuser.setUserSex(user.getUserSex());
                                }
                                if (user.getUserName() != null) {
                                    upuser.setUserName(user.getUserName());
                                }
                                if (user.getUserQQ() != null) {
                                    upuser.setUserQQ(user.getUserQQ());
                                }
                                if (user.getPersonal_profile() != null) {
                                    upuser.setPersonal_profile(user.getPersonal_profile());
                                }
                                Intent intent = new Intent();
                                intent.putExtra("User", "yes");
                                setResult(RESULT_OK, intent);
                                Toast.makeText(UpdataActivity.this, "更新完成", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(UpdataActivity.this, "更新失败，请重试，如果还失败请联系我，联系方式详见关于", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "没有修改，请勿点击", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
