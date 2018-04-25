package com.gin.xjh.shin_music.bean;

import java.io.Serializable;

/**
 * Created by Gin on 2018/4/24.
 */

public class Album implements Serializable {

    private String AlbumName;//专辑名称
    private String AlbumUrl;//专辑图片路径

    public Album(String AlbumName, String AlbumUrl){
        this.AlbumName = AlbumName;
        this.AlbumUrl = AlbumUrl;
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public String getAlbumUrl() {
        return AlbumUrl;
    }

}
