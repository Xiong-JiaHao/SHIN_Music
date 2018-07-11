package com.gin.xjh.shin_music.util;

import android.content.SharedPreferences;

import com.gin.xjh.shin_music.bean.Song;
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
     * 保存List和当前歌曲号
     *
     * @param listtag
     * @param datalist
     */
    public static void setDataList(String listtag, List<Song> datalist) {
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
    public static List<Song> getDataList(String tag) {
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

}
