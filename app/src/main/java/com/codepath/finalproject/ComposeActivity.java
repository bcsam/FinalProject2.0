package com.codepath.finalproject;

//import android.suppapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by bcsam on 7/13/17.
 */

public class ComposeActivity extends AppCompatActivity { // TODO: 7/17/17 put past messages in a recycler view
    Button btCheck;
    ImageView btSend;
    EditText etBody;
    EditText etNumber;
    AnalyzerClient client;
    ArrayList<User> contacts;
    RecyclerView rvCompose;
    ComposeAdapter composeAdapter;
    ArrayList<User> postQueryContacts;
    String query;
    ArrayList<SMS> incomingList = new ArrayList<>();
    ArrayList<SMS> outgoingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        InitializeViews();
        setListeners();

        incomingList = getIntent().getParcelableArrayListExtra("incomingList");
        outgoingList = getIntent().getParcelableArrayListExtra("outgoingList");

        rvCompose = (RecyclerView) findViewById(R.id.rvCompose);
        addContacts(); //populates contacts
        postQueryContacts = new ArrayList<>();

        // TODO: 7/23/17 figure out how to pass the in-flight message to the messaging activity
        composeAdapter = new ComposeAdapter(ComposeActivity.this, postQueryContacts, incomingList, outgoingList);
        rvCompose.setLayoutManager(new LinearLayoutManager(this));
        rvCompose.setAdapter(composeAdapter);

/*
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/


        etBody.setText(getIntent().getStringExtra("message"));
        etNumber.setText(getIntent().getStringExtra("recipient"));
        //unwrapIntent();
    }

    public void addContacts(){


        try {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            contacts = new ArrayList<>();
            while (phones.moveToNext()) { // TODO: 7/23/17 change how read in 
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                //sets phone number like +1 555-555-5555
                String phoneNumber = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //String phoneNumber = phones.getString(phones.getColumnIndexOrThrow("address")).toString();
                User newContact = new User();
                newContact.setName(name);
                newContact.setNumber(phoneNumber);

                contacts.add(newContact);
                //Log.e("Contact list with name & numbers", " "+contacts);
            }
            phones.close(); //look for cursor errors here
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_unsearchable, menu);
        return true;

    }

    private void setListeners() {


        btCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheck(); //check for fields filled is in onCheck()
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean messageEntered = !etBody.getText().toString().equals("");
                final boolean recipientEntered = !etNumber.getText().toString().equals("");
                if(messageEntered && recipientEntered){
                    sendText(view);
                    Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
                    etBody.setText("");
                    etNumber.setText("");

                }else if(etNumber.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter a recipient!",
                            Toast.LENGTH_LONG).show();

                }else if(etBody.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a message!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        etNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                postQueryContacts.clear();
                composeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString();
                postQueryContacts.clear();

                if(query.equals("")){
                    postQueryContacts.clear();
                    composeAdapter.notifyDataSetChanged();
                }else {
                    for (User contact : contacts) {
                        String name = contact.getName();
                        String number = contact.getNumber();
                        if (name.toLowerCase().contains(query.toLowerCase()) ||
                                number.toLowerCase().contains(query.toLowerCase())) {
                            postQueryContacts.add(contact);
                            composeAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                query = s.toString();
                postQueryContacts.clear();

                if(query.equals("")){
                    postQueryContacts.clear();
                    composeAdapter.notifyDataSetChanged();
                }else {
                    for (User contact : contacts) {
                        String name = contact.getName();
                        String number = contact.getNumber();
                        if (name.toLowerCase().contains(query.toLowerCase()) ||
                                number.toLowerCase().contains(query.toLowerCase())) {
                            postQueryContacts.add(contact);
                            composeAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }


    public void InitializeViews(){
        btCheck = (Button) findViewById(R.id.btComp2Check);
        btSend = (ImageView) findViewById(R.id.btComp2Send);
        etBody = (EditText) findViewById(R.id.etComp2Body);
        etNumber = (EditText) findViewById(R.id.etComp2Number);

        //etSubject = (EditText) findViewById(R.id.etSubject);
    }

    public void unwrapIntent(){
        String recipient = getIntent().getStringExtra("recipient");
        if (recipient != null){
            etNumber.setText(recipient);
        }

        String message = getIntent().getStringExtra("message");
        if (message != null){
            etBody.setText(message);
        }
    }

    /**
     *
     */
    public void onCheck(){
        String message = etBody.getText().toString();
        String recipientName = etNumber.getText().toString();
        //String subject = etSubject.getText().toString();

        if(!message.equals("") && !recipientName.equals("")){
            Intent intent = new Intent(ComposeActivity.this, PostCheckActivity.class);
            intent.putExtra("message", message);
            // TODO: 7/14/17 send the number and name of the recipient
            intent.putExtra("recipientName", recipientName);
            //intent.putExtra("subject", subject);

            SMS sms = new SMS();
            sms.setBody(message);
            client = new AnalyzerClient();
            client.getScores(sms);
            ComposeActivity.this.startActivity(intent);

            //makes the user enter a message before submitting
        }else if (message.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter a message!",
                    Toast.LENGTH_LONG).show();

            //makes the user enter a recipient before submitting
        }else {
            Toast.makeText(getApplicationContext(), "Please enter a recipient!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void sendText(View view){
        SMS text = new SMS();
        text.setNumber(etNumber.getText().toString());
        text.setBody(etBody.getText().toString());
        text.sendSMS();
    }

    public void launchComposeActivity(MenuItem item) {
        //launches the profile view
        Intent i = new Intent(ComposeActivity.this, ComposeActivity.class);
        i.putParcelableArrayListExtra("incomingList", incomingList);
        i.putParcelableArrayListExtra("outgoingList", outgoingList);
        ComposeActivity.this.startActivity(i);
    }

    public void launchMyProfileActivity(MenuItem item) {
        //launches the profile view
        User user = new User(this);
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
        user.setNumber(mPhoneNumber);
        user.setName("Me");
        Log.i("profile", user.getNumber()); //delete afterwards
        Log.i("profile", user.toStringNumber());
        Intent i = new Intent(ComposeActivity.this, ProfileActivity.class);

        i.putExtra("user", user);
        ComposeActivity.this.startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ComposeActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void launchMainActivity(MenuItem item){
        Intent i = new Intent(ComposeActivity.this, MainActivity.class);
        ComposeActivity.this.startActivity(i);
    }


}
