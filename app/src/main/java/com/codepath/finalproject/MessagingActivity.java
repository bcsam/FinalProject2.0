package com.codepath.finalproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
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
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by bcsam on 7/14/17.
 * For showing the users conversation with a specific individual
 */

//***when sending to message detail it should just send the name, number, and message
//****textBody will be created in message detail
public class MessagingActivity extends AppCompatActivity {

    ImageView btSend;
    Button btCheck;
    EditText etBody;
    ArrayList<SMS> messages;
    ArrayList<SMS> incomingList;
    ArrayList<SMS> outgoingList;
    ArrayList<User> users;
    User user;
    Uri uri;
    int position;

    String recipientName = "";
    String recipientNumber;
    public static MessagingActivity inst;
    //ListAdapter adapter;
    ConversationAdapter adapter;

    RecyclerView rvText;
    ArrayList<SMS> postQuerySmsList = new ArrayList<SMS>();

    String recipientId;

    String myId;
    String myNumber;
    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        initializeViews();
        setListeners();
        inst = this;

        SMS checkedText = getIntent().getParcelableExtra("text");

        if (checkedText != null) { //if clicked send from post check // TODO: 7/28/17 put the sent message on the right side
            recipientName = checkedText.getContact();
            recipientNumber = checkedText.getNumber();
            etBody.setText("");

            if (!recipientName.equals("")) {
                getSupportActionBar().setTitle(recipientName);
            } else {
                getSupportActionBar().setTitle(recipientNumber);
            }

            rvText = (RecyclerView) findViewById(R.id.rvMessaging);
            int position = getIntent().getIntExtra("position", 0);
            users = getIntent().getParcelableArrayListExtra("users");
            user = users.get(position);
            messages = user.getConversation();
            incomingList = getIntent().getParcelableArrayListExtra("incomingList");
            outgoingList = getIntent().getParcelableArrayListExtra("outgoingList");
            if(messages.size() == 0)
                getMessages();
            adapter = new ConversationAdapter(this, messages, incomingList, outgoingList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rvText.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            rvText.setAdapter(adapter);
            rvText.scrollToPosition(0);

            checkedText.setType(2);
            sendText(checkedText);
        }else {
            //stores this info to know which messages to bring up
            position = getIntent().getIntExtra("position", 0);
            users = getIntent().getParcelableArrayListExtra("users");
            user = users.get(position); // TODO: 8/1/17 Check for IOB and Null Pointer errors!!!!!!!
            recipientName = user.getName();
            recipientNumber = user.getNumber();
            String message = getIntent().getStringExtra("message");
            etBody.setText(message);


            if (!recipientName.equals("")) {
                getSupportActionBar().setTitle(recipientName);
            } else {
                getSupportActionBar().setTitle(recipientNumber);
            }


            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            myNumber = tMgr.getLine1Number();

        /*ContentResolver contentResolver = this.getContentResolver();

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
        }*/

            rvText = (RecyclerView) findViewById(R.id.rvMessaging);
            messages = user.getConversation();
            incomingList = getIntent().getParcelableArrayListExtra("incomingList");
            outgoingList = getIntent().getParcelableArrayListExtra("outgoingList");
            if(messages.size() == 0)
                getMessages();
            Log.i("messages", String.valueOf(messages.size()));
            adapter = new ConversationAdapter(this, messages, incomingList, outgoingList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rvText.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            rvText.setAdapter(adapter);
            rvText.scrollToPosition(0);
        }
    }

    public static MessagingActivity instance() {
        return inst;
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
                postQuerySmsList.clear();
                //insert query here
                //edit here down
                if(query.equals("")){
                    postQuerySmsList.clear();
                    adapter.notifyDataSetChanged();
                }else {
                    for (SMS text : messages) {
                        String body = text.getBody();

                        if (body.toLowerCase().contains(query.toLowerCase())) {
                            //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
                            postQuerySmsList.add(text);
                        }
                    }
                }
                rvText.setAdapter(new ConversationAdapter(MessagingActivity.this, postQuerySmsList, incomingList, outgoingList));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                postQuerySmsList.clear();
                //insert query here
                //edit here down
                for (SMS text : messages) {
                    String body = text.getBody();

                    if (body.toLowerCase().contains(newText.toLowerCase())) {
                        postQuerySmsList.add(text);
                    }
                }
                rvText.setAdapter(new ConversationAdapter(MessagingActivity.this, postQuerySmsList, incomingList, outgoingList));
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //rvText.scrollToPosition(0); //this scrolls and then opens the soft input
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                rvText.setAdapter(new ConversationAdapter(MessagingActivity.this, messages, incomingList, outgoingList));
                return true;
            }
        });

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    rvText.scrollToPosition(0);
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void launchComposeActivity(MenuItem item) {
        //launches the compose activity
        Intent i = new Intent(MessagingActivity.this, ComposeActivity.class);
        i.putExtra("incomingList", incomingList);
        i.putExtra("outgoingList", outgoingList);
        MessagingActivity.this.startActivity(i);
    }

    public void launchMyProfileActivity(MenuItem item) {
        //launches the profile view
        User user = new User();
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
        if (!mPhoneNumber.equals("")) {
            user.setNumber("+" + mPhoneNumber); //this is why the + shows up
        }

        user.setName("Me");
        Intent i = new Intent(MessagingActivity.this, ProfileActivity.class);

        i.putExtra("user", user);
        MessagingActivity.this.startActivity(i);
    }

    public void launchMainActivity(MenuItem item) {
        Intent i =  new Intent(MessagingActivity.this, MainActivity.class);
        i.putParcelableArrayListExtra("incomingList", incomingList);
        i.putParcelableArrayListExtra("outgoingList", outgoingList);
        setResult(RESULT_OK, i);
        finish();
    }

    public void initializeViews() {
        btSend = (ImageView) findViewById(R.id.btSend);
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
                    SMS text = new SMS();
                    text.setNumber(recipientNumber);
                    text.setContact(recipientName);
                    text.setBody(message);
                    intent.putExtra("text", text);
                    intent.putParcelableArrayListExtra("incomingList", incomingList);
                    intent.putParcelableArrayListExtra("outgoingList", outgoingList);
                    intent.putParcelableArrayListExtra("users", users);
                    intent.putExtra("position", position);
                    /*
                    intent.putExtra("message", message);
                    intent.putExtra("recipientName", recipientName); // TODO: 7/14/17 insert recipient here based on who you're texting
                    intent.putExtra("recipientNumber", recipientNumber); // TODO: 7/14/17 insert recipient number based on who you're texting
                    */
                    MessagingActivity.this.startActivity(intent);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(MessagingActivity.this, etBody, "message");
                    startActivity(intent, options.toBundle());
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
                    rvText.scrollToPosition(0);
                }
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidInput()) {
                    //sendText(v);
                    SMS text = new SMS();
                    text.setNumber(recipientNumber);
                    text.setBody(etBody.getText().toString());
                    text.setDate(String.valueOf(System.currentTimeMillis()));
                    text.setType(2);
                    sendText(text);
                    Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Invalid text", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getMessages() {
        Log.i("messages", "getMessages");
        for (SMS s : incomingList) {
            if (s.getNumber().equals(recipientNumber) || s.getNumber().equals("+" + recipientNumber)) {
                messages.add(s);
            }
        }
        for (SMS s : outgoingList) {
            if (s.getNumber().equals(recipientNumber) || s.getNumber().equals("+" + recipientNumber)) {
                int index = 0;
                for (SMS m : messages) {
                    if (Double.parseDouble(m.getDate()) < Double.parseDouble(s.getDate())) {
                        index = messages.indexOf(m);
                        break;
                    }
                }
                messages.add(index, s);
            }
        }
    }

    public void sendText(SMS text){
        etBody.setText("");
        //this would hide the keyboard
        //InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        text.sendSMS();
        messages.add(0, text);
        adapter.notifyItemInserted(0);
        outgoingList.add(0, text);
        rvText.scrollToPosition(0);
    }

    public boolean isValidInput(){
        if(etBody.length()>0){
            return true;
        }
        return false;
    }

    @Override
    public void onPause(){
        messages = adapter.getModifyList();
        user.setConversation(messages);
        super.onPause();
    }

    @Override
    public void onBackPressed(){
        Intent i =  new Intent(MessagingActivity.this, MainActivity.class);
        i.putParcelableArrayListExtra("incomingList", incomingList);
        i.putParcelableArrayListExtra("outgoingList", outgoingList);
        i.putParcelableArrayListExtra("users", users);
        setResult(RESULT_OK, i);
        finish();
    }
}

