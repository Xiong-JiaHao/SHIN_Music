package com.gin.xjh.shin_music.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.music_play_Activity;
import com.gin.xjh.shin_music.service.MusicService;
import com.gin.xjh.shin_music.util.BitmapUtil;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.squareup.picasso.Picasso;

public class MusicNotification extends Notification {

    private static MusicNotification notifyInstance = null;
    private Notification musicNotifi = null;
    private final int NOTIFICATION_ID = 100;

    private Context context;
    private final int REQUEST_CODE = 207;

    private NotificationManager manager = null;
    private Builder builder;

    private RemoteViews remoteViews;
    private Intent play, next, pre;


    public void setContext(Context context){
        this.context=context;
    }
    public void setManager(NotificationManager manager) {
        this.manager = manager;
    }

    private MusicNotification (Context context){
        this.context = context;
        // 初始化操作
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.customnotice);
        builder = new Builder(context);
        play = new Intent();
        play.setAction(MusicService.MUSIC_NOTIFICATION_ACTION_PLAY);
        next = new Intent();
        next.setAction(MusicService.MUSIC_NOTIFICATION_ACTION_NEXT);
        pre = new Intent();
        pre.setAction(MusicService.MUSIC_NOTIFICATION_ACTION_PRE);

    }

    public static MusicNotification getMusicNotification(Context context){
        if (notifyInstance == null) {
            notifyInstance = new MusicNotification(context);
        }
        return notifyInstance;
    }

    public void onCreateMusicNotifi() {
        // 设置点击事件

        // 1.注册控制点击事件

        PendingIntent pplay = PendingIntent.getBroadcast(context, REQUEST_CODE, play, 0);
        remoteViews.setOnClickPendingIntent(R.id.notigication_playorpaues, pplay);

        // 2.注册下一首点击事件

        PendingIntent pnext = PendingIntent.getBroadcast(context, REQUEST_CODE, next, 0);
        remoteViews.setOnClickPendingIntent(R.id.notigication_next, pnext);

        // 3.注册上一首点击事件

        PendingIntent ppre = PendingIntent.getBroadcast(context, REQUEST_CODE, pre, 0);
        remoteViews.setOnClickPendingIntent(R.id.notigication_pre, ppre);

        //4.设置点击事件（调转到播放界面）
        Intent intent = new Intent(context, music_play_Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        builder.setContent(remoteViews)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setOngoing(true)//必须手动代码清除
                .setSmallIcon(R.drawable.notification_icon);//设置下拉图标

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_PROGRESS)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = "ginshin";
            String channelName = "SHIN_Music";
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);//取消提示
            channel.setSound(null, null);//取消提示音
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);

            builder.setChannelId(channelID);
            musicNotifi = builder.build();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            musicNotifi = builder.getNotification();
        } else {
            musicNotifi = builder.build();
        }
        musicNotifi.flags = Notification.FLAG_ONGOING_EVENT;
        manager.notify(NOTIFICATION_ID, musicNotifi);
    }

    public void onUpdataMusicNotifi() {
        Song song = MusicUtil.getNowSong();
        // 设置添加内容
        if (song==null){
            remoteViews.setTextViewText(R.id.notigication_songName,"未知");
            remoteViews.setTextViewText(R.id.notigication_singer,"未知");
            remoteViews.setImageViewResource(R.id.notigication_album,R.drawable.album);
        }
        else {
            remoteViews.setTextViewText(R.id.notigication_songName, song.getSongName());
            remoteViews.setTextViewText(R.id.notigication_singer, song.getSingerName());

            if(song.isOnline()){
                if (song.getAlbumUrl() != null) {
                    Picasso.get()
                            .load(song.getAlbumUrl())
                            .error(R.drawable.album)
                            .into(remoteViews, R.id.notigication_album, NOTIFICATION_ID, musicNotifi);
                } else {
                    remoteViews.setImageViewResource(R.id.notigication_album, R.drawable.album);
                }
            } else {
                remoteViews.setImageViewBitmap(R.id.notigication_album, BitmapUtil.getAlbumArt(context, song));
            }
            if (MusicUtil.isPlayMusic()) {
                remoteViews.setImageViewResource(R.id.notigication_playorpaues, R.drawable.notigication_pause);
            } else {
                remoteViews.setImageViewResource(R.id.notigication_playorpaues, R.drawable.notigication_play);
            }
        }

        onCreateMusicNotifi(); //每一次改变都要重新创建，所以直接写这里
    }

    public void onUpdataPlayNotifi() {
        if (MusicUtil.isPlayMusic()) {
            remoteViews.setImageViewResource(R.id.notigication_playorpaues, R.drawable.notigication_pause);
        } else {
            remoteViews.setImageViewResource(R.id.notigication_playorpaues, R.drawable.notigication_play);
        }
        onCreateMusicNotifi(); //每一次改变都要重新创建，所以直接写这里
    }


    /**
     * 取消通知栏
     */
    public void onCancelMusicNotifi(){
        manager.cancel(NOTIFICATION_ID);
    }


}
