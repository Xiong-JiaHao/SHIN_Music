package com.gin.xjh.shin_music.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.RemoteViews;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.activities.MusicPlayActivity;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.service.MusicService;
import com.gin.xjh.shin_music.utils.BitmapUtil;
import com.gin.xjh.shin_music.utils.MusicUtil;
import com.squareup.picasso.Picasso;


public class MusicNotification extends Notification {

    private static MusicNotification mNotifyInstance = null;
    private Notification mMusicNotifi = null;
    private final int NOTIFICATION_ID = 100;

    private Context mContext;
    private final int REQUEST_CODE = 0;

    private NotificationManager mManager = null;
    private Builder mBuilder;

    private RemoteViews mRemoteViews;
    private Intent mPlay, mNext, mPre;


    public void setContext(Context context){
        this.mContext =context;
    }
    public void setManager(NotificationManager manager) {
        this.mManager = manager;
    }

    private MusicNotification (Context context){
        this.mContext = context;
        // 初始化操作
        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout_music);
        mBuilder = new Builder(context);
        mPlay = new Intent();
        mPlay.setAction(MusicService.MUSIC_NOTIFICATION_ACTION_PLAY);
        mNext = new Intent();
        mNext.setAction(MusicService.MUSIC_NOTIFICATION_ACTION_NEXT);
        mPre = new Intent();
        mPre.setAction(MusicService.MUSIC_NOTIFICATION_ACTION_PRE);

    }

    public static MusicNotification getMusicNotification(Context context){
        if (mNotifyInstance == null) {
            mNotifyInstance = new MusicNotification(context);
        }
        return mNotifyInstance;
    }

    public void onCreateMusicNotifi() {
        // 设置点击事件

        // 1.注册控制点击事件

        PendingIntent pplay = PendingIntent.getBroadcast(mContext, REQUEST_CODE, mPlay, PendingIntent.FLAG_UPDATE_CURRENT);//因为每次更新UI都会重建所以设置flag无效
        mRemoteViews.setOnClickPendingIntent(R.id.notigication_playorpaues, pplay);

        // 2.注册下一首点击事件

        PendingIntent pnext = PendingIntent.getBroadcast(mContext, REQUEST_CODE, mNext, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notigication_next, pnext);

        // 3.注册上一首点击事件

        PendingIntent ppre = PendingIntent.getBroadcast(mContext, REQUEST_CODE, mPre, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notigication_pre, ppre);

        //4.设置点击事件（调转到播放界面）
        Intent intent = new Intent(mContext, MusicPlayActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContent(mRemoteViews)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setOngoing(true)//必须手动代码清除
                .setSmallIcon(R.drawable.notification_icon);//设置下拉图标

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setCategory(Notification.CATEGORY_PROGRESS)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = mContext.getString(R.string.GINSHIN);
            String channelName = mContext.getString(R.string.SHIN_MUSIC);
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);//取消提示
            channel.setSound(null, null);//取消提示音
            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);

            mBuilder.setChannelId(channelID);
            mMusicNotifi = mBuilder.build();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mMusicNotifi = mBuilder.getNotification();
        } else {
            mMusicNotifi = mBuilder.build();
        }
        mMusicNotifi.flags = Notification.FLAG_ONGOING_EVENT;
        mManager.notify(NOTIFICATION_ID, mMusicNotifi);
    }

    public void onUpdataMusicNotifi() {
        Song song = MusicUtil.getNowSong();
        // 设置添加内容
        if (song==null){
            mRemoteViews.setTextViewText(R.id.notigication_songName,"未知");
            mRemoteViews.setTextViewText(R.id.notigication_singer,"未知");
            mRemoteViews.setImageViewResource(R.id.notigication_album,R.drawable.def_album);
        }
        else {
            mRemoteViews.setTextViewText(R.id.notigication_songName, song.getSongName());
            mRemoteViews.setTextViewText(R.id.notigication_singer, song.getSingerName());

            if(song.isOnline()){
                if (song.getAlbumUrl() != null) {
                    Picasso.get()
                            .load(song.getAlbumUrl())
                            .error(R.drawable.def_album)
                            .into(mRemoteViews, R.id.notigication_album, NOTIFICATION_ID, mMusicNotifi);
                } else {
                    mRemoteViews.setImageViewResource(R.id.notigication_album, R.drawable.def_album);
                }
            } else {
                Bitmap bitmap = BitmapUtil.getAlbumArt(mContext, song);
                if (bitmap == null) {
                    mRemoteViews.setImageViewResource(R.id.notigication_album, R.drawable.def_album);
                } else {
                    mRemoteViews.setImageViewBitmap(R.id.notigication_album, bitmap);
                }
            }
            if (MusicUtil.isPlayMusic()) {
                mRemoteViews.setImageViewResource(R.id.notigication_playorpaues, R.drawable.notigication_pause);
            } else {
                mRemoteViews.setImageViewResource(R.id.notigication_playorpaues, R.drawable.notigication_play);
            }
        }

        onCreateMusicNotifi(); //每一次改变都要重新创建，所以直接写这里
    }

    public void onUpdataPlayNotifi() {
        if (MusicUtil.isPlayMusic()) {
            mRemoteViews.setImageViewResource(R.id.notigication_playorpaues, R.drawable.notigication_pause);
        } else {
            mRemoteViews.setImageViewResource(R.id.notigication_playorpaues, R.drawable.notigication_play);
        }
        onCreateMusicNotifi(); //每一次改变都要重新创建，所以直接写这里
    }


    /**
     * 取消通知栏
     */
    public void onCancelMusicNotifi(){
        mManager.cancel(NOTIFICATION_ID);
    }


}
