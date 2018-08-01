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

    private PowerManager.WakeLock mWakeLock = null;//电源锁


    private MusicBroadCast mMusicBroadCast = null;
    private MusicNotification mMusicNotifi = null;

    private Intent mChangeIntent = new Intent(MusicPlayActivity.MUSIC_ACTION_CHANGE);

    private void acquireWakeLock() {
        if (null == mWakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            if (null != mWakeLock) {
                mWakeLock.acquire();
                if (mWakeLock.isHeld()) {

                } else {
                    Toast.makeText(this, "申请电源锁失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void releseWakeLock() {
        if ((null != mWakeLock)) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public void onCreate() {
        // 初始化通知栏
        mMusicNotifi = MusicNotification.getMusicNotification(getApplicationContext());
        mMusicNotifi.setContext(getBaseContext());
        mMusicNotifi.setManager((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        mMusicBroadCast = new MusicBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MUSIC_NOTIFICATION_ACTION_PLAY);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_NEXT);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_PRE);
        registerReceiver(mMusicBroadCast, filter);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mMusicNotifi.onCreateMusicNotifi();
        return new MusicBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        acquireWakeLock();
        if (intent != null) {
            switch (intent.getStringExtra("action")) {
                case AUTONEXTMUSIC:
                    MusicUtil.autonext();
                    mMusicNotifi.onUpdataMusicNotifi();
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(mChangeIntent);
                    break;
                case PLAYORPAUSE:
                    MusicUtil.playorpause();
                    mMusicNotifi.onUpdataPlayNotifi();
                    break;
                case PREVIOUSMUSIC:
                    MusicUtil.pre();
                    mMusicNotifi.onUpdataMusicNotifi();
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(mChangeIntent);
                    break;
                case NEXTMUSIC:
                    MusicUtil.next();
                    mMusicNotifi.onUpdataMusicNotifi();
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(mChangeIntent);
                    break;
                case PLAY:
                    MusicUtil.play();
                    mMusicNotifi.onUpdataMusicNotifi();
                    android.support.v4.content.LocalBroadcastManager.getInstance(this).sendBroadcast(mChangeIntent);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMusicNotifi.onCancelMusicNotifi();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        releseWakeLock();
        MusicUtil.clean();
        unregisterReceiver(mMusicBroadCast);
        mMusicNotifi.onCancelMusicNotifi();
        super.onDestroy();
    }

    public void changNotifi() {
        mMusicNotifi.onUpdataMusicNotifi();
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
                        mMusicNotifi.onUpdataMusicNotifi();
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mChangeIntent);
                        break;
                    case MUSIC_NOTIFICATION_ACTION_NEXT:
                        MusicUtil.next();
                        mMusicNotifi.onUpdataMusicNotifi();
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mChangeIntent);
                        break;
                    case MUSIC_NOTIFICATION_ACTION_PRE:
                        MusicUtil.pre();
                        mMusicNotifi.onUpdataMusicNotifi();
                        LocalBroadcastManager.getInstance(context).sendBroadcast(mChangeIntent);
                        break;
                }
            } else {
                Toast.makeText(context, "当前歌单无歌曲，请添加后重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
