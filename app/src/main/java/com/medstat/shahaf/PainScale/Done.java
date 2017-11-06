package com.medstat.shahaf.PainScale;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class Done extends AppCompatActivity {
    TextView _doneText;

    Button _finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        _doneText = (TextView) findViewById(R.id.doneText);
        Intent myIntent = getIntent();
        String daysRemaining = myIntent.getStringExtra("daysRemaining");
        Log.e("done days", daysRemaining);
        boolean filledFormNow = myIntent.getBooleanExtra("filledFormNow",false);
        if (daysRemaining != null){
            StringBuilder sb = new StringBuilder();
            if (filledFormNow){
                sb.append("הנתונים לחודש זה נשלחו בהצלחה,");
                sb.append(System.getProperty("line.separator"));
            }
            sb.append("עליך לחכות להתראה החודשית בעוד ");
            sb.append(daysRemaining);
            if (daysRemaining.equals("1") ){
                sb.append(" יום.");
            }
            else{
                sb.append(" ימים.");
            }

            _doneText.setText(sb.toString());

        }
        _finish = (Button) findViewById(R.id.finish);
        _finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
