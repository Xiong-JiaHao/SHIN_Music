package com.gin.xjh.shin_music.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Follow;
import com.gin.xjh.shin_music.bean.LikeSong;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.user.UserState;
import com.gin.xjh.shin_music.util.ListDataSaveUtil;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.gin.xjh.shin_music.util.NetStateUtil;
import com.gin.xjh.shin_music.util.TimesUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginMenuActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mGoBack, mUserImg, mUserSex;
    private TextView mUserName, mUserQQ, mUserSign;
    private LinearLayout mEditUser, mAbout, mQuestion, mUpdataPassword, mToConcernList;
    private Button mLogout;
    private Switch mFourG;


    private static final int EDIT_REQUEST_CODE = 0x110;
    private static final int REGISTER_REQUEST_CODE = 0x111;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_menu_activity);
        initView();
        initEvent();
    }

    private void initView() {
        mGoBack = findViewById(R.id.go_back);
        mUserImg = findViewById(R.id.User_img);
        mUserName = findViewById(R.id.User_Name);
        mUserSex = findViewById(R.id.User_sex);
        mUserQQ = findViewById(R.id.User_QQ);
        mUserSign = findViewById(R.id.User_sign);
        mEditUser = findViewById(R.id.edit_user);
        mAbout = findViewById(R.id.about);
        mQuestion = findViewById(R.id.question);
        mLogout = findViewById(R.id.logout);
        mUpdataPassword = findViewById(R.id.updata_password);
        mFourG = findViewById(R.id.fourg);
        mToConcernList = findViewById(R.id.toConcernList);
    }

    private void initEvent() {
        mFourG.setChecked(UserState.isUse_4G());

        mGoBack.setOnClickListener(this);
        mUserName.setOnClickListener(this);
        mEditUser.setOnClickListener(this);
        mAbout.setOnClickListener(this);
        mQuestion.setOnClickListener(this);
        mLogout.setOnClickListener(this);
        mUpdataPassword.setOnClickListener(this);
        mToConcernList.setOnClickListener(this);
        mFourG.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    List<Song> mSongList = MusicUtil.getSongList();
                    if (mSongList != null) {
                        boolean flag = false;
                        for (Song song : mSongList) {
                            if (song != null && song.isOnline() && NetStateUtil.getNetWorkState(LoginMenuActivity.this) == NetStateUtil.DATA_STATE) {
                                Toast.makeText(LoginMenuActivity.this, "当前歌单中存在在线歌曲，关闭失败", Toast.LENGTH_SHORT).show();
                                mFourG.setChecked(true);
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            return;
                        }
                    }
                }
                UserState.setUse_4G(isChecked);
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("use4G", isChecked);
                editor.commit();
                if (isChecked) {
                    Toast.makeText(LoginMenuActivity.this, "允许4G播放", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginMenuActivity.this, "不允许4G播放", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (UserState.getState()) {
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
                if (!UserState.getState()) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginMenuActivity.this);
                    LayoutInflater inflater1 = LayoutInflater.from(LoginMenuActivity.this);
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
                                                    UserState.Login(user);
                                                    updataLogin();
                                                } else {
                                                    Toast.makeText(LoginMenuActivity.this, "密码错误，请确认后重新输入", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(LoginMenuActivity.this, "未找到该用户名，请核对后重新输入", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    });
                    builder1.setNegativeButton("注册", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent registerintent = new Intent(LoginMenuActivity.this, RegisterActivity.class);
                            startActivityForResult(registerintent, REGISTER_REQUEST_CODE);
                        }
                    });
                    builder1.create();
                    builder1.show();
                }
                break;
            case R.id.edit_user:
                if (UserState.getState()) {
                    Intent editintent = new Intent(this, UpdataActivity.class);
                    startActivityForResult(editintent, EDIT_REQUEST_CODE);
                } else {
                    Toast.makeText(this, "请登录后再进行该项操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.updata_password:
                if (UserState.getState()) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginMenuActivity.this);
                    LayoutInflater inflater2 = LayoutInflater.from(LoginMenuActivity.this);
                    View viewDialog2 = inflater2.inflate(R.layout.password_validate_dialog, null);
                    final EditText Password = viewDialog2.findViewById(R.id.UserPassword);
                    builder2.setView(viewDialog2);
                    builder2.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String str = "";
                            final String password = Password.getText().toString();
                            String userid = UserState.getLoginUser().getUserId();
                            int lena = password.length();
                            int lenb = userid.length();
                            for (int i = 0; i < lena; i++) {
                                str += password.charAt(i) % userid.charAt(i % lenb);
                            }
                            if (UserState.getLoginUser().getPassWord().compareTo(str) == 0) {
                                Intent intent = new Intent(LoginMenuActivity.this, UpdataPasswordActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginMenuActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder2.create();
                    builder2.show();
                } else {
                    Toast.makeText(this, "请登录后再进行该项操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.toConcernList:
                if (UserState.getState()) {
                    Intent intent = new Intent(this, ConcernListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "请登录后再进行该项操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.about:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(LoginMenuActivity.this);
                LayoutInflater inflater3 = LayoutInflater.from(LoginMenuActivity.this);
                View viewDialog3 = inflater3.inflate(R.layout.about_me_layout, null);
                builder3.setView(viewDialog3);
                builder3.create();
                builder3.show();
                break;
            case R.id.question:
                Intent questionintent = new Intent(this, AddQuestionActivity.class);
                startActivity(questionintent);
                break;
            case R.id.logout:
                if (UserState.getState()) {
                    updataLogout();
                    UserState.Logout();
                    Toast.makeText(this, "退出成功", Toast.LENGTH_SHORT).show();
                }
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
        User user = UserState.getLoginUser();
        mUserName.setText(user.getUserName());
        mUserQQ.setText("QQ:" + user.getUserQQ());
        mUserSign.setText("个人简介：" + user.getPersonal_profile());
        mUserSex.setVisibility(View.VISIBLE);
        switch (user.getUserSex()) {
            case 0:
                mUserSex.setImageResource(R.drawable.sel_sex_man);
                break;
            case 1:
                mUserSex.setImageResource(R.drawable.sel_sex_woman);
                break;
            case 2:
                mUserSex.setImageResource(R.drawable.sel_sex_alien);
                break;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", user.getUserId());
        editor.putString("user_name", user.getUserName());
        editor.putString("btn_password", user.getPassWord());
        editor.putString("user_qq", user.getUserQQ());
        editor.putInt("user_sex", user.getUserSex());
        editor.putString("personal_profile", user.getPersonal_profile());
        editor.putString("objId", user.getObjectId());
        editor.putLong("time", TimesUtil.dateToLong(new Date(System.currentTimeMillis())));
        editor.putString("likesonglistname", user.getLikeSongListName());
        editor.putBoolean("public_song",user.isPublic_song());
        editor.commit();
        updateBmobLikeEvent();
        updateBmobConcernEvent();

        mLogout.setVisibility(View.VISIBLE);
    }

    private void updataLogout() {
        mUserName.setText("登录/注册");
        mUserQQ.setText("");
        mUserSign.setText("");
        mUserSex.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", null);
        editor.putString("user_name", null);
        editor.putString("btn_password", null);
        editor.putString("user_qq", null);
        editor.putInt("user_sex", 0);
        editor.putString("personal_profile", null);
        editor.putString("objId", null);
        editor.putLong("time", -1L);
        editor.putString("btn_like_song", null);
        editor.putString("likesonglistname",null);
        editor.putString("concernUser",null);
        editor.putBoolean("public_song",false);
        editor.commit();
        UserState.setLikeSongList(null);
        UserState.setConcernList(null);

        mLogout.setVisibility(View.GONE);
    }

    private void updateBmobLikeEvent() {
        BmobQuery<LikeSong> query = new BmobQuery<>();
        query.addWhereEqualTo("UserId", UserState.getLoginUser().getUserId());//按当前登录的ID进行查找
        query.findObjects(new FindListener<LikeSong>() {
            @Override
            public void done(List<LikeSong> list, BmobException e) {
                if (list != null && list.size() != 0) {
                    Song song;
                    LikeSong likeSong;
                    List<Song> mSong = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        likeSong = list.get(i);
                        song = likeSong.getSong();
                        song.setObjectId(likeSong.getObjectId());
                        mSong.add(song);
                    }
                    UserState.setLikeSongList(mSong);
                    ListDataSaveUtil.setSongList("btn_like_song", mSong);
                } else {
                    UserState.setLikeSongList(null);
                    ListDataSaveUtil.setSongList("btn_like_song", null);
                }
            }
        });

    }

    private void updateBmobConcernEvent() {
        BmobQuery<Follow> query = new BmobQuery<>();
        query.addWhereEqualTo("UserId", UserState.getLoginUser().getUserId());//按当前登录的ID进行查找
        query.include("FollowUser");
        query.findObjects(new FindListener<Follow>() {
            @Override
            public void done(List<Follow> list, BmobException e) {
                if (list != null && list.size() != 0) {
                    User user;
                    Follow concernUser;
                    List<User> concernList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        concernUser = list.get(i);
                        user = concernUser.getFollowUser();
                        user.setObjectId(concernUser.getObjectId());
                        concernList.add(user);
                    }
                    UserState.setConcernList(concernList);
                    ListDataSaveUtil.setUserList("concernUser", concernList);
                } else {
                    UserState.setConcernList(null);
                    ListDataSaveUtil.setUserList("concernUser", null);
                }
            }
        });
    }

}
