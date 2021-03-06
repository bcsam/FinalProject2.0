package com.codepath.finalproject;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bcsam on 7/11/17.
 */

public class PostCheckActivity extends AppCompatActivity { // TODO: 8/1/17 edit takes you to where you were before 

    TextView tvBody;
    SMS text;
    String activity;
    String message;
    String recipientName;
    String recipientNumber;
    Button btSend;
    Button btEdit;
    ArrayList<SMS> incomingList = new ArrayList<>();
    ArrayList<SMS> outgoingList = new ArrayList<>();
    ArrayList<SMS> smsList;
    ArrayList<User> users;
    HashMap<String, String> getContactIdMemo;
    int position;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        //sets up the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post_check);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvBody.setVisibility(View.INVISIBLE);


        //stores info in intent for sending back to MainActivity
        /*
        message = getIntent().getStringExtra("message");
        recipientName = getIntent().getStringExtra("recipientName");
        recipientNumber = getIntent().getStringExtra("recipientNumber");
        */
        getSupportActionBar().setTitle("Sherlock");
        text = getIntent().getParcelableExtra("text");
        tvBody.setText(text.getBody());

        activity = getIntent().getStringExtra("activity");
        incomingList = getIntent().getParcelableArrayListExtra("incomingList");
        outgoingList = getIntent().getParcelableArrayListExtra("outgoingList");
        smsList = getIntent().getParcelableArrayListExtra("smsList");
        users = getIntent().getParcelableArrayListExtra("users");
        position = getIntent().getIntExtra("position", -1);

        //makes a Textbody with the user's message

        /*
        sms = new SMS();
        sms.setBody(message);
        */

        //gets the textbody's score and puts them in textBody
        AnalyzerClient client = new AnalyzerClient();
        client.getScores(text);
        tvBody.setTextColor(Color.parseColor(text.getTextColor()));
        //sets the message on the activity



        //Code for tabs below

