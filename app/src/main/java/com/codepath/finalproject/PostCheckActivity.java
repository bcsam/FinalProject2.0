package com.codepath.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bcsam on 7/11/17.
 */

public class PostCheckActivity extends AppCompatActivity {

    TextView tvTextBody;
    SMS text;
    String message;
    String recipientName;
    String recipientNumber;
    Button btSend;
    Button btEdit;
    ArrayList<SMS> incomingList = new ArrayList<>();
    ArrayList<SMS> outgoingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sets up the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post_check);

        //stores info in intent for sending back to MainActivity
        /*
        message = getIntent().getStringExtra("message");
        recipientName = getIntent().getStringExtra("recipientName");
        recipientNumber = getIntent().getStringExtra("recipientNumber");
        */
        text = getIntent().getParcelableExtra("text");
        incomingList = getIntent().getParcelableArrayListExtra("incomingList");
        outgoingList = getIntent().getParcelableArrayListExtra("outgoingList");

        //makes a Textbody with the user's message

        /*
        sms = new SMS();
        sms.setBody(message);
        */

        //gets the textbody's score and puts them in textBody
        //AnalyzerClient client = new AnalyzerClient();
        //client.getScores(sms);
        //client.getUtteranceScores(textBody);

        //sets the message on the activity
        tvTextBody = (TextView) findViewById(R.id.tvTextBody);
        tvTextBody.setText(text.getBody());
        //tvTextBody.setTextColor(Color.parseColor(text.getTextColor()));

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_unsearchable, menu);
        return true;
    }
/*
    public void sendEmail(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        //for only emails
        i.setType("message/rfc822"); // TODO: 7/14/17 make this sending a text
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        i.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(PostCheckActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
*/

    public void setOnClickListeners(){

        btEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(PostCheckActivity.this, MessagingActivity.class);
                intent.putExtra("message", text.getBody());
                intent.putExtra("name", text.getContact());
                intent.putExtra("number", text.getNumber());
                intent.putParcelableArrayListExtra("incomingList", incomingList);
                intent.putParcelableArrayListExtra("outgoingList", outgoingList);
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
                /*
                    intent.putExtra("name", recipientName);
                    intent.putExtra("number", recipientNumber);
                    intent.putExtra("message", message);
                    */
                    startActivity(intent);
            }
        });
    }

    public void initializeViews(){
        btSend = (Button) findViewById(R.id.btSend);
        btEdit = (Button) findViewById(R.id.btEdit);
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
        PostCheckActivity.this.startActivity(i);
    }

    public void launchMyProfileActivity(MenuItem item) {
        User user = new User(this);
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
        user.setNumber("+"+mPhoneNumber);
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
}

