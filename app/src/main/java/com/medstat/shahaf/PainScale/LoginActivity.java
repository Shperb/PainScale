package com.medstat.shahaf.PainScale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.ButterKnife;
import butterknife.Bind;

public class LoginActivity extends AppCompatActivity implements AsyncResponse{
    private static final String TAG = "LoginActivity";
    private ProgressDialog progressDialog;
    private String patientCode;

    @Bind(R.id.input_code)
    EditText _codeText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        // _codeText.setBackgroundResource(R.drawable.edittext);
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);


        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("מאמת נתונים...");
        progressDialog.show();
        patientCode = _codeText.getText().toString();
        NetworkAsyncTask asyncTask =new NetworkAsyncTask();
        asyncTask.delegate = this;
        asyncTask.execute(Defs.LOGIN, "/loginPatient", patientCode, Utils.getCurrentDateAsString());
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        //moveTaskToBack(true);
        finish();
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        SharedPreferences settings = getSharedPreferences(Defs.PAIN_SCALE_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Defs.PAIN_SCALE_ID, patientCode);
        editor.commit();
        NetworkAsyncTask asyncTask =new NetworkAsyncTask();
        asyncTask.delegate = this;
        asyncTask.execute(Defs.CHECK, "/canOpenForm", patientCode, Utils.getCurrentDateAsString());
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "הקוד שהוזן שגוי", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public void onLoginError() {
        Toast.makeText(getBaseContext(), "התרחשה שגיאת רשת, אנא נסה שנית מאוחר יותר", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String code = _codeText.getText().toString();
        if (code.isEmpty()) {
            _codeText.setError("אנא הכנס קוד התחברות");
            valid = false;
        } else {
            _codeText.setError(null);
        }

        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.medstat.shahaf.medstat/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.medstat.shahaf.medstat/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void processFinish(String action,String response) {
        if (Defs.LOGIN.equals(action)){
            if (Defs.SUCCESS.equals(response)){
                onLoginSuccess();
                progressDialog.dismiss();
            }
            else if (Defs.FAILED.equals(response)){
                onLoginFailed();
                progressDialog.dismiss();
            }
            else{
                onLoginError();
                progressDialog.dismiss();
            }
        }
        else if (Defs.CHECK.equals(action)){
            try {
                int daysRemaning;
                if (Defs.NO_DATA.equals(response) || ((daysRemaning = Integer.parseInt(response)) <= 0)){
                    Intent intent1 = new Intent(this, PainQuestions.class);
                    startActivity(intent1);
                    finish();
                    System.exit(0);
                } else {

                    Intent intent1 = new Intent(this, Done.class);
                    intent1.putExtra("daysRemaining", response);
                    Log.e("DONE", "done from login");
                    startActivity(intent1);

                    finish();
                    System.exit(0);
                }
            }
            catch (Exception e){
                Toast.makeText(getBaseContext(), "התרחשה שגיאת רשת, אנא נסה שנית מאוחר יותר", Toast.LENGTH_LONG).show();
                finish();
                System.exit(0);
            }

        }
    }
}

/*
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent sampleIntent = new Intent(context, SampleDataAlarmReceiver.class);
        PendingIntent sampleAlarmIntent = PendingIntent.getBroadcast(context, 0, sampleIntent, 0);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 10000, SAMPLING_INTERVAL_MIN, sampleAlarmIntent);

 */
/*
public class SampleDataAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(context, BackgroundSensorSamplingService.class);
        newIntent.setAction("fdsfsdfds");
        context.startService(newIntent);
    }


 */
/*

public class BackgroundSensorSamplingService extends IntentService {
@Override
    protected void onHandleIntent(Intent intent) {
        if ("fdsfsdfds".equals(intent.getAction())) {
            code
        }
    }
    }
 */

/*
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting())
        {
            Log.e("SendDataAlarmReceiver","no network");
            return;
        }
        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
        {
        }
 */