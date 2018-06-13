package com.gin.xjh.shin_music.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.gin.xjh.shin_music.bean.Song;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

public class BitmapUtil {
    /**
     * 根据专辑ID获取专辑封面图
     *
     * @param song 歌曲
     * @return
     */
    public static Bitmap getAlbumArt(Context context, Song song) {
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        Bitmap bitmap = null;
        Long album = song.getAlbumId();
        Long songid = song.getSongId();
        ContentResolver resolver = context.getContentResolver();
        if (album < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or song");
        }

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fileDescriptor = null;
            if (album < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor parcelFileDescriptor = resolver.openFileDescriptor(uri, "r");
                if (parcelFileDescriptor != null) {
                    fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, album);
                ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fileDescriptor = pfd.getFileDescriptor();
                }
            }

            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
