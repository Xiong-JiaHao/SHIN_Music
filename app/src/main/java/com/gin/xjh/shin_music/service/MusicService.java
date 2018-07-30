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
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.gin.xjh.shin_music.activity.MusicPlayActivity;
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

    private Intent changeIntent = new Intent(MusicPlayActivity.MUSIC_ACTION_CHANGE);

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
        musicNotifi.onCreateMusicNotifi();
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
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(changeIntent);
                    break;
                case PLAYORPAUSE:
                    MusicUtil.playorpause();
                    musicNotifi.onUpdataPlayNotifi();
                    break;
                case PREVIOUSMUSIC:
                    MusicUtil.pre();
                    musicNotifi.onUpdataMusicNotifi();
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(changeIntent);
                    break;
                case NEXTMUSIC:
                    MusicUtil.next();
                    musicNotifi.onUpdataMusicNotifi();
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(changeIntent);
                    break;
                case PLAY:
                    MusicUtil.play();
                    musicNotifi.onUpdataMusicNotifi();
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(changeIntent);
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
            if (MusicUtil.getSongList() != null) {
                switch (intent.getAction()) {
                    case MUSIC_NOTIFICATION_ACTION_PLAY:
                        MusicUtil.playorpause();
                        musicNotifi.onUpdataMusicNotifi();
                        LocalBroadcastManager.getInstance(context).sendBroadcast(changeIntent);
                        break;
                    case MUSIC_NOTIFICATION_ACTION_NEXT:
                        MusicUtil.next();
                        musicNotifi.onUpdataMusicNotifi();
                        LocalBroadcastManager.getInstance(context).sendBroadcast(changeIntent);
                        break;
                    case MUSIC_NOTIFICATION_ACTION_PRE:
                        MusicUtil.pre();
                        musicNotifi.onUpdataMusicNotifi();
                        LocalBroadcastManager.getInstance(context).sendBroadcast(changeIntent);
                        break;
                }
            } else {
                Toast.makeText(context, "当前歌单无歌曲，请添加后重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
