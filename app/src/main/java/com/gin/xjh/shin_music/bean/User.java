package com.gin.xjh.shin_music.bean;

import cn.bmob.v3.BmobObject;

public class User extends BmobObject {

    private String UserId = null;
    private String UserName = null;
    private String PassWord = null;
    private String UserQQ = null;
    private int UserSex = 0;
    private String Personal_profile = null;
    private boolean Public_song = false;
    private String likeSongListName = null;

    public User() {

    }

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

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public String getUserQQ() {
        return UserQQ;
    }

    public void setUserQQ(String userQQ) {
        UserQQ = userQQ;
    }

    public int getUserSex() {
        return UserSex;
    }

    public void setUserSex(int userSex) {
        UserSex = userSex;
    }

    public String getPersonal_profile() {
        return Personal_profile;
    }

    public void setPersonal_profile(String personal_profile) {
        Personal_profile = personal_profile;
    }

    public boolean isPublic_song() {
        return Public_song;
    }

    public void changPublic_song() {
        Public_song = !Public_song;
    }

    public String getLikeSongListName() {
        return likeSongListName;
    }

    public void setLikeSongListName(String likeSongListName) {
        this.likeSongListName = likeSongListName;
    }
}
