package com.codepath.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    Button btCheck;
    EditText etBody;
    EditText etName;
    EditText etSubject;
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
        etName = (EditText) findViewById(R.id.etName);
        etSubject = (EditText) findViewById(R.id.etSubject);


        btCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });
    }

    public void onSubmit(){
        String message = etBody.getText().toString();
        String to = etName.getText().toString();
        String subject = etSubject.getText().toString();

        Intent intent = new Intent(MainActivity.this, PostCheckActivity.class);
        intent.putExtra("message", message);
        intent.putExtra("to", to);
        intent.putExtra("subject", subject);

        TextBody tb = new TextBody();
        tb.setMessage(message);
        client = new AnalyzerClient();
        client.getToneScores(tb);

        MainActivity.this.startActivity(intent);
    }
}

