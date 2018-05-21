package com.gin.xjh.shin_music.service;

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

import com.gin.xjh.shin_music.util.MusicUtil;

public class MusicService extends Service {

    private final String MUSIC_NOTIFICATION_ACTION_PLAY = "MusicNotificaion.To.PLAY";
    private final String MUSIC_NOTIFICATION_ACTION_NEXT = "MusicNotificaion.To.NEXT";
    private final String MUSIC_NOTIFICATION_ACTION_PRE = "MusicNotificaion.To.Pre";
    private final String MUSIC_NOTIFICATION_ACTION_AUTONEXT = "MusicNotificaion.To.AUTONEXT";

    private Intent intent1 = new Intent("com.example.communication.CHANGE");
    private Intent intent2 = new Intent("com.example.communication.LISTCHANGE");

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

    private MusicBroadCast musicBroadCast = null;

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


        musicBroadCast = new MusicBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MUSIC_NOTIFICATION_ACTION_PLAY);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_NEXT);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_PRE);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_AUTONEXT);
        registerReceiver(musicBroadCast, filter);

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
                    sendBroadcast(intent2);
                    break;
                case PLAYORPAUSE:
                    MusicUtil.playorpause();
                    sendBroadcast(intent2);
                    break;
                case PREVIOUSMUSIC:
                    MusicUtil.pre();
                    sendBroadcast(intent2);
                    break;
                case NEXTMUSIC:
                    MusicUtil.next();
                    sendBroadcast(intent2);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        releseWakeLock();
        MusicUtil.clean();
        unregisterReceiver(musicBroadCast);
        super.onDestroy();
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
                    sendBroadcast(intent1);
                    break;
                case MUSIC_NOTIFICATION_ACTION_NEXT:
                    MusicUtil.next();
                    Intent startIntent2 = new Intent(getApplicationContext(), MusicService.class);
                    startIntent2.putExtra("action", MusicService.NEXTMUSIC);
                    startService(startIntent2);
                    sendBroadcast(intent1);
                    break;
                case MUSIC_NOTIFICATION_ACTION_PRE:
                    MusicUtil.pre();
                    Intent startIntent3 = new Intent(getApplicationContext(), MusicService.class);
                    startIntent3.putExtra("action", MusicService.PREVIOUSMUSIC);
                    startService(startIntent3);
                    sendBroadcast(intent1);
                    break;
                case MUSIC_NOTIFICATION_ACTION_AUTONEXT:
                    Intent startIntent4 = new Intent(getApplicationContext(), MusicService.class);
                    startIntent4.putExtra("action", MusicService.AUTONEXTMUSIC);
                    startService(startIntent4);
                    sendBroadcast(intent1);
                    break;
            }
        }
    }
}