        // Locate the viewpager in activity_compose.xml
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TonesFragment(), "Tones", text, "PostCheckActivity");
        adapter.addFrag(new StylesFragment(), "Styles", text, "PostCheckActivity");
        adapter.addFrag(new SocialFragment(), "Social", text, "PostCheckActivity");
        //adapter.addFrag(new UtteranceFragment(), "Utterance", textBody, "PostCheckActivity");

        viewPager.setAdapter(adapter);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.pager_header);
        mTabLayout.setupWithViewPager(viewPager);

        initializeViews();
        setOnClickListeners();
    }

    @Override
    public void onEnterAnimationComplete(){
        tvBody.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_unsearchable, menu);
        return true;
    }

    public void setOnClickListeners(){

        /*btEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent;
                if (activity.equals("Messaging")) {
                    intent = new Intent(PostCheckActivity.this, MessagingActivity.class);
                } else {
                    intent = new Intent(PostCheckActivity.this, ComposeActivity.class);
                }
                for(User u: users){
                    if(u.getNumber().equals(text.getNumber()))
                        position = users.indexOf(u);
                }
                if(position == -1){
                    User newUser = new User();
                    newUser.setNumber(text.getNumber());
                    newUser.setName(text.getContact());
                    newUser.setContactId(getContactId(text.getNumber()));
                    users.add(users.size(), newUser);
                    position = users.indexOf(newUser);
                }
                intent.putExtra("position", position);
                intent.putExtra("message", text.getBody());
                intent.putExtra("name", text.getContact());
                intent.putExtra("number", text.getNumber());
                intent.putParcelableArrayListExtra("incomingList", incomingList);
                intent.putParcelableArrayListExtra("outgoingList", outgoingList);
                intent.putParcelableArrayListExtra("smsList", smsList);
                intent.putParcelableArrayListExtra("users", users);
                PostCheckActivity.this.startActivity(intent);
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // TODO: 7/28/17 send this to the messaging activity
                    Intent intent = new Intent(PostCheckActivity.this, MessagingActivity.class);
                    text.setDate(String.valueOf(System.currentTimeMillis()));
                    intent.putExtra("text", text);
                    intent.putParcelableArrayListExtra("incomingList", incomingList);
                    intent.putParcelableArrayListExtra("outgoingList", outgoingList);
                    intent.putParcelableArrayListExtra("smsList", smsList);
                    intent.putParcelableArrayListExtra("users", users);
                    intent.putExtra("position", position);
                /*
                    intent.putExtra("name", recipientName);
                    intent.putExtra("number", recipientNumber);
                    intent.putExtra("message", message);

                    startActivity(intent);
            }
        });
        */
        btEdit.setOnTouchListener(new View.OnTouchListener(){

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btEdit.setBackgroundColor(getColor(R.color.colorAccent));
                    return true;
                }else if(event.getAction() == MotionEvent.ACTION_UP) {
                    btEdit.setBackgroundColor(getColor(R.color.checkButton));

                    Intent intent;
                    if (activity.equals("Messaging")) {
                        intent = new Intent(PostCheckActivity.this, MessagingActivity.class);
                    } else {
                        intent = new Intent(PostCheckActivity.this, ComposeActivity.class);
                    }
                    for(User u: users){
                        if(u.getNumber().equals(text.getNumber()))
                            position = users.indexOf(u);
                    }
                    if(position == -1){
                        User newUser = new User();
                        newUser.setNumber(text.getNumber());
                        newUser.setName(text.getContact());
                        newUser.setContactId(getContactId(text.getNumber()));
                        users.add(users.size(), newUser);
                        position = users.indexOf(newUser);
                    }
                    intent.putExtra("position", position);
                    intent.putExtra("message", text.getBody());
                    intent.putExtra("name", text.getContact());
                    intent.putExtra("number", text.getNumber());
                    intent.putParcelableArrayListExtra("incomingList", incomingList);
                    intent.putParcelableArrayListExtra("outgoingList", outgoingList);
                    intent.putParcelableArrayListExtra("smsList", smsList);
                    intent.putParcelableArrayListExtra("users", users);
                    PostCheckActivity.this.startActivity(intent);
                }
                return false;
            }
        });

        btSend.setOnTouchListener(new View.OnTouchListener(){

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btSend.setBackgroundColor(getColor(R.color.colorAccent));
                    return true;
                }else if(event.getAction() == MotionEvent.ACTION_UP) {
                    btSend.setBackgroundColor(getColor(R.color.checkButton));

                    Intent intent = new Intent(PostCheckActivity.this, MessagingActivity.class);
                    text.setDate(String.valueOf(System.currentTimeMillis()));
                    intent.putExtra("text", text);
                    intent.putParcelableArrayListExtra("incomingList", incomingList);
                    intent.putParcelableArrayListExtra("outgoingList", outgoingList);
                    intent.putParcelableArrayListExtra("smsList", smsList);
                    intent.putParcelableArrayListExtra("users", users);
                    intent.putExtra("position", position);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initializeViews(){
        btSend = (Button) findViewById(R.id.btSend);
        btEdit = (Button) findViewById(R.id.btEdit);
        btEdit.setBackgroundColor(getColor(R.color.colorPrimary));
        btSend.setBackgroundColor(getColor(R.color.colorPrimary));
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
            private final List<Fragment> mFragmentList = new ArrayList<>();
            private final List<String> mFragmentTitleList = new ArrayList<>();

            public ViewPagerAdapter(FragmentManager manager) {
                super(manager);
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            public void addFrag(Fragment fragment, String title, SMS sms, String activity) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("sms", sms);
                bundle.putString("activity", activity);
                fragment.setArguments(bundle);
                mFragmentList.add(fragment);
                mFragmentTitleList.add(title);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentTitleList.get(position);
            }
    }

    public void launchComposeActivity(MenuItem item) {
        //launches the compose activity
        Intent i = new Intent(PostCheckActivity.this, ComposeActivity.class);
        i.putExtra("incomingList", incomingList);
        i.putExtra("outgoingList", outgoingList);
        i.putExtra("users", users);
        PostCheckActivity.this.startActivity(i);
    }

    public void launchMyProfileActivity(MenuItem item) {
        User user = new User(this);
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
        if (!mPhoneNumber.equals("")) {
            user.setNumber("+" + mPhoneNumber); //this is why the + shows up
        }

        user.setName("Me");
        //user.setContactId(id);

        Intent i = new Intent(PostCheckActivity.this, ProfileActivity.class);
        i.putExtra("user", user);
        i.putParcelableArrayListExtra("incomingList", incomingList);
        i.putParcelableArrayListExtra("outgoingList", outgoingList);
        startActivity(i);
    }

    public void launchMainActivity(MenuItem item){
        Intent i = new Intent(PostCheckActivity.this, MainActivity.class);
        PostCheckActivity.this.startActivity(i);
    }

    public void sendText(View view){
        SMS text = new SMS();
        text.setNumber(recipientNumber);
        text.setBody(message);
        text.sendSMS();
    }

    private String getContactId(String recipientNumber) {
        getContactIdMemo = new HashMap<String, String>();
        String memoizedResult = getContactIdMemo.get(recipientNumber);
        if (memoizedResult != null) {
            return memoizedResult;
        }

        ContentResolver contentResolver = this.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(recipientNumber));

        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor c2 = contentResolver.query(
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
        c2.close();
        getContactIdMemo.put(recipientNumber, id);
        return id;
    }
}

