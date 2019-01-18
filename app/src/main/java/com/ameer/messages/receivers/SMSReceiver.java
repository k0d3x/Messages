package com.ameer.messages.receivers;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ameer.messages.SMS;
import com.ameer.messages.services.NotificationService;
import com.ameer.messages.services.SaveSMSService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSReceiver";
    private static final String pdus_type = "pdus";
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String strAddress = "";
        String strMessage = "";
        //long time;
        String format = bundle.getString("format");
        Object[] pdus = (Object[])bundle.get(pdus_type);
        Log.d("avi", "onReceive: Message receive");
        if(pdus != null){
            boolean isVersionM = (Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.M);
            msgs = new SmsMessage[pdus.length];
            for(int i = 0; i < msgs.length; i++){
                if(isVersionM)
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i],format);
                else
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                strAddress = msgs[i].getOriginatingAddress();
                strMessage = msgs[i].getMessageBody();
                //time = msgs[i].getTimestampMillis();

                Intent intent1 = new Intent(context,SaveSMSService.class);
                intent1.setAction("SaveSMS");
                intent1.putExtra("address",strAddress);
                intent1.putExtra("message_body",strMessage);
                context.sendBroadcast(intent1);
                if(strMessage != null && strMessage.contains(" otp ") || strMessage.contains(" OTP ")){
                    Pattern r = Pattern.compile("(\\d+(?=\\sis)|(?<=is\\s)\\d+)");
                    Matcher m = r.matcher(strMessage);
                  //  \d+(?=\sis)|(?<=is\s)\d+
                    if (m.find( )) {
                        String otp = m.group(0);
                        Intent copyIntent = new Intent(context,NotificationService.class);
                        copyIntent.setAction("show_notification");
                        copyIntent.putExtra("contact",strAddress);
                        copyIntent.putExtra("message",otp);
                        copyIntent.putExtra("type",1);
                        context.startService(copyIntent);
                        //issue notification
                    }
                }else {
                    Intent copyIntent = new Intent(context,NotificationService.class);
                    copyIntent.setAction("show_notification");
                    copyIntent.putExtra("contact",strAddress);
                    copyIntent.putExtra("message",strMessage);
                    copyIntent.putExtra("type",2);
                    context.startService(copyIntent);
                    Log.d("mia", "onReceive: notification");
                }
                //issue normal notification

          }
        }

    }
}
