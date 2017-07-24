package com.codepath.finalproject;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvText;
    ArrayList<User> users;
    Context context;
    ArrayList<SMS> smsList;
    ArrayList<SMS> incomingList;
    ArrayList<SMS> outgoingList;
    SharedPreferences sharedPrefs;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_ALL = 3;

    private static MainActivity ins;

    String recipientName;
    String recipientNumber;
    String body;
    String date;
    String read;
    String id;
    Boolean SMS;
    int type;
    Uri uri;
    Cursor c;
    ArrayList<SMS> onQuerySmsList = new ArrayList<>();

    TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        users = new ArrayList<User>();
        rvText = (RecyclerView) findViewById(R.id.rvText);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("incomingList", null);
        Type type = new TypeToken<ArrayList<SMS>>() {}.getType();
        incomingList = gson.fromJson(json, type);
        json = sharedPrefs.getString("outgoingList", null);
        type = new TypeToken<ArrayList<SMS>>() {}.getType();
        outgoingList = gson.fromJson(json, type);
        if(incomingList == null && outgoingList == null)
            getPermissionToRead();
        else
            Log.i("sharedPreferences", String.valueOf(smsList.size()));
        ins = this;


        //if statement for requesting info
        /*if (ContextCompat.checkSelfPermission(this,
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

        SMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED;
        contact = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;

        if (SMS && contact) {
            text();
        }
        /*
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                User OtherUser = new User();
                OtherUser.setNumber(recipientNumber);
                OtherUser.setName(recipientName);
                MainActivity.this.startActivity(i);
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //this works
                searchView.clearFocus();

                //insert query here
                ArrayList<SMS> postQuerySmsList = new ArrayList<>();
                for (SMS text : smsList) {
                    String number = text.getNumber();
                    String body = text.getBody();
                    String contact = text.getContact();

                    if (number.toLowerCase().contains(query.toLowerCase()) ||
                            body.toLowerCase().contains(query.toLowerCase()) ||
                            contact.toLowerCase().contains(query.toLowerCase())) {

                        makeText(getApplicationContext(), query,
                                LENGTH_LONG).show();
                        postQuerySmsList.add(text);
                    }
                }

                rvText.setAdapter(new ListAdapter(MainActivity.this, postQuerySmsList, incomingList, outgoingList));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onQuerySmsList.clear();
                onQuerySmsList.clear();
                for (SMS text : smsList) {
                    String number = text.getNumber();
                    String body = text.getBody();
                    String contact = text.getContact();

                    //Uri profileImage = text.getPhotoUri();

                    if (number.toLowerCase().contains(newText.toLowerCase()) ||
                            body.toLowerCase().contains(newText.toLowerCase()) ||
                            contact.toLowerCase().contains(newText.toLowerCase())) {
                        onQuerySmsList.add(text);
                    }
                }

                rvText.setAdapter(new ListAdapter(MainActivity.this, (ArrayList<com.codepath.finalproject.SMS>) onQuerySmsList, incomingList, outgoingList));
                return true;
            }
        });


        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener(){

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                rvText.setAdapter(new ListAdapter(MainActivity.this, smsList, incomingList, outgoingList));
                Toast.makeText(getApplicationContext(), "working",
                        Toast.LENGTH_LONG).show();
                return true;
            }
        });

        return true;
    }

    public static MainActivity getInstace(){
        return ins;
    }

    public void launchMyProfileActivity(MenuItem item) {
        //launches the profile view
        User user = new User(this);
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
        user.setNumber("+"+mPhoneNumber);
        user.setName("Me");
        //user.setContactId(id);

        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
        i.putExtra("user", user);
        MainActivity.this.startActivity(i);
    }

    public void launchComposeActivity(MenuItem item) {
        Intent i = new Intent(MainActivity.this, ComposeActivity.class);
        i.putParcelableArrayListExtra("incomingList", incomingList);
        i.putParcelableArrayListExtra("outgoingList", outgoingList);
        MainActivity.this.startActivity(i);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
       // SMS sms = (SMS) getListAdapter().getItem(position);

        Intent intent = new Intent(this, MessagingActivity.class);
        intent.putExtra("recipientName", recipientName);
        intent.putExtra("recipientNumber", recipientNumber);


        startActivity(intent);

        //want to send to MessageActivity
        //want to send name and number of whose text you clicked in intent
    }

    public void getPermissionToRead() {
        Log.i("sharedPreferences", "getPermissionToRead");
        boolean readSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED;
        boolean readContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED;
        boolean sendSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED;

        if (readSMS || readContacts || sendSMS) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_READ_ALL);
        }
        else {
            Log.i("Main Activity", "getPermission");
            text();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_ALL) {
            if (grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                makeText(this, "Permission granted", LENGTH_SHORT).show();
                Log.i("Main Activity", "onRequest");
                text();
            } else {
                makeText(this, "Permission denied", LENGTH_SHORT).show();
            }

        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public String getContactName(final String phoneNumber,Context context)

    {

        if (phoneNumber == null || phoneNumber.equals("")) {
            return "Anonymous";
        }

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

    private void text(){ // TODO: 7/17/17 rename this method
        smsList = new ArrayList<SMS>();
        incomingList = new ArrayList<SMS>();
        outgoingList = new ArrayList<SMS>();
        //ContentResolver contentResolver = context.getContentResolver();

        //Uri uriContact = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        //String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        uri = Uri.parse("content://sms/");
        c = getContentResolver().query(uri, null, null, null, null);
        startManagingCursor(c);

        // Read the sms data and store it in the list
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                SMS sms = new SMS(this);
                // TODO: 7/23/17 cleaning: isn't this a sender?
                recipientNumber = c.getString(c.getColumnIndexOrThrow("address")).toString();
                body = c.getString(c.getColumnIndexOrThrow("body")).toString();
                date = c.getString(c.getColumnIndexOrThrow("date")).toString();
                read = c.getString(c.getColumnIndexOrThrow("read")).toString();
                type = c.getInt(c.getColumnIndexOrThrow("type"));

                ContentResolver contentResolver = this.getContentResolver();

                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(recipientNumber));

                String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

                /*Cursor cursor =
                        contentResolver.query(
                                uri,
                                projection,
                                null,
                                null,
                                null);

                if(cursor != null) {
                    while(cursor.moveToNext()){
                        id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                        Log.i("in cursor", id);
                    }
                    Log.i("out cursor", id);
                    cursor.close();
                }*/
                recipientName = getContactName(recipientNumber, this);

                sms.setBody(body);
                sms.setNumber(recipientNumber);
                sms.setContact(recipientName);
                sms.setDate(date);
                sms.setRead(read);
                sms.setContactId(id);
                id = "";

                sms.setType(type);
                if(sms.getType() == 1) {
                    incomingList.add(sms);
                }
                else {
                    outgoingList.add(sms);
                }

                //if the sms is from a new person add it to the list
                // TODO: 7/23/17 better way to do this?
                int count = 0;
                for (SMS text : smsList) {
                     if(!matchNumber(sms, text)){
                        count++;
                     }
                }

                if (count == smsList.size()) {
                    smsList.add(sms);
                }

                c.moveToNext();
            }
        }
        // Set smsList in the ListAdapter
        rvText.setLayoutManager(new LinearLayoutManager(this));
        rvText.setAdapter(new ListAdapter(this, smsList, incomingList, outgoingList));
    }

    public void updateInbox(String smsMessageStr) {
        text();
    }

    public boolean matchNumber(SMS sms, SMS text){
        if(!sms.getNumber().equals(text.getNumber()) && !("+1" + sms.getNumber()).equals(text.getNumber()) && !("1" + sms.getNumber()).equals(text.getNumber())
                && !("+" + sms.getNumber()).equals(text.getNumber())&& !(sms.getNumber()).equals("1" + text.getNumber()) &&
                !(sms.getNumber()).equals("+1" + text.getNumber()) && !(sms.getNumber()).equals("+" + text.getNumber()))
            return false;
        return true;
    }


            @Override
            protected void onDestroy() {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                Gson gson = new Gson();
                String iJson = gson.toJson(incomingList);
                String oJson = gson.toJson(outgoingList);
                editor.putString("incomingList", iJson);
                editor.putString("outgoingList", oJson);
                editor.commit();
                c.close();
                super.onDestroy();
            }
}