package com.gin.xjh.shin_music.util;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import com.gin.xjh.shin_music.bean.Song;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicUtil {

    private static List<Song> SongList = null;//当前音乐播放列表

    private static int index = 0;//当前播放歌曲编号
    private static int listSize = 0;
    private volatile static boolean isPlay = false;//是否正在播放
    private static int play_state = 0;//播放状态

    public static final int ORDER_CYCLE = 0;//顺序播放
    public static final int SINGLE_CYCLE = 1;//单曲循环
    public static final int DISORDERLY_CYCLE = 2;//乱序播放

    private static MediaPlayer mediaPlayer;

    private static int playTime = 0;


    public static List<Song> getSongList() {
        return SongList;
    }

    public static int getIndex() {
        return index;
    }

    public static int getPlay_state() {
        return play_state;
    }

    public static boolean isPlayMusic() {
        return isPlay;
    }

    public static Song getNowSong(){
        if (SongList != null) {
            return SongList.get(index);
        }
        return null;
    }

    public static int getListSize() {
        return listSize;
    }

    public static MediaPlayer getMediaPlayer(){
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
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
        SongList.add(index + 1, song);
        listSize++;
    }

    public static void removeSong(int num) {
        if(listSize==1){
            listSize = 0;
            SongList = null;
            return;
        }
        SongList.remove(num);
        listSize--;
    }

    public static void setSeekTo(int i){
        mediaPlayer.seekTo(i);
    }

    public static void setIndex(int i) {
        index = i;
        playTime = 0;
    }

    public static int getPlayTime() {
        return mediaPlayer.getCurrentPosition();
    }

    public static int getSumTime() {
        return mediaPlayer.getDuration();
    }

    public static void play() {
        isPlay = true;
        playMusic(SongList.get(index));
        //还原暂停播放
        setSeekTo(playTime);
    }

    private static void pause(){
        isPlay = false;
        stopMusic();
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
        mediaPlayer.reset();
        mediaPlayer.release();
    }

    public static void pre(){
        playTime = 0;
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
        play();
    }

    public static void next(){
        playTime = 0;
        if (play_state == ORDER_CYCLE || play_state == SINGLE_CYCLE) {
            index++;
            if (index == listSize) {
                index = 0;
            }
        }
        else {
            index = new Random().nextInt(listSize-1);
        }
        play();
    }

    public static void autonext(){
        playTime = 0;
        if (play_state == ORDER_CYCLE) {
            index++;
            if (index == listSize) {
                index = 0;
            }
        } else if (play_state == DISORDERLY_CYCLE) {
            index = new Random().nextInt(listSize-1);
        }
        play();
    }

    public static List<Song> getLocalMusic(Context context){
        List <Song> mSongList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String SongName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String SingerName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String AlbumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String Url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    Song song = new Song(SongName, SingerName, AlbumName, Url);
                    song.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                    song.setSongTime(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                    if (cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)) >= 800000 && new File(song.getUrl()).exists()) {
                        mSongList.add(song);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return mSongList;
    }

    private static void playMusic(Song song) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try {
            if (song.getUrl() == null) {
                //获取网络歌曲
                mediaPlayer.reset();
                mediaPlayer.setDataSource(Constant.MUSIC_URL + song.getSongId() + Constant.SUFFIX_MP3);
                mediaPlayer.prepare();
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


    private static void stopMusic() {
        playTime = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
    }

}