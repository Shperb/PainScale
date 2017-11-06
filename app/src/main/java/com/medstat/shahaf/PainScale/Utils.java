package com.medstat.shahaf.PainScale;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Shahaf on 3/24/2016.
 */
public class Utils {

    public static String getCurrentDateAsString(){
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.UK);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,-3);
        return curFormater.format(calendar.getTime());
    }

    public static boolean testInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting())
        {
            return false;
        }
        return true;
    }

    public static void sendNotification(Context context,String title,String body,boolean isOngoing) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/"+ R.raw.amazing_simple_beep);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setOngoing(isOngoing)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
