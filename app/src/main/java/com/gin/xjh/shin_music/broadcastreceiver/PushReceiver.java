package com.gin.xjh.shin_music.broadcastreceiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.gin.xjh.shin_music.R;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

public class PushReceiver extends BroadcastReceiver {

    private Notification.Builder mBuilder = null;
    private Notification mNotification;
    private final int NOTIFICATION_ID = 233;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
                String msg = intent.getStringExtra(context.getString(R.string.MESSAGE));
                JSONObject jsonObject = new JSONObject(msg);
                String title = jsonObject.getString(context.getString(R.string.TITLE));
                String content = jsonObject.getString(context.getString(R.string.CONTENT));
                mBuilder = new Notification.Builder(context);
                mBuilder.setContentTitle(title)
                        .setContentText(content)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.notification_icon);//设置下拉图标

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBuilder.setCategory(Notification.CATEGORY_PROGRESS)
                            .setVisibility(Notification.VISIBILITY_PUBLIC);
                }

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelID = context.getString(R.string.UPDATE);
                    String channelName = context.getString(R.string.SHIN_MUSIC_UPDATE);
                    NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

                    manager.createNotificationChannel(channel);

                    mBuilder.setChannelId(channelID);
                    mNotification = mBuilder.build();
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mNotification = mBuilder.getNotification();
                } else {
                    mNotification = mBuilder.build();
                }
                manager.notify(NOTIFICATION_ID, mNotification);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
