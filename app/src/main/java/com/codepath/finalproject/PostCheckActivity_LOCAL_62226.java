package com.codepath.finalproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by bcsam on 7/11/17.
 */

public class PostCheckActivity extends AppCompatActivity {

    TextView tvBody;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post_check);
        String text = getIntent().getStringExtra("text");
        TextBody textBody = new TextBody();
        textBody.setMessage(text);
        AnalyzerClient client = new AnalyzerClient();
        client.getToneScores(textBody);
        TextView textView = (TextView) findViewById(R.id.tvTextBody);
        textView.setText(text);
        textView.setTextColor(Color.parseColor(textBody.getColor()));
    }


}
