package com.gin.xjh.shin_music.bean;

import cn.bmob.v3.BmobObject;

public class LikeSong extends BmobObject {
    private String UserId;
    private Song song;

    public LikeSong(String UserId, Song song) {
        this.UserId = UserId;
        this.song = song;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Song getSong() {
        return song;
    }

}
