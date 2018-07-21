package com.gin.xjh.shin_music.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gin.xjh.shin_music.bean.Song;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.IOException;

public class BitmapUtil {
    /**
     * 根据专辑ID获取专辑封面图
     *
     * @param song 歌曲
     * @return
     */
    public static Bitmap getAlbumArt(Song song) throws InvalidDataException, IOException, UnsupportedTagException {
        Mp3File mp3file = new Mp3File(song.getUrl());
        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            byte[] imageData = id3v2Tag.getAlbumImage();
            if (imageData != null) {
                return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            }
        }
        return null;
    }
}
