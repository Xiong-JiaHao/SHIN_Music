package com.gin.xjh.shin_music.bean;

import cn.bmob.v3.BmobObject;

public class User extends BmobObject {

    private String UserId;
    private String UserName;
    private String PassWord;
    private String UserQQ;
    private int UserSex;
    private String Personal_profile;

    public User(String UserId, String UserName, String PassWord, String UserQQ, int UserSex, String Personal_profile) {
        this.UserId = UserId;
        this.UserName = UserName;
        this.PassWord = PassWord;
        this.UserQQ = UserQQ;
        this.UserSex = UserSex;
        this.Personal_profile = Personal_profile;
    }

    public String getUserId() {
        return UserId;
    }

    public String getUserName() {
        return UserName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public String getUserQQ() {
        return UserQQ;
    }

    public int getUserSex() {
        return UserSex;
    }

    public String getPersonal_profile() {
        return Personal_profile;
    }
}
