package com.codepath.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

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
        Sentence s = new Sentence();
        s.setMessage("I am so angry!");
        client = new AnalyzerClient();
        client.getToneScores(s);
        Log.i("Main", String.valueOf(s.getAngerLevel()));
    }

   /*
    btCheck.setOnClickListener(new View.OnClickListener(){
        public void onClick (View v){
            Intent intent = new Intent(context, PostCheckActivity.class);
            context.startActivity(intent);
        }
    });
    */
}
