package com.medstat.shahaf.PainScale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements AsyncResponse {
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(Defs.PAIN_SCALE_ID, Context.MODE_PRIVATE);
        String patientCode = settings.getString(Defs.PAIN_SCALE_ID, Defs.INVALID_ID);
        //SharedPreferences.Editor editor = settings.edit();
        //editor.putString("id", INVALID_ID);
        //editor.commit();
        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        if (!Utils.testInternetConnection(this)){
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme_Dark))
                    .setTitle("לא אותר חיבור לאינטרנט")
                    .setMessage("אנא בדוק את החיבור לאינטרנט והפעל את האפליקציה מחדש")
                    .setCancelable(false)
                    .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

           // Toast.makeText(getApplicationContext(), "אנא בדוק את החיבור לאינטרנט והפעל את האפליקציה מחדש", Toast.LENGTH_SHORT).show();
           // finish();
           // System.exit(0);
        }
        else {
            if (Defs.INVALID_ID.equals(patientCode)) {
           //     if (true){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                System.exit(0);
            } else {

                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("מאמת נתונים...");
                progressDialog.show();
                NetworkAsyncTask asyncTask =new NetworkAsyncTask();
                asyncTask.delegate = this;
                asyncTask.execute(Defs.CHECK, "/canOpenForm", patientCode, Utils.getCurrentDateAsString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        //moveTaskToBack(true);
        finish();
    }


    @Override
    public void processFinish(String action, String response) {
        progressDialog.dismiss();
        if (Defs.CHECK.equals(action)){
            try {
                Log.e("response", response);
                int daysRemaning = -100;
                if (Defs.NO_DATA.equals(response) || ((daysRemaning = Integer.parseInt(response)) <= 0)){
                    Log.e("DaysRemaning", ""+daysRemaning);
                    Intent intent1 = new Intent(this, PainQuestions.class);
                    startActivity(intent1);
                    finish();
                    System.exit(0);
                } else {
                    Intent intent1 = new Intent(this, Done.class);
                    intent1.putExtra("daysRemaining", response);
                    Log.e("DONE", "done from main");
                    startActivity(intent1);
                    finish();
                    System.exit(0);
                }
            }
            catch (Exception e){

                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme_Dark))
                        .setTitle("אותרה בעיה במערכת")
                        .setMessage("אנא נסה שוב במועד מאוחר יותר")
                        .setCancelable(false)
                        .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                System.exit(0);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }
}