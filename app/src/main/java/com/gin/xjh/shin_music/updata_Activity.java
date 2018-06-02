package com.gin.xjh.shin_music;

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

import com.gin.xjh.shin_music.User.User_state;
import com.gin.xjh.shin_music.bean.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class updata_Activity extends BaseActivity implements View.OnClickListener {


    private ImageView go_back;
    private EditText UserName, UserQQ, UserSign;
    private RadioGroup UserSex;
    private RadioButton man, woman, alien;
    private Button submit;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updata_layout);
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
        User user = User_state.getLoginUser();
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
                User user = new User();
                final User upuser = User_state.getLoginUser();
                String userName = UserName.getText().toString();
                String userQQ = UserQQ.getText().toString();
                String userSign = UserSign.getText().toString();
                if (userName.compareTo("") != 0) {
                    flag = true;
                    user.setUserName(userName);
                    upuser.setUserName(userName);
                }
                if (userQQ.compareTo("") != 0) {
                    flag = true;
                    user.setUserQQ(userQQ);
                    upuser.setUserQQ(userQQ);
                }
                if (userSign.compareTo("") != 0) {
                    flag = true;
                    user.setPersonal_profile(userSign);
                    upuser.setPersonal_profile(userSign);
                }
                if (flag) {
                    user.update(upuser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                User_state.Login(upuser);
                                Intent intent = new Intent();
                                intent.putExtra("User", "yes");
                                setResult(RESULT_OK, intent);
                                Toast.makeText(updata_Activity.this, "更新完成", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(updata_Activity.this, "更新失败，请重试，如果还失败请联系我，联系方式详见关于", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }
    }
}
