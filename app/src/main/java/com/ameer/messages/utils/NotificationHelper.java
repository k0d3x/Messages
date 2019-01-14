package com.ameer.messages.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import com.ameer.messages.R;

public class NotificationHelper extends ContextWrapper {
    public static final String channel1ID = "channel1";
    public static final String channel1Name = "channel_one";
    public static final String channel2ID = "channel2";
    public static final String channel2Name = "channel_two";
    private NotificationManager mManager;
    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel(){
        NotificationChannel channel1 = new NotificationChannel(channel1ID,channel1Name,NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(R.color.colorWhite);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel1);

        NotificationChannel channel2 = new NotificationChannel(channel2ID,channel2Name,NotificationManager.IMPORTANCE_DEFAULT);
        channel2.enableLights(true);
        channel2.enableVibration(true);
        channel2.setLightColor(R.color.colorWhite);
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel2);
    }
    public NotificationManager getManager(){
        if(mManager == null){
            mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

}
