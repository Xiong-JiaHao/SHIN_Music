package com.gin.xjh.shin_music.bean;


public class LikeSong extends Song {
    private String UserId;

    public LikeSong(String UserId, Song song) {
        super(song);
        this.UserId = UserId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Song getSong() {
        return super.getSong();
    }

}
