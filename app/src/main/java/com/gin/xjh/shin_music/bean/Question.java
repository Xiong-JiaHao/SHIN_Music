package com.gin.xjh.shin_music.bean;

import cn.bmob.v3.BmobObject;

public class Question extends BmobObject {

    private String category;
    private String user;
    private String question;

    public Question(String category, String user, String question) {
        this.category = category;
        this.user = user;
        this.question = question;
    }
}
