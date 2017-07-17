package com.codepath.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bcsam on 7/11/17.
 */

public class PostCheckActivity extends AppCompatActivity {

    TextView tvTextBody;
    TextBody textBody;
    String message;
    String recipient;
    Button btSend;
    Button btEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sets up the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post_check);

        //stores info in intent for sending back to MainActivity
        message = getIntent().getStringExtra("message");
        recipient = getIntent().getStringExtra("recipientName");

        //makes a Textbody with the user's message
        textBody = new TextBody();
        textBody.setMessage(message);

        //gets the textbody's score and puts them in textBody
        AnalyzerClient client = new AnalyzerClient();
        client.getToneScores(textBody);
        client.getStyleScores(textBody);
        client.getSocialScores(textBody);
        //client.getUtteranceScores(textBody);

        //sets the message on the activity
        tvTextBody = (TextView) findViewById(R.id.tvTextBody);
        tvTextBody.setText(message);
        tvTextBody.setTextColor(Color.parseColor(textBody.getTextColor()));

        //Code for tabs below

        // Locate the viewpager in activity_composeose.xml
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TonesFragment(), "Tones", textBody, "PostCheckActivity");
        adapter.addFrag(new StylesFragment(), "Styles", textBody, "PostCheckActivity");
        adapter.addFrag(new SocialFragment(), "Social", textBody, "PostCheckActivity");
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    public void setOnClickListeners(){

        btEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(PostCheckActivity.this, ComposeActivity.class);
                intent.putExtra("message", message);
                intent.putExtra("recipient", recipient);
                PostCheckActivity.this.startActivity(intent);
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    sendText(view);
                    Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PostCheckActivity.this, MainActivity.class);
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

            public void addFrag(Fragment fragment, String title, TextBody textBody, String activity) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("textBody", textBody);
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
        //launches the profile view
        Intent i = new Intent(PostCheckActivity.this, ComposeActivity.class);
        PostCheckActivity.this.startActivity(i);
    }

    public void launchMyProfileActivity(MenuItem item) {
        //launches the profile view
        Intent i = new Intent(PostCheckActivity.this, ProfileActivity.class);
        PostCheckActivity.this.startActivity(i);
    }

    public void sendText(View view){
        SMS text = new SMS();
        text.setNumber(recipient);
        text.setBody(message);
        text.sendSMS();
    }
}

