package com.gin.xjh.shin_music.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by Gin on 2018/4/24.
 */

public class Album extends BmobObject implements Serializable {

    private String AlbumName;//专辑名称
    private String AlbumUrl;//专辑图片路径
    private String Singer;//歌手名称
    private Long Times;//发行时间
    private String AlbumId;//专辑id

    public Album(String AlbumName, String AlbumUrl, Long Times, String AlbumId, String Singer) {
        this.AlbumName = AlbumName;
        this.AlbumUrl = AlbumUrl;
        this.Times=Times;
        this.AlbumId=AlbumId;
        this.Singer=Singer;
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public String getAlbumUrl() {
        return AlbumUrl;
    }

    public String getSinger() {
        return Singer;
    }

    public Long getTimes() {
        return Times;
    }

    public String getAlbumId() {
        return AlbumId;
    }

}
