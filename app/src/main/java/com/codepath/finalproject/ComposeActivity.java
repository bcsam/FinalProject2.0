package com.codepath.finalproject;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by bcsam on 7/13/17.
 */

public class ComposeActivity extends Activity { // TODO: 7/17/17 put past messages in a recycler view
    Button btCheck;
    Button btSend;
    EditText etBody;
    EditText etNumber;
    AnalyzerClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        InitializeViews();
        etBody.setText(getIntent().getStringExtra("message"));
        etNumber.setText(getIntent().getStringExtra("recipient"));
        //unwrapIntent();
        setListeners();


        FragmentManager fm = getFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            ContactsFragment list = new ContactsFragment();

            fm.beginTransaction().replace(android.R.id.content, list).commit(); //TODO fix this
        }



        //This is an attempt to make the buttons appear and disappear
        /*
        etBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //btCheck.setVisibility(View.VISIBLE);
                //btCheck.setBackgroundColor(Color.parseColor("#D3D3D3")); // TODO: 7/12/17 abstract this
                if(etBody.getText().toString().trim().length()>0 && etName.getText().toString().trim().length()>0) {
                    btCheck.setBackgroundColor(Color.parseColor("#267326")); // TODO: 7/12/17 abstract this

                }else if(etBody.getText().toString().trim().length()>0 || etName.getText().toString().trim().length()>0){
                    btCheck.setBackgroundColor(Color.parseColor("#D3D3D3")); // TODO: 7/12/17 abstract this

                }else{
                    btCheck.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable str) {
                if(etBody.getText().toString().trim().length()>0 && etName.getText().toString().trim().length()>0) {
                    btCheck.setBackgroundColor(Color.parseColor("#267326")); // TODO: 7/12/17 abstract this

                }else if(etBody.getText().toString().trim().length()>0 || etName.getText().toString().trim().length()>0){
                    btCheck.setBackgroundColor(Color.parseColor("#D3D3D3")); // TODO: 7/12/17 abstract this

                }else{
                    btCheck.setVisibility(View.INVISIBLE);
                }
            }

        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setListeners() {
        final boolean messageEntered = !etBody.getText().toString().equals("");
        final boolean recipientEntered = !etNumber.getText().toString().equals("");

        btCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onCheck(); //check for fields filled is in onCheck()
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!etBody.getText().toString().equals("") && !etNumber.getText().toString().equals("")){
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

    }

    public void InitializeViews(){
        btCheck = (Button) findViewById(R.id.btCompCheck);
        btSend = (Button) findViewById(R.id.btCompSend);
        etBody = (EditText) findViewById(R.id.etBody);
        etNumber = (EditText) findViewById(R.id.etNumber);

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

            TextBody tb = new TextBody();
            tb.setMessage(message);
            client = new AnalyzerClient();
            client.getToneScores(tb);
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
        ComposeActivity.this.startActivity(i);
    }

    public void launchMyProfileActivity(MenuItem item) {
        //launches the profile view
        User user = new User();
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
}
