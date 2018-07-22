package com.gin.xjh.shin_music.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gin.xjh.shin_music.bean.Song;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

import java.io.File;

public class BitmapUtil {
    /**
     * 根据专辑ID获取专辑封面图
     *
     * @param song 歌曲
     * @return
     */
    public static Bitmap getAlbumArt(Song song) {
        byte[] imageData = null;
        try {
            MP3File mp3file = new MP3File(new File(song.getUrl()));
            AbstractID3v2Tag tag = mp3file.getID3v2Tag();
            AbstractID3v2Frame frame = (AbstractID3v2Frame) tag.getFrame("APIC");
            FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
            imageData = body.getImageData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (imageData != null) {
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        }
        return null;
//        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
//        mediaMetadataRetriever.setDataSource(song.getUrl());
//        byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
//        if(picture==null)return null;
//        return BitmapFactory.decodeByteArray(picture,0,picture.length);
    }
}
