package com.gin.xjh.shin_music.bean;

import cn.bmob.v3.BmobObject;

public class Follow extends BmobObject {

    private String UserId;
    private User FollowUser;

    public Follow() {

    }

    public Follow(String UserId, User FollowUser) {
        this.UserId = UserId;
        this.FollowUser = FollowUser;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public User getFollowUser() {
        return FollowUser;
    }

    public void setFollowUser(User followUser) {
        FollowUser = followUser;
    }
}
