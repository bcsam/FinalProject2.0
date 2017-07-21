package com.codepath.finalproject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.view.inputmethod.InputMethodManager;
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
public class MessagingActivity extends AppCompatActivity {

    Button btSend;
    Button btCheck;
    EditText etBody;
    ArrayList<SMS> messages;

    String recipientName;
    String recipientNumber;
    String recipientId;

    String myId;
    String myNumber;
    String id;
    public static MessagingActivity inst;
    //ListAdapter adapter;
    Cursor c;
    ConversationAdapter adapter;

    RecyclerView rvText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        inst = this;

        //stores this info to know which messages to bring up
        recipientName = getIntent().getStringExtra("name");
        recipientNumber = getIntent().getStringExtra("number");
        recipientId = getIntent().getStringExtra("id");

        if (!recipientName.equals("")) {
            getSupportActionBar().setTitle(recipientName);
        } else {
            getSupportActionBar().setTitle(recipientNumber);
        }

        initializeViews();
        setListeners();
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        myNumber = tMgr.getLine1Number();

        ContentResolver contentResolver = this.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(myNumber));

        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if(cursor != null) {
            while(cursor.moveToNext()){
                myId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }

        rvText = (RecyclerView) findViewById(R.id.rvMessaging);
        messages = new ArrayList<SMS>();
        getMessages();
        adapter = new ConversationAdapter(this, messages);
        rvText.setLayoutManager(new LinearLayoutManager(this));
        rvText.setAdapter(adapter);
        rvText.scrollToPosition(messages.size() - 1);
    }

    public static MessagingActivity instance() {
        return inst;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                List<SMS> postQuerySmsList = new ArrayList<SMS>();
                for (SMS text : messages) {
                    String body = text.getBody();

                    if (body.toLowerCase().contains(query.toLowerCase())) {
                        Toast.makeText(getApplicationContext(), query,
                                Toast.LENGTH_LONG).show();
                        postQuerySmsList.add(text);
                    }
                }

                rvText.setAdapter(new ConversationAdapter(MessagingActivity.this, postQuerySmsList));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                rvText.setAdapter(new ConversationAdapter(MessagingActivity.this, messages));
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    public void launchComposeActivity(MenuItem item) {
        Intent i = new Intent(MessagingActivity.this, ComposeActivity.class);
        MessagingActivity.this.startActivity(i);
    }

    public void launchMyProfileActivity(MenuItem item) {
        //launches the profile view
        User user = new User(this);
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
        user.setNumber("+"+mPhoneNumber);
        user.setName("Me");
        user.setContactId(myId);
        Log.i("profile", user.getNumber());
        Log.i("profile", user.toStringNumber());
        Intent i = new Intent(MessagingActivity.this, ProfileActivity.class);

        i.putExtra("user", user);
        MessagingActivity.this.startActivity(i);
    }

    public void launchMainActivity(MenuItem item) {
        Intent i = new Intent(MessagingActivity.this, MainActivity.class);
        MessagingActivity.this.startActivity(i);
    }

    public void initializeViews() {
        btSend = (Button) findViewById(R.id.btSend);
        btCheck = (Button) findViewById(R.id.btCheck);
        etBody = (EditText) findViewById(R.id.etBody);
    }

    public void setListeners() {
        btCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessagingActivity.this, PostCheckActivity.class);
                String message = etBody.getText().toString();
                if (!message.equals("")) {
                    intent.putExtra("message", message);
                    intent.putExtra("recipientName", recipientName); // TODO: 7/14/17 insert recipient here based on who you're texting
                    intent.putExtra("recipientNumber", recipientNumber); // TODO: 7/14/17 insert recipient number based on who you're texting
                    intent.putExtra("recipientId", recipientId);
                    MessagingActivity.this.startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a message!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        etBody.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }
            }
        });
    }

    public void getMessages() {
        ContentValues contentValues = new ContentValues();

        if (recipientNumber.length() == 12) {
            differentNumber(recipientNumber);
            differentNumber(recipientNumber.substring(1, 12));
            differentNumber(recipientNumber.substring(2, 12));
        }
        else if (recipientNumber.length() == 11) {
            differentNumber(recipientNumber);
            differentNumber(recipientNumber.substring(1, 11));
            differentNumber("+" + recipientNumber);
        }
        else if (recipientNumber.length() == 10) {
            differentNumber(recipientNumber);
            differentNumber("1" + recipientNumber);
            differentNumber("+1" + recipientNumber);
        }
        else {
            differentNumber(recipientNumber);
        }

        c = getContentResolver().query(Uri.parse("content://sms/sent"), null, "address='" + recipientNumber + "'", null, null);
        while (c.moveToNext()) {
            for (int i = 0; i < c.getCount(); i++) {
                String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                String date = c.getString(c.getColumnIndexOrThrow("date")).toString();

                ContentResolver contentResolver = this.getContentResolver();

                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(recipientNumber));

                String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

                Cursor cursor =
                        contentResolver.query(
                                uri,
                                projection,
                                null,
                                null,
                                null);

                if(cursor != null) {
                    while(cursor.moveToNext()){
                        id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                    }
                    cursor.close();
                }

                contentValues.put("read", true);
                getContentResolver().update(Uri.parse("content://sms/inbox"), contentValues, "_id=" + id, null);
                Log.i("check id", id);
                Log.i("check value", c.getString(c.getColumnIndexOrThrow("read")).toString());
                SMS message = new SMS();
                message.setBody(text);
                Log.i("MyNumber", myNumber);
                message.setNumber(myNumber);
                message.setDate(date);
                message.setContact(" ");
                message.setContactId(id);

                int index = messages.size();
                for (SMS m : messages) {
                    if (Double.parseDouble(m.getDate()) > Double.parseDouble(message.getDate())) {
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

    public void differentNumber(String number) {
        c = getContentResolver().query(Uri.parse("content://sms/inbox"), null, "address='" + number + "'", null, null);
        while (c.moveToNext()) {

            for (int i = 0; i < c.getCount(); i++) {
                String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                String date = c.getString(c.getColumnIndexOrThrow("date")).toString();

                ContentResolver contentResolver = this.getContentResolver();

                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(recipientNumber));

                String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

                Cursor cursor =
                        contentResolver.query(
                                uri,
                                projection,
                                null,
                                null,
                                null);

                if(cursor != null) {
                    while(cursor.moveToNext()){
                        id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                    }
                    cursor.close();
                }

                SMS message = new SMS();
                message.setBody(text);
                Log.i("MyNumber", myNumber);
                message.setNumber(number);
                message.setDate(date);
                message.setContact(" ");
                message.setContactId(id);
                int index = messages.size();
                for (SMS m : messages) {
                    if (Double.parseDouble(m.getDate()) > Double.parseDouble(message.getDate())) {
                        index = messages.indexOf(m);
                        break;
                    }
                }
                messages.add(index, message);
                c.moveToNext();
            }
        }
    }

    public void sendText(View view){
        SMS text = new SMS();
        text.setNumber(recipientNumber);
        text.setBody(etBody.getText().toString());
        text.setDate(String.valueOf(System.currentTimeMillis()));
        etBody.clearFocus();
        etBody.setText("");
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        text.sendSMS();
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String myNumber = tMgr.getLine1Number();
        text.setNumber(myNumber);
        messages.add(messages.size(), text);
        adapter.notifyItemInserted(messages.size());
        rvText.scrollToPosition(messages.size());
    }
}

