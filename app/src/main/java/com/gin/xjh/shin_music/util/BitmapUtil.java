package com.gin.xjh.shin_music.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.gin.xjh.shin_music.bean.Song;

public class BitmapUtil {
    /**
     * 根据专辑ID获取专辑封面图
     *
     * @param song 歌曲
     * @return
     */
    public static Bitmap getAlbumArt(Context context, Song song) {
        Uri selectedAudio = Uri.parse(song.getUrl());
        MediaMetadataRetriever myRetriever = new MediaMetadataRetriever();
        myRetriever.setDataSource(context, selectedAudio); // the URI of audio file
        byte[] artwork;

        artwork = myRetriever.getEmbeddedPicture();

        if (artwork != null) {
            Bitmap bMap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);

            return bMap;
        } else {
            return null;
        }
//        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
//        Bitmap bitmap = null;
//        Long albumid = song.getAlbumId();
//        Long songid = song.getSongId();
//        ContentResolver resolver = context.getContentResolver();
//        if (albumid < 0 && songid < 0) {
//            throw new IllegalArgumentException("Must specify an album or song");
//        }
//
//        try {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            FileDescriptor fileDescriptor = null;
//            if (albumid < 0) {
//                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
//                ParcelFileDescriptor parcelFileDescriptor = resolver.openFileDescriptor(uri, "r");
//                if (parcelFileDescriptor != null) {
//                    fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
//                }
//            } else {
//                Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
//                ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r");
//                if (pfd != null) {
//                    fileDescriptor = pfd.getFileDescriptor();
//                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
//                }
//            }
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return bitmap;
    }
}
