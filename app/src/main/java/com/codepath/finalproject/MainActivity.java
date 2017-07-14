package com.codepath.finalproject;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity { // TODO: 7/12/17 make the app work if the device is turned sideways

    RecyclerView rvText;
    User user;
    ArrayList<User> users;
    FileInputStream is;
    FileOutputStream os;
    BufferedReader reader;
    BufferedWriter writer;
    final File file = new File("users.txt");

    private static final int  MY_PERMISSIONS_REQUEST_READ_SMS = 1;
    private static final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;

    String recipientName;
    String recipientNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                recipientNumber = c.getString(c.getColumnIndexOrThrow("address")).toString(); //think this is the name of who sent it
                String body = c.getString(c.getColumnIndexOrThrow("body")).toString();
                recipientName = getContactName(recipientNumber, this); //make sure that the body is written by who you think it is


                sms.setBody(body);
                sms.setNumber(recipientNumber);
                sms.setContact(recipientName);

                smsList.add(sms);

                c.moveToNext();
            }
        }
        c.close();
        try {
            parseFile();
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

        Toast.makeText(getApplicationContext(), sms.getBody(), Toast.LENGTH_LONG).show();
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

    private void parseFile() throws IOException {
        is = new FileInputStream("users.txt");
        if (is != null) {
            reader = new BufferedReader(new InputStreamReader(is));
            int numberUsers = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numberUsers; i++) {
                String line = reader.readLine();
                User newUser = new User();
                newUser.setNumber(line.split(" ")[0]);
                newUser.setName(line.split(" ")[1]);
                line = reader.readLine();
                String[] toneScores = line.split(" ");
                for(int j = 0; j < 5; j++) {
                    newUser.setAverageToneLevels(j, Integer.parseInt(toneScores[j]));
                }
                line = reader.readLine();
                String[] styleScores = line.split(" ");
                for(int j = 0; j < 3; j++) {
                    newUser.setAverageStyleLevels(j, Integer.parseInt(styleScores[j]));
                }
                line = reader.readLine();
                String[] socialScores = line.split(" ");
                for(int j = 0; j < 5; j++) {
                    newUser.setAverageSocialLevels(j, Integer.parseInt(socialScores[j]));
                }
                line = reader.readLine();
                String[] utteranceScores = line.split(" ");
                for(int j = 0; j < 7; j++) {
                    newUser.setAverageUtteranceLevels(j, Integer.parseInt(utteranceScores[j]));
                }
                users.add(newUser);
            }
            Log.i("read", user.getName());
            Log.i("read", user.getNumber());
            Log.i("read", String.valueOf(user.getAverageToneLevels(0)));
        }
        else{
            getUsers();
            Log.i("get", user.getName());
            Log.i("get", user.getNumber());
            Log.i("get", String.valueOf(user.getAverageToneLevels(0)));
        }

    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.i("onStop", "stopped");
        try {
            os = openFileOutput("users.txt", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(users.size());
            writer.newLine();
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                writer.write(u.getNumber() + " " + u.getName());
                writer.newLine();
                for(int j = 0; j < 5; j++) {
                    writer.write(u.getAverageToneLevels(j));
                }
                for(int j = 0; j < 3; j++) {
                    writer.write(u.getAverageStyleLevels(j));
                }
                for(int j = 0; j < 5; j++) {
                    writer.write(u.getAverageSocialLevels(j));
                }
                for(int j = 0; j < 7; j++) {
                    writer.write(u.getAverageUtteranceLevels(j));
                }
            }
            Log.i("onStop", "written");
        } catch (FileNotFoundException e) {
            Log.e("Exception", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Exception", "IO exception: " + e.toString());
        }
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
    }
}

