package com.ameer.messages.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ameer.messages.R;
import com.ameer.messages.activities.MainActivity;
import com.ameer.messages.utils.NotificationHelper;

public class NotificationService extends Service {

    Intent mIntent;
    NotificationHelper mNotificationHelper;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals("copy.OTP")) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("otp", intent.getStringExtra("message"));
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "OTP Copied"+intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals("start.service")) {
            Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
            mIntent = intent;
        } else if(intent.getAction().equals("show_notification")) {
            mNotificationHelper = new NotificationHelper(this);
            Log.d("mia", "onReceive: show_notification");
            String message = intent.getStringExtra("message");
            String address = intent.getStringExtra("contact");
            int type = intent.getIntExtra("type",0);
            showNotification(type,message,address);
            Toast.makeText(this,"Showing Notification",Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }
    public void showNotification(int type,String message, String address){

        if(type == 1){
            RemoteViews views = new RemoteViews(getPackageName(),
                    R.layout.otp_notification);
            views.setTextViewText(R.id.otp_text,message);
            Intent copyIntent = new Intent(this, NotificationService.class);
            copyIntent.setAction("copy.OTP");
            copyIntent.putExtra("message",message);
            PendingIntent copyPendingIntent = PendingIntent.getService(this, 1,
                    copyIntent, 0);
            views.setOnClickPendingIntent(R.id.copy_otp, copyPendingIntent);
            views.setImageViewResource(R.id.contact,R.drawable.icon_contact);
            NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext(),NotificationHelper.channel1ID);
            b.setContent(views);
            b.setSmallIcon(R.drawable.icon_contact);
            NotificationManager notificationManager = mNotificationHelper.getManager();
            notificationManager.notify(1, b.build());

        }else if(type == 2){
            RemoteViews views = new RemoteViews(getPackageName(),
                    R.layout.default_notificatiion);
            views.setImageViewResource(R.id.contact,R.drawable.icon_contact);
            views.setTextViewText(R.id.title,"Message from: "+address);
            views.setTextViewText(R.id.notification_message,message);

            Intent homeIntent = new Intent(getApplicationContext(),MainActivity.class);
            PendingIntent intent = PendingIntent.getService(this,1,
                    homeIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext(),NotificationHelper.channel2ID);
            b.setSmallIcon(R.drawable.icon_contact);
            b.setContent(views);
            b.setAutoCancel(true);
            b.setContentIntent(intent);

            Notification notification = b.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            NotificationManager notificationManager = mNotificationHelper.getManager();
            notificationManager.notify(2, notification);

        }


    }
}
