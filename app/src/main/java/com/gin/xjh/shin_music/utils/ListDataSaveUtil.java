package com.gin.xjh.shin_music.utils;

import android.content.SharedPreferences;

import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.bean.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ListDataSaveUtil {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static void setPreferences(SharedPreferences preferences) {
        ListDataSaveUtil.preferences = preferences;
    }

    /**
     * 保存List
     *
     * @param listtag
     * @param datalist
     */
    public static void setSongList(final String listtag, final List<Song> datalist) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (null == datalist || datalist.size() <= 0) {
                    editor = preferences.edit();
                    editor.putString(listtag, null);
                    editor.commit();
                    return;
                }

                Gson gson = new Gson();
                //转换成json数据，再保存
                editor = preferences.edit();
                String Json = gson.toJson(datalist);
                editor.putString(listtag, Json);
                editor.commit();
            }
        }).start();
    }

    /**
     * 当前歌曲号
     *
     * @param indextag
     * @param index
     */
    public static void setIndex(String indextag, int index) {
        editor = preferences.edit();
        editor.putInt(indextag, index);
        editor.commit();
    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public static List<Song> getSongList(String tag) {
        List<Song> datalist;
        String Json = preferences.getString(tag, null);
        if (null == Json) {
            return null;
        }

        Gson gson = new Gson();
        datalist = gson.fromJson(Json, new TypeToken<List<Song>>() {

        }.getType());
        return datalist;

    }

    /**
     * 保存List
     *
     * @param listtag
     * @param datalist
     */
    public static void setUserList(final String listtag, final List<User> datalist) {
        if (null == datalist || datalist.size() <= 0) {
            editor = preferences.edit();
            editor.putString(listtag, null);
            editor.commit();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                //转换成json数据，再保存
                editor = preferences.edit();
                String Json = gson.toJson(datalist);
                editor.putString(listtag, Json);
                editor.commit();
            }
        }).start();
    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public static List<User> getUserList(String tag) {
        List<User> datalist;
        String Json = preferences.getString(tag, null);
        if (null == Json) {
            return null;
        }

        Gson gson = new Gson();
        datalist = gson.fromJson(Json, new TypeToken<List<User>>() {

        }.getType());
        return datalist;

    }

}
