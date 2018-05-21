package com.gin.xjh.shin_music.util;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import com.gin.xjh.shin_music.Net_Request.getNetMusicUrl;
import com.gin.xjh.shin_music.bean.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicUtil {

    private static List<Song> SongList = null;//当前音乐播放列表

    private static int index;//当前播放歌曲编号
    private static int listSize;
    private volatile static boolean isPlay = false;//是否正在播放
    private static int play_state = 0;//播放状态

    public static final int ORDER_CYCLE = 0;//顺序播放
    public static final int SINGLE_CYCLE = 1;//单曲循环
    public static final int DISORDERLY_CYCLE = 2;//乱序播放

    private static MediaPlayer mediaPlayer;



    public static List<Song> getSongList() {
        return SongList;
    }

    public static int getIndex() {
        return index;
    }

    public static int getPlay_state() {
        return play_state;
    }

    private static boolean isPlayMusic(){
        return isPlay;
    }

    public static Song getNowSong(){
        if (SongList != null) {
            return SongList.get(index);
        }
        return null;
    }

    public static MediaPlayer getMediaPlayer(){
        if(mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        return mediaPlayer;
    }

    public static void changeType(){
        synchronized (MusicUtil.class){
            play_state++;
            play_state %= 3;
        }
    }

    public static void changeSongList(List<Song> list){
        SongList = list;
        listSize = list.size();
    }

    public static void addSong(Song song){
        if (SongList == null) {
            SongList = new ArrayList<>();
        }
        SongList.add(index,song);
        listSize++;
    }

    public static void removeSong(){
        SongList.remove(index);
        listSize--;
    }

    public static void setSeekTo(int i){
        mediaPlayer.seekTo(i);
    }

    private static void play(){
        isPlay = true;
    }

    private static void pause(){
        isPlay = false;
    }

    public static void playorpause(){
        if(isPlay){
            pause();
        }
        else{
            play();
        }
    }

    public static void clean() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public static void pre(){
        if (play_state == ORDER_CYCLE || play_state == SINGLE_CYCLE) {
            if (index == 0) {
                index = listSize - 1;
            }
            else{
                index--;
            }
        }
        else {
            index = new Random().nextInt(listSize - 1);
        }
    }

    public static void next(){
        if (play_state == ORDER_CYCLE || play_state == SINGLE_CYCLE) {
            index++;
            if (index == listSize) {
                index=0;
            }
        }
        else {
            index=new Random().nextInt(listSize-1);
        }
    }

    public static void autonext(){
        if (play_state == ORDER_CYCLE) {
            index++;
            if (index == listSize) {
                index=0;
            }
        } else if (play_state == DISORDERLY_CYCLE) {
            index=new Random().nextInt(listSize-1);
        }
    }

    public static List<Song> getLocalMusic(Context context){
        List <Song> mSongList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String SongName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String SingerName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String AlbumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String Url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String AlbumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                Song song = new Song(SongName, SingerName, AlbumName, Url);
                song.setSongTime(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                if (song.getSongTime() >= 60000) {
                    mSongList.add(song);
                }
            }
        }
        cursor.close();
        return mSongList;
    }

    public void playMusic(Song song) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try {
            if (song.getUrl() == null) {
                //获取网络歌曲
                new getNetMusicUrl().getJson(song, mediaPlayer);
            } else {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.getUrl());
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

}