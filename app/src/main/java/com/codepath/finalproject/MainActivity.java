package com.codepath.finalproject;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity { // TODO: 7/12/17 make the app work if the device is turned sideways

    RecyclerView rvText;
    User user;
    ArrayList<User> users;
    Context context;

    private static final int  MY_PERMISSIONS_REQUEST_READ_SMS = 1;
    private static final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;

    String recipientName;
    String recipientNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        users = new ArrayList<User>();
        //if statement for requesting info
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_REQUEST_READ_SMS);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        List<SMS> smsList = new ArrayList<SMS>();

        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        startManagingCursor(c);

        // Read the sms data and store it in the list
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                SMS sms = new SMS();
                recipientNumber = c.getString(c.getColumnIndexOrThrow("address")).toString();
                String body = c.getString(c.getColumnIndexOrThrow("body")).toString();
                recipientName = getContactName(recipientNumber, this);

                sms.setBody(body);
                sms.setNumber(recipientNumber);
                sms.setContact(recipientName);

                smsList.add(sms);

                c.moveToNext();
            }
        }
        c.close();
        try {
            getSharedUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Set smsList in the ListAdapter
        setListAdapter(new ListAdapter(this, smsList));
    }


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void launchProfileActivity(MenuItem item) {
        //launches the profile view
        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
        i.putExtra("user", user);
        MainActivity.this.startActivity(i);
    }

    public void launchComposeActivity(MenuItem item) {
        //launches the profile view
        Intent i = new Intent(MainActivity.this, ComposeActivity.class);
        MainActivity.this.startActivity(i);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        SMS sms = (SMS) getListAdapter().getItem(position);

        Intent intent = new Intent(this, MessagingActivity.class);
        intent.putExtra("recipientName", recipientName);
        intent.putExtra("recipientNumber", recipientNumber);

        startActivity(intent);

        //want to send to MessageActivity
        //want to send name and number of whose text you clicked in intent
    }

    public String getContactName(final String phoneNumber,Context context)

    {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);

        if(cursor.moveToFirst())
        {

            contactName=cursor.getString(0);

        }
        cursor.close();
        cursor = null;

        return contactName;
    }

    private void getSharedUsers() throws IOException {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            String json = sharedPrefs.getString("users", null);
            Type type = new TypeToken<ArrayList<User>>() {}.getType();
            ArrayList<User> users = gson.fromJson(json, type);
            if(users != null) {
                user = users.get(0);
                Log.i("read", user.getName());
                Log.i("read", user.getNumber());
                Log.i("read", String.valueOf(user.getAverageToneLevels(0)));
            }
            else {
                getUsers();
                Log.i("get", user.getName());
                Log.i("get", user.getNumber());
                Log.i("get", String.valueOf(user.getAverageToneLevels(0)));
            }

    }
    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(users);

        editor.putString("users", json);
        editor.commit();
    }

    private void getUsers(){
        TelephonyManager tMgr =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        user = new User();
        user.setNumber(mPhoneNumber);
        user.setName("me");
        for(int j = 0; j < 5; j++) {
            user.setAverageToneLevels(j, j);
        }
        for(int j = 0; j < 3; j++) {
            user.setAverageStyleLevels(j, j);
        }
        for(int j = 0; j < 5; j++) {
            user.setAverageSocialLevels(j, j);
        }
        for(int j = 0; j < 7; j++) {
            user.setAverageUtteranceLevels(j, j);
        }
        users.add(user);
    }
}

