package com.gin.xjh.shin_music.bean;

import java.io.Serializable;

/**
 * Created by Gin on 2018/4/24.
 */

public class Album implements Serializable {

    private String AlbumName;//专辑名称
    private String AlbumUrl;//专辑图片路径
    private String Singer;//歌手名称
    private String Times;//发行时间
    private long AlbumId;//专辑id

    public Album(String AlbumName, String AlbumUrl,String Times,long AlbumId,String Singer) {
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

    public String getTimes() {
        return Times;
    }

    public long getAlbumId() {
        return AlbumId;
    }

}
