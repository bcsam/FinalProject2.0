package com.codepath.finalproject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
public class MessagingActivity extends AppCompatActivity { //TODO: 8/1/17 messaging ->mod, don't chnge the bars on either side
    // TODO: 8/1/17 animation out

    ImageView btSend;
    Button btCheck;
    EditText etBody;
    ArrayList<SMS> messages;
    ArrayList<SMS> incomingList;
    ArrayList<SMS> outgoingList;
    ArrayList<User> users;
    ArrayList<SMS> totalList;
    User user;
    Uri uri;
    int position;
    Boolean checkable = false; //for button feedback
    SearchView searchView;

    LinearLayoutManager layoutManager;

    Cursor c1;
    Cursor c2;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        initializeViews();
        inst = this;

        FinalProject applicationClass = ((FinalProject)getApplicationContext());
        totalList = applicationClass.getSmsList();

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
            if (position != -1) {
                user = users.get(position);
            }
            messages = user.getConversation();
            incomingList = getIntent().getParcelableArrayListExtra("incomingList");
            outgoingList = getIntent().getParcelableArrayListExtra("outgoingList");
            if(messages.size() == 0)
                getMessages();

            adapter = new ConversationAdapter(this, messages, incomingList, outgoingList, users);

            layoutManager = new LinearLayoutManager(this);
            rvText.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            int lastVisible = layoutManager.findLastVisibleItemPosition();

            rvText.setAdapter(adapter);
            rvText.scrollToPosition(0);
            int numVisibleItems = rvText.getChildCount();

            checkedText.setType(2);
            sendText(checkedText);

