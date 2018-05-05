package com.gin.xjh.shin_music.bean;

import java.io.Serializable;

/**
 * Created by Gin on 2018/4/24.
 */

public class Song implements Serializable {

    private String SongName;//歌曲名字
    private String SongId;//歌曲id
    private String SingerName;//歌手名字
    private String AlbumName;//专辑名称
    private String Uri;//路径
    private String AlbumUrl;//专辑图片路径

    public Song(String SongName, String SingerName, String AlbumName, String Uri) {
        this.SongName = SongName;
        this.SingerName = SingerName;
        this.AlbumName = AlbumName;
        this.Uri = Uri;
        SongId = "-1";
        AlbumUrl = "-1";
    }

    public Song(String SongName, String Songid, String SingerName, String AlbumName, String Uri, String AlbumUrl) {
        this.SongName = SongName;
        this.SongId = Songid;
        this.SingerName = SingerName;
        this.AlbumName = AlbumName;
        this.Uri = Uri;
        this.AlbumUrl = AlbumUrl;
    }

    public boolean isOnline() {
        if (SongId.compareTo("-1") == 0) {
            return false;
        }
        return true;
    }

    public String getSongName() {
        return SongName;
    }

    public String getSongId() {
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

    @Override
    public String toString() {
        return SingerName + " - " + AlbumName;
    }
}
