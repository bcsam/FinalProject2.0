package com.codepath.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by bcsam on 7/14/17.
 * For showing the users conversation with a specific individual
 */

//***when sending to message detail it should just send the name, number, and message
//****textBody will be created in message detail
public class MessagingActivity extends AppCompatActivity{

    Button btSend;
    Button btCheck;
    EditText etBody;

    String recipientName;
    String recipientNumber;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_messaging);

                //stores this info to know which messages to bring up
            recipientName = getIntent().getStringExtra("recipientName");
            recipientNumber = getIntent().getStringExtra("recipientNumber");
            initializeViews();
            setOnClickListeners();
        }

        public void initializeViews(){
                btSend = (Button) findViewById(R.id.btSend);
                btCheck = (Button) findViewById(R.id.btCheck);
                etBody = (EditText) findViewById(R.id.etBody);
        }

        public void setOnClickListeners(){
                btCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Intent intent = new Intent(MessagingActivity.this, PostCheckActivity.class);
                                String message = etBody.getText().toString();
                                if(!message.equals("")) {
                                        intent.putExtra("message", message);
                                        intent.putExtra("recipientName", recipientName); // TODO: 7/14/17 insert recipient here based on who you're texting
                                        intent.putExtra("recipientNumber", recipientNumber); // TODO: 7/14/17 insert recipient number based on who you're texting
                                }else {
                                        Toast.makeText(getApplicationContext(), "Please enter a message!",
                                                Toast.LENGTH_LONG).show();
                                }
                        }
                });

        }
}
