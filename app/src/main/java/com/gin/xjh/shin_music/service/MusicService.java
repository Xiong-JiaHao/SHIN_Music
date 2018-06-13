package com.gin.xjh.shin_music.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.gin.xjh.shin_music.music_play_Activity;
import com.gin.xjh.shin_music.notification.MusicNotification;
import com.gin.xjh.shin_music.util.MusicUtil;

public class MusicService extends Service {


    /**
     * 暂停或者是播放音乐
     */
    public static final String PLAYORPAUSE = "playorpause";
    public static final String PLAY = "play";
    /**
     * 上一首音乐
     */
    public static final String PREVIOUSMUSIC = "previonsmusic";//上一首
    /**
     * 下一首音乐
     */
    public static final String NEXTMUSIC = "nextmusic";//下一首
    public static final String AUTONEXTMUSIC = "autonextmusic";//自动下一首


    public static final String MUSIC_NOTIFICATION_ACTION_PLAY = "musicnotificaion.To.PLAY";
    public static final String MUSIC_NOTIFICATION_ACTION_NEXT = "musicnotificaion.To.NEXT";
    public static final String MUSIC_NOTIFICATION_ACTION_PRE = "musicnotificaion.To.PRE";

    private PowerManager.WakeLock wakeLock = null;//电源锁


    private MusicBroadCast musicBroadCast = null;
    private MusicNotification musicNotifi = null;

    private Intent changeIntent = new Intent(music_play_Activity.MUSIC_ACTION_CHANGE);

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
        musicNotifi = MusicNotification.getMusicNotification(getApplicationContext());
        musicNotifi.setContext(getBaseContext());
        musicNotifi.setManager((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        musicBroadCast = new MusicBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MUSIC_NOTIFICATION_ACTION_PLAY);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_NEXT);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_PRE);
        registerReceiver(musicBroadCast, filter);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        musicNotifi.onUpdataMusicNotifi();
        return new MusicBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        acquireWakeLock();
        if (intent != null) {
            switch (intent.getStringExtra("action")) {
                case AUTONEXTMUSIC:
                    MusicUtil.autonext();
                    musicNotifi.onUpdataMusicNotifi();
                    Intent Musicintent1 = new Intent(music_play_Activity.MUSIC_ACTION_CHANGE);
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(Musicintent1);
                    break;
                case PLAYORPAUSE:
                    MusicUtil.playorpause();
                    musicNotifi.onUpdataPlayNotifi();
                    break;
                case PREVIOUSMUSIC:
                    MusicUtil.pre();
                    musicNotifi.onUpdataMusicNotifi();
                    Intent Musicintent2 = new Intent(music_play_Activity.MUSIC_ACTION_CHANGE);
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(Musicintent2);
                    break;
                case NEXTMUSIC:
                    MusicUtil.next();
                    musicNotifi.onUpdataMusicNotifi();
                    Intent Musicintent3 = new Intent(music_play_Activity.MUSIC_ACTION_CHANGE);
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(Musicintent3);
                    break;
                case PLAY:
                    MusicUtil.play();
                    Intent Musicintent4 = new Intent(music_play_Activity.MUSIC_ACTION_CHANGE);
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(Musicintent4);
                    musicNotifi.onUpdataMusicNotifi();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        musicNotifi.onCancelMusicNotifi();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        releseWakeLock();
        MusicUtil.clean();
        unregisterReceiver(musicBroadCast);
        musicNotifi.onCancelMusicNotifi();
        super.onDestroy();
    }

    public void changNotifi() {
        musicNotifi.onUpdataMusicNotifi();
    }


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public class MusicBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MUSIC_NOTIFICATION_ACTION_PLAY:
                    Intent startIntent1 = new Intent(getApplicationContext(), MusicService.class);
                    startIntent1.putExtra("action", MusicService.PLAYORPAUSE);
                    startService(startIntent1);
                    sendBroadcast(changeIntent);
                    break;
                case MUSIC_NOTIFICATION_ACTION_NEXT:
                    Intent startIntent2 = new Intent(getApplicationContext(), MusicService.class);
                    startIntent2.putExtra("action", MusicService.NEXTMUSIC);
                    startService(startIntent2);
                    sendBroadcast(changeIntent);
                    break;
                case MUSIC_NOTIFICATION_ACTION_PRE:
                    Intent startIntent3 = new Intent(getApplicationContext(), MusicService.class);
                    startIntent3.putExtra("action", MusicService.PREVIOUSMUSIC);
                    startService(startIntent3);
                    sendBroadcast(changeIntent);
                    break;
            }

        }
    }

}
