package com.gin.xjh.shin_music.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.gin.xjh.shin_music.music_play_Activity;
import com.gin.xjh.shin_music.util.MusicUtil;

public class MusicService extends Service {


    /**
     * 暂停或者是播放音乐
     */
    public static final String PLAYORPAUSE = "playorpause";
    /**
     * 上一首音乐
     */
    public static final String PREVIOUSMUSIC = "previonsmusic";//上一首
    /**
     * 下一首音乐
     */
    public static final String NEXTMUSIC = "nextmusic";//下一首
    public static final String AUTONEXTMUSIC = "autonextmusic";//自动下一首

    private PowerManager.WakeLock wakeLock = null;//电源锁


    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            if (null != wakeLock) {
                wakeLock.acquire();
                if (wakeLock.isHeld()) {

                } else {
                    Toast.makeText(this, "申请电源锁失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void releseWakeLock() {
        if ((null != wakeLock)) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    public void onCreate() {
        // 初始化通知栏



        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        acquireWakeLock();
        if (intent != null) {
            switch (intent.getStringExtra("action")) {
                case AUTONEXTMUSIC:
                    MusicUtil.autonext();
                    Intent Musicintent1 = new Intent(music_play_Activity.MUSIC_ACTION_CHANGE);
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(Musicintent1);
                    break;
                case PLAYORPAUSE:
                    MusicUtil.playorpause();
                    break;
                case PREVIOUSMUSIC:
                    MusicUtil.pre();
                    Intent Musicintent2 = new Intent(music_play_Activity.MUSIC_ACTION_CHANGE);
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(Musicintent2);
                    break;
                case NEXTMUSIC:
                    MusicUtil.next();
                    Intent Musicintent3 = new Intent(music_play_Activity.MUSIC_ACTION_CHANGE);
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(Musicintent3);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        releseWakeLock();
        MusicUtil.clean();
        super.onDestroy();
    }


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

}