            rvText.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // TODO: 8/3/17 check if this code works

                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if(bottom < oldBottom){
                        rvText.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rvText.smoothScrollToPosition(0);
                            }
                        }, 100);
                    }
                }
            });
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


            adapter = new ConversationAdapter(this, messages, incomingList, outgoingList, users);

            layoutManager = new LinearLayoutManager(this);
            rvText.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            int lastVisible = layoutManager.findLastVisibleItemPosition();
            int lastVis = rvText.getChildCount();
            rvText.setAdapter(adapter);
            rvText.scrollToPosition(0);
        }

        if (etBody.getText().toString().equals("")) {
            btCheck.setBackgroundColor(getColor(R.color.uncheckButton));
        }else {
            btCheck.setBackgroundColor(getColor(R.color.checkButton));
        }
        setListeners();
    }

    public static MessagingActivity instance() {
        return inst;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        final Menu theMenu = menu;
        getMenuInflater().inflate(R.menu.menu_searchable, menu);
        MenuItem searchItem = menu.findItem(R.id.miSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
                rvText.setAdapter(new ConversationAdapter(MessagingActivity.this, postQuerySmsList, incomingList, outgoingList, users));

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

                rvText.setAdapter(new ConversationAdapter(MessagingActivity.this, postQuerySmsList, incomingList, outgoingList, users));

                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                rvText.scrollToPosition(0);
                theMenu.findItem(R.id.miProfile).setVisible(false);
                theMenu.findItem(R.id.miMain).setVisible(false);
                theMenu.findItem(R.id.miCompose).setVisible(false);
                //rvText.scrollToPosition(0); //this scrolls and then opens the soft input

                //postQuerySmsList.clear();
                //rvText.setAdapter(new ListAdapter(MessagingActivity.this, postQuerySmsList, incomingList, outgoingList, users));
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                theMenu.findItem(R.id.miProfile).setVisible(true);
                theMenu.findItem(R.id.miMain).setVisible(true);
                theMenu.findItem(R.id.miCompose).setVisible(true);

                //rvText.setAdapter(new ConversationAdapter(MessagingActivity.this, messages, incomingList, outgoingList, users));
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
        i.putExtra("users", users);
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
        i.putExtra("users", users);
        i.putExtra("user", user);
        MessagingActivity.this.startActivity(i);
    }

    public void launchMainActivity(MenuItem item) {
        Intent i =  new Intent(MessagingActivity.this, MainActivity.class);
        i.putParcelableArrayListExtra("incomingList", incomingList);
        i.putParcelableArrayListExtra("outgoingList", outgoingList);
        i.putParcelableArrayListExtra("users", users);
        setResult(RESULT_OK, i);
        finish();
    }

    public void initializeViews() {
        btSend = (ImageView) findViewById(R.id.btSend);
        btCheck = (Button) findViewById(R.id.btCheck);
        etBody = (EditText) findViewById(R.id.etBody);

    }

    public void setListeners() {
        etBody.addTextChangedListener(new TextWatcher() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().equals("")) {
                    btCheck.setBackgroundColor(getColor(R.color.uncheckButton));
                    checkable = false;
                }else{
                    btCheck.setBackgroundColor(getColor(R.color.checkButton));
                    checkable = true;
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")) {
                    btCheck.setBackgroundColor(getColor(R.color.uncheckButton));
                    checkable = false;
                }else{
                    btCheck.setBackgroundColor(getColor(R.color.checkButton));
                    checkable = true;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    btCheck.setBackgroundColor(getColor(R.color.uncheckButton));
                    checkable = false;
                }else{
                    btCheck.setBackgroundColor(getColor(R.color.checkButton));
                    checkable = true;
                }
            }
        });

        /*
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
                    intent.putExtra("activity", "Messaging");
                    intent.putExtra("text", text);
                    intent.putParcelableArrayListExtra("incomingList", incomingList);
                    intent.putParcelableArrayListExtra("outgoingList", outgoingList);
                    intent.putParcelableArrayListExtra("users", users);
                    intent.putExtra("position", position);
                    /*
                    intent.putExtra("message", message);
                    intent.putExtra("recipientName", recipientName); // TODO: 7/14/17 insert recipient here based on who you're texting
                    intent.putExtra("recipientNumber", recipientNumber); // TODO: 7/14/17 insert recipient number based on who you're texting

                    MessagingActivity.this.startActivity(intent);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a message!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });*/

        btCheck.setOnTouchListener(new View.OnTouchListener(){

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btCheck.setBackgroundColor(getColor(R.color.pressCheck));
                    return true;
                }else if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(checkable) {
                        btCheck.setBackgroundColor(getColor(R.color.checkButton));
                    }else{
                        btCheck.setBackgroundColor(getColor(R.color.uncheckButton));
                    }


                    Intent intent = new Intent(MessagingActivity.this, PostCheckActivity.class);
                    String message = etBody.getText().toString();
                    if (!message.equals("")) {
                        SMS text = new SMS();
                        text.setNumber(recipientNumber);
                        text.setContact(recipientName);
                        text.setBody(message);
                        intent.putExtra("activity", "Messaging");
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
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter a message!",
                                Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
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

        rvText.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom < oldBottom){
                    rvText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rvText.smoothScrollToPosition(0);
                        }
                    }, 100);
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

    public String getContactName(final String phoneNumber, Context context)  {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        c1 = context.getContentResolver().query(uri,projection,null,null,null);

        if(c1.moveToFirst())
        {

            contactName=c1.getString(0);

        }

        return contactName;
    }

    private String getContactId(String recipientNumber) {

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

        return id;
    }

    public boolean matchNumber(SMS sms, SMS text){
        if(!sms.getNumber().equals(text.getNumber()) && !("+1" + sms.getNumber()).equals(text.getNumber()) && !("1" + sms.getNumber()).equals(text.getNumber())
                && !("+" + sms.getNumber()).equals(text.getNumber())&& !(sms.getNumber()).equals("1" + text.getNumber()) &&
                !(sms.getNumber()).equals("+1" + text.getNumber()) && !(sms.getNumber()).equals("+" + text.getNumber()))
            return false;
        return true;
    }

    public void updateInbox(String message, String from, String date) {
        FinalProject applicationClass = ((FinalProject)getApplicationContext());
        SMS sms = new SMS(this);
        String name = getContactName(from, this);
        String id = getContactId(from);

        sms.setBody(message);
        sms.setNumber(from);
        sms.setContact(getContactName(from, this));
        sms.setDate(date);
        sms.setContactId(id);
        sms.setType(1);

        boolean isAdded = false;

        for (User u : users) {
            if (u.getName().equals(name)) {
                isAdded = true;
                break;
            }
        }

        if (isAdded == false) {
            User user = new User(this);
            user.setNumber(from);
            user.setName(name);
            user.setContactId(id);
            users.add(user);
        }

        if (sms.getContact().equals(recipientName)) {
            messages.add(0, sms);
        }

        incomingList.add(0, sms);
        totalList.add(0, sms);

        applicationClass.setSmsList(totalList);
        applicationClass.setIncomingList(incomingList);

        adapter = new ConversationAdapter(this, messages, incomingList, outgoingList, users);

        rvText.setAdapter(adapter);
        layoutManager.scrollToPosition(0);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i("Profile", "onActivityResult");
        users = data.getParcelableArrayListExtra("users");
        adapter.setUserList(users);
    }

    @Override
    public void onResume(){
        if(searchView != null)
            searchView.onActionViewCollapsed();
        super.onResume();
    }
}

