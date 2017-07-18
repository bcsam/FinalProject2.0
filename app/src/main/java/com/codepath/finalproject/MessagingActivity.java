package com.codepath.finalproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
    String myNumber;

    RecyclerView rvText;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_messaging);

                //stores this info to know which messages to bring up
            recipientName = getIntent().getStringExtra("name");
            recipientNumber = getIntent().getStringExtra("number");

            if (!recipientName.equals("")) {
                getSupportActionBar().setTitle(recipientName);
            }
            else {
                getSupportActionBar().setTitle(recipientNumber);
            }

            Log.i("recipientNumber", recipientNumber);
            initializeViews();
            setOnClickListeners();
            TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            myNumber = tMgr.getLine1Number();
            rvText = (RecyclerView) findViewById(R.id.rvMessaging);
            messages = new ArrayList<SMS>();
            getMessages();
            rvText.setLayoutManager(new LinearLayoutManager(this));
            rvText.setAdapter(new ConversationAdapter(this, messages));

            Log.i("MyNumber", myNumber);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            MenuItem searchItem = menu.findItem(R.id.miSearch);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();

                    //insert query here
                    //edit here down
                    List<SMS> postQuerySmsList = new ArrayList<SMS>();
                    for (SMS text : messages) {
                        String body = text.getBody();

                        if(body.toLowerCase().contains(query.toLowerCase())){
                            Toast.makeText(getApplicationContext(), query,
                                    Toast.LENGTH_LONG).show();
                            postQuerySmsList.add(text);
                        }
                    }

                    rvText.setAdapter(new ListAdapter(MessagingActivity.this, postQuerySmsList));
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            return super.onCreateOptionsMenu(menu);
        }

        public void launchComposeActivity(MenuItem item) {
            //launches the compose activit
            Intent i = new Intent(MessagingActivity.this, ComposeActivity.class);
            MessagingActivity.this.startActivity(i);
        }

        public void launchMyProfileActivity(MenuItem item) {
            //launches the profile view
            Intent i = new Intent(MessagingActivity.this, ProfileActivity.class);
            MessagingActivity.this.startActivity(i);
        }

        public void launchMainActivity(MenuItem item){
            Intent i = new Intent(MessagingActivity.this, MainActivity.class);
            MessagingActivity.this.startActivity(i);
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
            Cursor c = getContentResolver().query(Uri.parse("content://sms/inbox"), null, "address='"+recipientNumber+"'", null, null);
            while (c.moveToNext()) {
                for (int i = 0; i < c.getCount(); i++) {
                    String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                    String date = c.getString(c.getColumnIndexOrThrow("date")).toString();
                    SMS message = new SMS();
                    message.setBody(text);
                    message.setNumber(recipientNumber);
                    message.setContact(" ");
                    message.setDate(date);
                    messages.add(0, message);
                    c.moveToNext();
                }
            }

            c = getContentResolver().query(Uri.parse("content://sms/sent"), null, "address='"+recipientNumber+"'", null, null);
            while (c.moveToNext()) {
                for (int i = 0; i < c.getCount(); i++) {
                    String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                    String date = c.getString(c.getColumnIndexOrThrow("date")).toString();
                    SMS message = new SMS();
                    message.setBody(text);
                    Log.i("MyNumber", myNumber);
                    message.setNumber(myNumber);
                    message.setDate(date);
                    message.setContact(" ");
                    int index = messages.size();
                    for(SMS m: messages){
                        if(Double.parseDouble(m.getDate())>Double.parseDouble(message.getDate())){
                            index = messages.indexOf(m);
                            break;
                        }
                    }
                    messages.add(index, message);
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
