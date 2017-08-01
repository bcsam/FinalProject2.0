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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

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
    HashMap<String, String> getContactIdMemo;
    HashMap<String, String> getContactNameMemo;
    SharedPreferences sharedPrefs;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_ALL = 3;

    private static MainActivity ins;
    LinearLayoutManager layoutManager;

    String recipientName;
    String recipientNumber;
    String body;
    String date;
    String id;
    String textID;
    Boolean SMS;
    int type;
    Uri uri;
    ArrayList<SMS> onQuerySmsList = new ArrayList<>();
    ListAdapter adapter;
    Cursor c;
    Cursor c1;
    Cursor c2;

    TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;
        setContentView(R.layout.activity_main);
        users = new ArrayList<User>();
        rvText = (RecyclerView) findViewById(R.id.rvText);
        getPermissionToRead();

        int random = (int) Math.floor(Math.random() * 5);
        int color;

        switch (random) {
            case 0:  color = ContextCompat.getColor(this, R.color.newRed);
                break;
            case 1:  color = ContextCompat.getColor(this, R.color.newYellow);
                break;
            case 2:  color = ContextCompat.getColor(this, R.color.newPurple);
                break;
            case 3:  color = ContextCompat.getColor(this, R.color.newGreen);
                break;
            case 4:  color = ContextCompat.getColor(this, R.color.newBlue);
                break;
            default: color = ContextCompat.getColor(this, R.color.darkRed);
                break;
        }

        color = ContextCompat.getColor(this, R.color.teal);

        String hexColor = String.format("#%06X", (0xFFFFFF & color));

        //getSupportActionBar().setBackgroundDrawable(
        //        new ColorDrawable(Color.parseColor(hexColor)));


        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getSupportActionBar().setIcon(R.drawable.white_icon);
        getSupportActionBar().setTitle("ToneTeller");
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

                rvText.setAdapter(new ListAdapter(MainActivity.this, postQuerySmsList, incomingList, outgoingList, users));
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

                rvText.setAdapter(new ListAdapter(MainActivity.this, (ArrayList<com.codepath.finalproject.SMS>) onQuerySmsList, incomingList, outgoingList, users));
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
                rvText.setAdapter(new ListAdapter(MainActivity.this, smsList, incomingList, outgoingList, users));
                return true;
            }
        });

        return true;
    }

    public static MainActivity getInstance(){
        return ins;
    }

    public void launchMyProfileActivity(MenuItem item) {
        //launches the profile view
        User user = new User(this);
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
        if (!mPhoneNumber.equals("")) {
            user.setNumber("+" + mPhoneNumber); //this is why the + shows up
        }

        user.setName("Me");
        //user.setContactId(id);

        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
        i.putExtra("user", user);
        i.putParcelableArrayListExtra("incomingList", incomingList);
        i.putParcelableArrayListExtra("outgoingList", outgoingList);
        i.putParcelableArrayListExtra("users", users);
        MainActivity.this.startActivity(i);
    }

    public void launchComposeActivity(MenuItem item) {
        Intent i = new Intent(MainActivity.this, ComposeActivity.class);
        i.putParcelableArrayListExtra("incomingList", incomingList);
        i.putParcelableArrayListExtra("outgoingList", outgoingList);
        i.putParcelableArrayListExtra("users", users);
        MainActivity.this.startActivityForResult(i, 1);
        overridePendingTransition(R.anim.expand, 0);

    }

    public void getPermissionToRead() {
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
                text();
            } else {
                makeText(this, "Permission denied", LENGTH_SHORT).show();
            }

        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public String getContactName(final String phoneNumber, Context context)  {
        String memoizedResult = getContactNameMemo.get(phoneNumber);
        if(memoizedResult != null) {
            return memoizedResult;
        }
        if (phoneNumber == null || phoneNumber.equals("")) {
            return "Anonymous";
        }

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        c1 = context.getContentResolver().query(uri,projection,null,null,null);

        if(c1.moveToFirst())
        {

            contactName=c1.getString(0);

        }

        getContactNameMemo.put(phoneNumber, contactName);

        return contactName;
    }

    private String getContactId(String recipientNumber) {
        String memoizedResult = getContactIdMemo.get(recipientNumber);
        if (memoizedResult != null) {
            return memoizedResult;
        }

        ContentResolver contentResolver = this.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(recipientNumber));

        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        c2 = contentResolver.query(
                uri,
                projection,
                null,
                null,
                null);

        String id = null;
        if(c2 != null && c2.moveToNext()) {
            id = c2.getString(c2.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            c2.close();
        }
        if (id == null) {
            id = "";
        }

        getContactIdMemo.put(recipientNumber, id);
        return id;
    }

    private void text(){ // TODO: 7/17/17 rename this method
        FinalProject applicationClass = ((FinalProject)getApplicationContext());

        if (applicationClass.getSmsList() == null) {

            smsList = new ArrayList<SMS>();
            incomingList = new ArrayList<SMS>();
            outgoingList = new ArrayList<SMS>();
            getContactIdMemo = new HashMap<String, String>();
            getContactNameMemo = new HashMap<String, String>();

            uri = Uri.parse("content://sms/");
            c = getContentResolver().query(uri, new String[]{"address", "body", "type", "date"}, null, null, null);
            startManagingCursor(c);

            // Read the sms data and store it in the list
            if (c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    SMS sms = new SMS(this);
                    // TODO: 7/23/17 cleaning: isn't this a sender?
                    recipientNumber = c.getString(c.getColumnIndexOrThrow("address")).toString();
                    body = c.getString(c.getColumnIndexOrThrow("body")).toString();
                    date = c.getString(c.getColumnIndexOrThrow("date")).toString();
                    type = c.getInt(c.getColumnIndexOrThrow("type"));
                    //textID = c.getString(c.getColumnIndexOrThrow("_id"));
                    id = getContactId(recipientNumber);

                    recipientName = getContactName(recipientNumber, this);

                    sms.setBody(body);
                    sms.setNumber(recipientNumber);
                    sms.setContact(recipientName);
                    sms.setDate(date);
                    sms.setContactId(id);

                    sms.setType(type);
                    if(sms.getType() == 1) {
                        incomingList.add(sms);
                    }
                    else {
                        outgoingList.add(sms);
                    }

                    //if the sms is from a new person add it to the list
                    // TODO: 7/23/17 better way to do this?
                    boolean isAdded = false;
                    for (SMS text : smsList) {
                        if (matchNumber(sms, text)) {
                            isAdded = true;
                            break;
                        }
                    }

                    if (!isAdded) {
                        User user = new User(context);
                        user.setNumber(recipientNumber);
                        user.setName(recipientName);
                        user.setContactId(id);
                        users.add(user);
                        smsList.add(sms);
                    }

                    c.moveToNext();
                }
            }
            applicationClass.setSmsList(smsList);
            applicationClass.setIncomingList(incomingList);
            applicationClass.setOutgoingList(outgoingList);
        }
        else {
            smsList = applicationClass.getSmsList();
            incomingList = applicationClass.getIncomingList();
            outgoingList = applicationClass.getOutgoingList();
        }

        // Set smsList in the ListAdapter
        layoutManager = new LinearLayoutManager(this);
        rvText.setLayoutManager(layoutManager);
        adapter = new ListAdapter(this, smsList, incomingList, outgoingList, users);
        rvText.setAdapter(adapter);
    }

    public void updateInbox(String message, String from, String date) {
        /*FinalProject applicationClass = ((FinalProject)getApplicationContext());
        SMS sms = new SMS(this);

        sms.setBody(message);
        sms.setNumber(from);
        sms.setContact(getContactName(from, this));
        sms.setDate(date);
        sms.setContactId(getContactId(from));
        sms.setType(1);


        boolean isAdded = false;
        for (SMS text : smsList) {
            if (matchNumber(sms, text)) {
                isAdded = true;
                break;
            }
        }

        if (!isAdded) {
            User user = new User(context);
            user.setNumber(recipientNumber);
            user.setName(recipientName);
            user.setContactId(id);
            users.add(user);
            smsList.add(sms);
            incomingList.add(sms);

        }


        applicationClass.setSmsList(smsList);
        applicationClass.setIncomingList(incomingList);

        adapter = new ListAdapter(this, smsList, incomingList, outgoingList, users);
        rvText.setAdapter(adapter);
        layoutManager.scrollToPosition(0);*/
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
        if(c != null)
            c.close();
        if(c1 != null)
            c1.close();
        if(c2 != null)
            c2.close();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            ArrayList<SMS> newoutgoingList = data.getParcelableArrayListExtra("outgoingList");
            outgoingList = newoutgoingList;
            users = data.getParcelableArrayListExtra("users");
        } catch (NullPointerException e) {

        }
        text();
        adapter.notifyDataSetChanged();
        rvText.scrollToPosition(0);
    }

    interface DataTransfer{
        public void setValues(ArrayList<SMS> smsList, String contactName, String contactNumber);
    }
}