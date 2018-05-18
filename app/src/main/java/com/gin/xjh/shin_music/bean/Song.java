package com.gin.xjh.shin_music.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by Gin on 2018/4/24.
 */

public class Song extends BmobObject implements Serializable {

    private String SongName;//歌曲名字
    private Long SongId;//歌曲id
    private String SingerName;//歌手名字
    private Long SingerId;//歌手ID
    private String AlbumName;//专辑名称
    private String Uri;//路径
    private String AlbumUrl;//专辑图片路径

    public Song(String SongName, String SingerName, String AlbumName, String Url) {
        this.SongName = SongName;
        this.SingerName = SingerName;
        this.AlbumName = AlbumName;
        this.Uri = Url;
        SongId = -1L;
        AlbumUrl = null;
    }

    public Song(String SongName, Long Songid, String SingerName, Long SingerId, String AlbumName, String AlbumUrl) {
        this.SongName = SongName;
        this.SongId = Songid;
        this.SingerName = SingerName;
        this.SingerId = SingerId;
        this.AlbumName = AlbumName;
        this.AlbumUrl = AlbumUrl;
        Uri = null;
    }

    public boolean isOnline() {
        if (SongId.compareTo(-1L) == 0) {
            return false;
        }
        return true;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getSongName() {
        return SongName;
    }

    public Long getSongId() {
        return SongId;
    }

    public String getSingerName() {
        return SingerName;
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public String getUri() {
        return Uri;
    }

    public String getAlbumUrl() {
        return AlbumUrl;
    }

    public Long getSingerId() {
        return SingerId;
    }

    public void setSingerId(Long singerId) {
        SingerId = singerId;
    }

    @Override
    public String toString() {
        return SingerName + " - " + AlbumName;
    }
}
