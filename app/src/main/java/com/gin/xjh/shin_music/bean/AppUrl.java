package com.gin.xjh.shin_music.bean;

import cn.bmob.v3.BmobObject;

public class AppUrl extends BmobObject {
    private String url;

    public AppUrl(String url) {
        this.url = url;
    }

    public AppUrl(String tableName, String url) {
        super(tableName);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
