package com.medstat.shahaf.PainScale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.Calendar;

public class PainScaleGcmListenerService extends GcmListenerService {

    private static final String TAG = "PainScaleGcmListenerService";


    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.e("started setting", "setting");
        String title = "PainScale";
        String body = data.getString("PainScale");
        Utils.sendNotification(this, title,body, true);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 10);
        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, notificationReceiver.class);
        notificationIntent.putExtra("body",body);
        notificationIntent.putExtra("title",title);
        PendingIntent notificationAlarmIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, 0);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notificationAlarmIntent);
        Log.e("finished setting", "setting");

    }

    public static class notificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("finished receving", "receving");
            Intent newIntent = new Intent(context, NotificationService.class);
            newIntent.putExtra("body",intent.getStringExtra("body"));
            //Log.e("body", intent.getStringExtra("body"));
            newIntent.putExtra("title", intent.getStringExtra("title"));
            //Log.e("title", intent.getStringExtra("title"));
            context.startService(newIntent);
        }
    }


            /**
             * Create and show a simple notification containing the received GCM message.
             *
             * @param message GCM message received.
             */

}

