package com.gin.xjh.shin_music.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gin.xjh.shin_music.bean.Song;

public class BaseSQLiteDBHelper extends SQLiteOpenHelper {

    private static final String TABLE = "LocalSongList";//表名
    private static final String SONG_NAME = "SongName";//歌曲名字
    private static final String SONG_ID = "SongId";//歌曲名字
    private static final String SINGER_NAME = "SingerName";//歌手名字
    private static final String ALBUM_NAME = "AlbumName";//专辑名称
    private static final String SONG_TIME = "SongTime";//歌曲时间

    public BaseSQLiteDBHelper(Context context) {
        super(context, TABLE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 判断表是否存在
     *
     * @return
     */
    public boolean tabbleIsExist() {
        boolean result = false;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            String sql = "select count(*) as c from Sqlite_master where type ='table' and name ='" + TABLE.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                result = true;
                cursor.close();
            }

        } catch (Exception e) {

        }
        return result;
    }

    /**
     * 创建表
     */
    public void createTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("create table if not exists " + TABLE + "(" +
                SONG_ID + " integer  primary key," +
                SONG_NAME + " varchar," +
                SINGER_NAME + " varchar," +
                ALBUM_NAME + " varchar," +
                SONG_TIME + " integer);");
    }

    public void insertSong(Song song) {
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        ContentValues contentValues = new ContentValues();
        contentValues.put(SONG_ID, song.getSongId());
        contentValues.put(SONG_NAME, song.getSongName());
        contentValues.put(SINGER_NAME, song.getSingerName());
        contentValues.put(ALBUM_NAME, song.getAlbumName());
        contentValues.put(SONG_TIME, song.getSongTime());
        database.insert(TABLE, null, contentValues);
    }

    public Song getSong(long songid) {
        Song song = null;
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        String sql = "select * from " + TABLE + " where " + SONG_ID + "=" + songid;
        Cursor cursor = database.rawQuery(sql, null);//sql和占位符
        if (cursor != null) {
            while (cursor.moveToNext()) {
                song = new Song();
                song.setSongName(cursor.getString(cursor.getColumnIndex(SONG_NAME)));
                song.setSingerName(cursor.getString(cursor.getColumnIndex(SINGER_NAME)));
                song.setAlbumName(cursor.getString(cursor.getColumnIndex(ALBUM_NAME)));
                song.setSongTime(cursor.getInt(cursor.getColumnIndex(SONG_TIME)));
                song.setSongId(songid);
            }
            cursor.close();
        }
        return song;
    }

}