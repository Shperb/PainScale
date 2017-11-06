package com.medstat.shahaf.PainScale;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationService(String name) {
        super(name);
    }

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.e("starting intent", "starting");
            SharedPreferences settings = getSharedPreferences(Defs.PAIN_SCALE_ID, Context.MODE_PRIVATE);
            String patientCode = settings.getString(Defs.PAIN_SCALE_ID, Defs.INVALID_ID);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
            OkHttpClient client2 = new OkHttpClient();
            JSONObject oneData = new JSONObject();
            oneData.put("PatientID", patientCode);
            oneData.put("ClientDateTime", Utils.getCurrentDateAsString());
            RequestBody body = RequestBody.create(Defs.JSON, oneData.toString());
            Request request = new Request.Builder()
                    .url(Defs.serverAddressProduction + "/canOpenForm")
                    .post(body)
                    .build();
            Response response = client2.newCall(request).execute();
            String result = response.body().string();
            Log.e("here","here");
            if (tryParse(result) == null){
                Utils.sendNotification(this, intent.getStringExtra("title"),intent.getStringExtra("body"), true);
            }
            else{
                int daysRemaning = Integer.parseInt(result);
                if (daysRemaning<= 0){
                    Utils.sendNotification(this, intent.getStringExtra("title"),intent.getStringExtra("body"), true);
                }
            }
        }
        catch (Exception e){
            Log.e("error",e.getMessage());
            Utils.sendNotification(this, intent.getStringExtra("title"),intent.getStringExtra("body")             , true);
        }



    }
    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}