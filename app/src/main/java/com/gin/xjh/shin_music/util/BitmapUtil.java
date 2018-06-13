package com.gin.xjh.shin_music.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

public class BitmapUtil {
    /**
     * 根据专辑ID获取专辑封面图
     *
     * @param songId 歌曲ID
     * @return
     */
    public static Bitmap getAlbumArt(Context context, Long songId) {
        if (songId != null && songId > 0) {
            FileDescriptor fileDescriptor = null;
            Uri uri = Uri.parse("content://media/external/audio/media/" + songId + "/albumart");
            ParcelFileDescriptor pfd = null;
            try {
                pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (pfd != null) {
                fileDescriptor = pfd.getFileDescriptor();
                return BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
        }
        return null;
    }
}
