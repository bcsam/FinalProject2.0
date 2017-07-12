package com.codepath.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    Button btCheck;
    EditText etBody;
    Context context;
    AnalyzerClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //Button btCheck = (Button) findViewById(btCheck);
        //client = new AnalyzerClient();
        /*Sentence s = new Sentence();
        s.setMessage("I am so angry!");
        client.getToneScores(s);
        Log.i("Main", String.valueOf(s.getAngerLevel()));
        TextBody tb = new TextBody();
        TextBody.setMessage("I am so angry!");
        client.getToneScores(tb);
        Log.i("Main", String.valueOf(tb.getAngerLevel()));*/

        btCheck = (Button) findViewById(R.id.btCheck);
        etBody = (EditText) findViewById(R.id.etBody);

        btCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });

        etBody.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });

    }

    public void onSubmit(){
        String message = etBody.getText().toString();
        Intent intent = new Intent(MainActivity.this, PostCheckActivity.class);
        intent.putExtra("message", message);

        TextBody tb = new TextBody();
        tb.setMessage(message);
        client = new AnalyzerClient();
        client.getToneScores(tb);

        MainActivity.this.startActivity(intent);
    }

    public void sendMessage () {
        String message = etBody.getText().toString();
        Intent intent = new Intent(MainActivity.this, PostCheckActivity.class);
        intent.putExtra("message", message);

        TextBody tb = new TextBody();
        tb.setMessage(message);
        client = new AnalyzerClient();
        client.getToneScores(tb);

        MainActivity.this.startActivity(intent);
    }
}

