package com.gin.xjh.shin_music.util;

import com.gin.xjh.shin_music.bean.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicUtil {

    private static List<Song> SongList = null;//当前音乐播放列表

    private static int index;//当前播放歌曲编号
    private volatile static boolean isPlay;//是否正在播放
    private static int play_state = 0;//播放状态

    public static final int ORDER_CYCLE = 0;//顺序播放
    public static final int SINGLE_CYCLE = 1;//单曲循环
    public static final int DISORDERLY_CYCLE = 2;//乱序播放



    public static void changeType(){
        synchronized (MusicUtil.class){
            play_state++;
            play_state%=3;
        }
    }

    public static void changeSongList(List<Song> list){
        SongList = list;
    }

    public static void addSong(Song song){
        if(SongList==null){
            SongList=new ArrayList<>();
        }
        SongList.add(index,song);
    }

    public static List<Song> getSongList() {
        return SongList;
    }

    public static int getIndex() {
        return index;
    }

    public static int getPlay_state() {
        return play_state;
    }
}
