package com.codepath.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by bcsam on 7/14/17.
 */

public class MessageDetailActivity extends AppCompatActivity{
    String author;
    String message;


    //needs author's name, message, analysis info
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        TextBody tb = getIntent().getParcelableExtra("textBody");

    }
}
