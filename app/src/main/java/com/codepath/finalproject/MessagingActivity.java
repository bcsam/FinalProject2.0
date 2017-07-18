package com.codepath.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

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
    ArrayList<SMS> messages;

    String recipientName;
    String recipientNumber;

    RecyclerView rvText;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_messaging);

                //stores this info to know which messages to bring up
            recipientName = getIntent().getStringExtra("name");
            recipientNumber = getIntent().getStringExtra("number");
            Log.i("recipientNumber", recipientNumber);
            initializeViews();
            setOnClickListeners();
            rvText = (RecyclerView) findViewById(R.id.rvMessaging);
            messages = new ArrayList<SMS>();
            getMessages();
            rvText.setLayoutManager(new LinearLayoutManager(this));
            rvText.setAdapter(new ListAdapter(this, messages));
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
                                        MessagingActivity.this.startActivity(intent);
                                }else {
                                        Toast.makeText(getApplicationContext(), "Please enter a message!",
                                                Toast.LENGTH_LONG).show();
                                }
                        }
                });

        }

        public void getMessages(){
            Cursor c = getContentResolver().query(Uri.parse("content://sms"), null, "address='"+recipientNumber+"'", null, null);
            while (c.moveToNext()) {
                for (int i = 0; i < c.getCount(); i++) {
                    String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                    SMS message = new SMS();
                    message.setBody(text);
                    message.setNumber(" ");
                    message.setContact(" ");
                    messages.add(0, message);
                    c.moveToNext();
                }
            }
            c.close();
        }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(MessagingActivity.this, MainActivity.class);
        startActivity(i);
    }
}
