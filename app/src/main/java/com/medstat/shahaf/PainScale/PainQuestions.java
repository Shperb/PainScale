package com.medstat.shahaf.PainScale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class PainQuestions extends AppCompatActivity implements AsyncResponse{

    private static final String TAG = "PainQuestions";
    private static final String MY_SEEK_BAR_VISIBILITY = "my_seek_bar_visibility";
    private static final String MY_SEEK_BAR_TEXT_VISIBILITY = "my_seek_bar_text_visibility";
    private static final String MY_DOWN_BUTTON_VISIBILITY = "my_down_button_visibility";
    private static final String MY_UP_BUTTON_VISIBILITY = "my_up_button_visibility";
    private static final String MY_ADD_MEDICHINE_VISIBILITY = "my_add_medichine_visibility";
    private static final String MY_QUESTION_TEXT_VISIBILITY = "my_question_text_visibility";
    private static final String MY_NEXT_BUTTON_VISIBILITY = "my_next_button_visibility";
    private static final String MY_NEXT_BUTTON_TEXT = "my_next_button_text";
    private static final String MY_PREV_BUTTON_VISIBILITY = "my_prev_button_visibility";
    private static final String MY_CURRENT_QUESTION = "my_current_question";
    private static final String MY_MEDICINE_LIST = "my_medichine_list";
    private static final String MY_MEDICINE_TEXT = "my_medichine_text";
    private static final String MY_ANSWERS = "my_answers";
    private static final String MY_MEDICINE_LIST_VISIBILITY = "my_medichine_list_visibility";

    View _activityRootView;



    SeekBar _seekBar;
    TextView _seekBar_text;
    TextView _question_text;
    EditText _medicine_text;
    ListView _medicine_list;
    ImageButton _downButton;
    ImageButton _upButton;
    ImageButton _addMedicine;
    Button _next;
    Button _prev;
    private long _lastBackPressTime = 0;
    private Toast _toast;
    final int QUESTION_NUM = 6;

    ArrayList<CharSequence> _questions;
    ArrayList<String> _answers;
    ArrayList<String> _medicine;
    int _currentQuestion;
    ProgressDialog progressDialog;



    @Override
    @SuppressWarnings("ResourceType")
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //Check if we have saved state, and restore the visibility
        //to all of the Views we care about
        if (savedInstanceState != null) {

            _seekBar.setVisibility(savedInstanceState.getInt(MY_SEEK_BAR_VISIBILITY, _seekBar.getVisibility()));
            _seekBar_text.setVisibility(savedInstanceState.getInt(MY_SEEK_BAR_TEXT_VISIBILITY, _seekBar_text.getVisibility()));
            _downButton.setVisibility(savedInstanceState.getInt(MY_DOWN_BUTTON_VISIBILITY, _downButton.getVisibility()));
            _upButton.setVisibility(savedInstanceState.getInt(MY_UP_BUTTON_VISIBILITY, _upButton.getVisibility()));
            _addMedicine.setVisibility(savedInstanceState.getInt(MY_ADD_MEDICHINE_VISIBILITY, _addMedicine.getVisibility()));
            _question_text.setVisibility(savedInstanceState.getInt(MY_QUESTION_TEXT_VISIBILITY, _question_text.getVisibility()));
            _next.setVisibility(savedInstanceState.getInt(MY_NEXT_BUTTON_VISIBILITY, _next.getVisibility()));
            _next.setText(savedInstanceState.getCharSequence(MY_NEXT_BUTTON_TEXT));
            _prev.setVisibility(savedInstanceState.getInt(MY_PREV_BUTTON_VISIBILITY, _prev.getVisibility()));
            _currentQuestion = savedInstanceState.getInt(MY_CURRENT_QUESTION);
            _medicine = savedInstanceState.getStringArrayList(MY_MEDICINE_LIST);
            _medicine_text.setVisibility(savedInstanceState.getInt(MY_MEDICINE_TEXT, _medicine_text.getVisibility()));
            _answers = savedInstanceState.getStringArrayList(MY_ANSWERS);
            _medicine_list.setVisibility(savedInstanceState.getInt(MY_MEDICINE_LIST_VISIBILITY, _medicine_list.getVisibility()));;
            _question_text.setText(_questions.get(_currentQuestion));
            //((ArrayAdapter<String>) _medicine_list.getAdapter()).notifyDataSetChanged();
            ArrayAdapter<String> arrayAdapter =
                    new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, _medicine);
            _medicine_list.setAdapter(arrayAdapter);
            _medicine_text.requestFocus();
        }
    }


    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the state of each of these. It's super important to add null checks here
        //(which is why I prefer to let the View handle it) as in some cases this can
        //get called after the Views have been destroyed.

        if (_seekBar != null) {
            outState.putInt(MY_SEEK_BAR_VISIBILITY, _seekBar.getVisibility());
        }

        if (_seekBar_text != null) {
            outState.putInt(MY_SEEK_BAR_TEXT_VISIBILITY, _seekBar_text.getVisibility());
        }

        if (_downButton != null) {
            outState.putInt(MY_DOWN_BUTTON_VISIBILITY, _downButton.getVisibility());
        }

        if (_upButton != null) {
            outState.putInt(MY_UP_BUTTON_VISIBILITY, _upButton.getVisibility());
        }

        if (_addMedicine != null) {
            outState.putInt(MY_ADD_MEDICHINE_VISIBILITY, _addMedicine.getVisibility());
        }

        if (_question_text != null) {
            outState.putInt(MY_QUESTION_TEXT_VISIBILITY, _question_text.getVisibility());
        }

        if (_next != null) {
            outState.putInt(MY_NEXT_BUTTON_VISIBILITY, _next.getVisibility());
            outState.putCharSequence(MY_NEXT_BUTTON_TEXT, _next.getText());
        }

        if (_prev != null) {
            outState.putInt(MY_PREV_BUTTON_VISIBILITY, _prev.getVisibility());
        }

        outState.putInt(MY_CURRENT_QUESTION, _currentQuestion);

        if (_medicine != null) {
            outState.putStringArrayList(MY_MEDICINE_LIST, _medicine);
        }

        if (_medicine_text != null) {
            outState.putInt(MY_MEDICINE_TEXT, _medicine_text.getVisibility());
        }

        if (_answers != null) {
            outState.putStringArrayList(MY_ANSWERS, _answers);
        }

        if (_medicine_list != null) {
            outState.putInt(MY_MEDICINE_LIST_VISIBILITY, _medicine_list.getVisibility());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pain_questions);
        _questions = new ArrayList<CharSequence>();
        _questions.add(Html.fromHtml(getResources().getString(R.string.Q1)));
        _questions.add(Html.fromHtml(getResources().getString(R.string.Q2)));
        _questions.add(Html.fromHtml(getResources().getString(R.string.Q3)));
        _questions.add(Html.fromHtml(getResources().getString(R.string.Q4)));
        _questions.add(Html.fromHtml(getResources().getString(R.string.Q5)));
        _questions.add(Html.fromHtml(getResources().getString(R.string.Q6)));
        _seekBar = (SeekBar) findViewById(R.id.seekBar);
        _downButton = (ImageButton) findViewById(R.id.downButton);
        _upButton = (ImageButton) findViewById(R.id.upButton);
        _addMedicine = (ImageButton) findViewById(R.id.addMedicine);
        _question_text = (TextView) findViewById(R.id.question_text);
        _next = (Button) findViewById(R.id.nextButton);
        _prev = (Button) findViewById(R.id.prevButton);
        _medicine_text = (EditText) findViewById(R.id.medicine_text);
        _medicine_list = (ListView) findViewById(R.id.medicine_list);
        _seekBar_text = (TextView) findViewById(R.id.seekBar_text);
        _medicine_text.setBackgroundResource(R.drawable.edittext);
        /*
        _activityRootView = findViewById(R.id.activityRoot);

        _activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = _activityRootView.getRootView().getHeight() - _activityRootView.getHeight();
                if (_medicine_text.getVisibility() == View.VISIBLE) {
                    if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                        _next.setVisibility(View.INVISIBLE);
                        _prev.setVisibility(View.INVISIBLE);
                    } else {
                        _next.setVisibility(View.VISIBLE);
                        _prev.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        */
        _answers = new ArrayList<String>();
        _medicine = new ArrayList<String>();
        _addMedicine.setVisibility(View.INVISIBLE);
        _currentQuestion = 0;
        _prev.setVisibility(View.INVISIBLE);
        _answers.add("0");
        _answers.add("0");
        _answers.add("0");
        _answers.add("0");
        _answers.add("0");
        _medicine_text.setVisibility(View.INVISIBLE);
        _medicine_list.setVisibility(View.INVISIBLE);
        _question_text.setText(_questions.get(_currentQuestion));

        _seekBar_text.setText(_seekBar.getProgress() + "/" + _seekBar.getMax());
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, _medicine);
        _medicine_list.setAdapter(arrayAdapter);
        _seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;

                _seekBar_text.setText(progress + "/" + seekBar.getMax());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do something here, if you want to do anything at the start of
                // touching the seekbar


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Display the value in textview
                _seekBar_text.setText(progress + "/" + seekBar.getMax());

            }
        });

        _downButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                decNumber();
            }
        });

        _addMedicine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String enteredString = _medicine_text.getText().toString();
                enteredString.trim();
                if (!enteredString.isEmpty()) {
                    _medicine.add(enteredString);
                }
                _medicine_text.setText("");
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        _upButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incNumber();
            }
        });

        _next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nextQuestion();
            }


        });

        _prev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                prevQuestion();
            }


        });

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void nextQuestion() {
        if (_currentQuestion == QUESTION_NUM-1){
            sendData();
        }
        else {
            _answers.set( _currentQuestion,"" + _seekBar.getProgress());
            _currentQuestion++;
            _prev.setVisibility(View.VISIBLE);
            _question_text.setText(_questions.get(_currentQuestion));
            if (_currentQuestion == QUESTION_NUM-1){
                _seekBar.setVisibility(View.INVISIBLE);
                _seekBar_text.setVisibility(View.INVISIBLE);
                _downButton.setVisibility(View.INVISIBLE);
                _upButton.setVisibility(View.INVISIBLE);
                _medicine_text.setVisibility(View.VISIBLE);
                _medicine_list.setVisibility(View.VISIBLE);
                _addMedicine.setVisibility(View.VISIBLE);

                _next.setText(getResources().getString(R.string.finish));
            }
            else{

                int progress = Integer.parseInt(_answers.get(_currentQuestion));
                _seekBar.setProgress(progress);
                _seekBar_text.setText(progress + "/" + _seekBar.getMax());
            }
        }

    }
    private void prevQuestion() {
        if (_currentQuestion == QUESTION_NUM-1){
            _next.setText(getResources().getString(R.string.next));
            _medicine_text.setVisibility(View.INVISIBLE);
            _medicine_list.setVisibility(View.INVISIBLE);
            _addMedicine.setVisibility(View.INVISIBLE);
            _seekBar.setVisibility(View.VISIBLE);
            _seekBar_text.setVisibility(View.VISIBLE);
            _downButton.setVisibility(View.VISIBLE);
            _upButton.setVisibility(View.VISIBLE);
        }
        else{
            _answers.set(_currentQuestion, "" + _seekBar.getProgress());
        }
        _currentQuestion--;
        _next.setVisibility(View.VISIBLE);
        if (_currentQuestion == 0){
            _prev.setVisibility(View.INVISIBLE);
        }
        _question_text.setText(_questions.get(_currentQuestion));
        int progress = Integer.parseInt(_answers.get(_currentQuestion));
        _seekBar.setProgress(progress);
        _seekBar_text.setText(progress + "/" + _seekBar.getMax());
    }

    private void decNumber() {
        int progress = _seekBar.getProgress();
        if (progress == 0){
            return;
        }
        progress--;
        _seekBar.setProgress(progress);
        _seekBar_text.setText(progress + "/" + _seekBar.getMax());

    }
    private void incNumber() {
        int progress = _seekBar.getProgress();
        if (progress == 100){
            return;
        }
        progress++;
        _seekBar.setProgress(progress);
        _seekBar_text.setText(progress + "/" + _seekBar.getMax());

    }



    @Override
    public void onBackPressed() {
        if (_currentQuestion == 0) {
            if (this._lastBackPressTime < System.currentTimeMillis() - 3000) {
                _toast = Toast.makeText(this, "Press back again to close this app", Toast.LENGTH_LONG);
                _toast.show();
                this._lastBackPressTime = System.currentTimeMillis();
            } else {
                if (_toast != null) {
                    _toast.cancel();
                }
                finish();
                //super.onBackPressed();
            }
        }
        else{
            prevQuestion();
        }
    }

    public void sendData() {
        Log.d(TAG, "Questions");

        _next.setEnabled(false);
        _prev.setEnabled(false);

        progressDialog = new ProgressDialog(PainQuestions.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("שולח נתונים לשרת...");
        progressDialog.show();

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
        }
        else{
            NetworkAsyncTask asyncTask = new NetworkAsyncTask();
            asyncTask.delegate = this;
            SharedPreferences settings = getSharedPreferences(Defs.PAIN_SCALE_ID, Context.MODE_PRIVATE);
            String patientCode = settings.getString(Defs.PAIN_SCALE_ID, Defs.INVALID_ID);
            String[] test = new String[10];
            int aLen = _answers.size();
            int bLen = _medicine.size();
            ArrayList<String> answers = new ArrayList<String>();
            answers.add(Defs.SEND);
            answers.add("/submitForm");
            answers.add(patientCode);
            answers.add(Utils.getCurrentDateAsString());
            answers.addAll(_answers);
            answers.addAll(_medicine);
            String[] ansArray = new String[answers.size()];
            ansArray = answers.toArray(ansArray);
            asyncTask.execute(ansArray);
        }
    }

    private void onSendSuccess() {
        SharedPreferences settings = getSharedPreferences(Defs.PAIN_SCALE_ID, Context.MODE_PRIVATE);
        String patientCode = settings.getString(Defs.PAIN_SCALE_ID, Defs.INVALID_ID);
        NetworkAsyncTask asyncTask =new NetworkAsyncTask();
        asyncTask.delegate = this;
        asyncTask.execute(Defs.CHECK, "/canOpenForm", patientCode, Utils.getCurrentDateAsString());
    }

    private void onSendError() {
        progressDialog.dismiss();
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


    @Override
    public void processFinish(String action,String response) {
        Log.e("response", response);
        if (Defs.SEND.equals(action)){
            if (Defs.SUCCESS.equals(response)){
                onSendSuccess();
            }
            else{
                onSendError();
            }
        }
        if (Defs.CHECK.equals(action)) {
            try {
                Log.e("response", response);
                int daysRemaning = Integer.parseInt(response);
                Intent intent = new Intent(this, Done.class);
                intent.putExtra("filledFormNow", true);
                intent.putExtra("daysRemaining", response);
                Log.e("DONE", "done from pain");
                progressDialog.dismiss();
                startActivity(intent);

                finish();
                System.exit(0);

            } catch (Exception e) {

                onSendError();
            }
        }
    }
}
