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
    private String Url;//路径
    private String AlbumUrl;//专辑图片路径
    private int SongTime = 0;//歌曲时间
    private Long AlbumTime = null;
    private Long AlbumId = null;

    public Song() {

    }

    public Song(String SongName, String SingerName, String AlbumName, String Url) {
        this.SongName = SongName;
        this.SingerName = SingerName;
        this.AlbumName = AlbumName;
        this.Url = Url;
        SongId = -1L;
        AlbumUrl = null;
    }

    public Song(String SongName, Long Songid, String SingerName, Long SingerId, String AlbumName, String AlbumUrl, int SongTime) {
        this.SongName = SongName;
        this.SongId = Songid;
        this.SingerName = SingerName;
        this.SingerId = SingerId;
        this.AlbumName = AlbumName;
        this.AlbumUrl = AlbumUrl;
        this.SongTime = SongTime;
        Url = null;
    }

    public Song(Song song) {
        this.SongName = song.getSongName();
        this.setObjectId(song.getObjectId());
        this.SongId = song.getSongId();
        this.SingerName = song.getSingerName();
        this.SingerId = song.getSingerId();
        this.AlbumName = song.getAlbumName();
        this.AlbumUrl = song.getAlbumUrl();
        this.SongTime = song.getSongTime();
        Url = null;
        this.AlbumTime = song.getAlbumTime();
        this.AlbumId = song.getAlbumId();
    }

    public Song getSong() {
        return this;
    }

    public boolean isOnline() {
        if (Url == null) {
            return true;
        }
        return false;
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

    public String getUrl() {
        return Url;
    }

    public String getAlbumUrl() {
        return AlbumUrl;
    }

    public Long getSingerId() {
        return SingerId;
    }

    public int getSongTime() {
        return SongTime;
    }

    public Long getAlbumTime() {
        return AlbumTime;
    }

    public Long getAlbumId() {
        return AlbumId;
    }

    public void setAlbumId(Long albumId) {
        AlbumId = albumId;
    }

    public void setSongId(Long songId) {
        SongId = songId;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public void setSongTime(int songTime) {
        SongTime = songTime;
    }

    public void setAlbumUrl(String albumUrl) {
        AlbumUrl = albumUrl;
    }

    public void setSingerId(Long singerId) {
        SingerId = singerId;
    }

    public void setAlbumTime(Long albumTime) {
        AlbumTime = albumTime;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public void setSingerName(String singerName) {
        SingerName = singerName;
    }

    public void setAlbumName(String albumName) {
        AlbumName = albumName;
    }

    @Override
    public String toString() {
        return SingerName + " - " + AlbumName;
    }

    @Override
    public boolean equals(Object obj) {
        Song song = (Song) obj;
        if (!song.getSongName().equals(SongName)) {
            return false;
        }
        if (!song.getSongId().equals(SongId)) {
            return false;
        }
        return true;
    }
}
