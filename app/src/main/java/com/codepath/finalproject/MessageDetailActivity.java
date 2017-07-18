package com.codepath.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bcsam on 7/14/17.
 */

public class MessageDetailActivity extends AppCompatActivity{
    String name;
    String message;
    String number;
    TextBody textBody;
    TextView tvName;
    TextView tvMessage;


    //needs author's name, message, analysis info
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        //stores info from the incoming intent
        message = getIntent().getStringExtra("message");
        name = getIntent().getStringExtra("name"); // TODO: 7/14/17 check that this is the right string
        number = getIntent().getStringExtra("number");

        //makes a Textbody with the user's message
        textBody = new TextBody();
        textBody.setMessage(message);

        //gets the textbody's score and puts them in textBody
        AnalyzerClient client = new AnalyzerClient();
        client.getToneScores(textBody);
        client.getStyleScores(textBody);
        client.getSocialScores(textBody);

        //sets the message on the activity
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        tvMessage.setTextColor(Color.parseColor(textBody.getTextColor()));

        //Code for tabs below

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TonesFragment(), "Tones", textBody, "MessageDetailActivity");
        adapter.addFrag(new StylesFragment(), "Styles", textBody, "MessageDetailActivity");
        adapter.addFrag(new SocialFragment(), "Social", textBody, "MessageDetailActivity");
        //adapter.addFrag(new UtteranceFragment(), "Utterance", textBody, "PostCheckActivity");

        viewPager.setAdapter(adapter);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.pager_header);
        mTabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_unsearchable, menu);
        return true;
    }

    public void launchComposeActivity(MenuItem item) {
        //launches the compose activit
        Intent i = new Intent(MessageDetailActivity.this, ComposeActivity.class);
        MessageDetailActivity.this.startActivity(i);
    }

    public void launchMyProfileActivity(MenuItem item) {
        //launches the profile view
        Intent i = new Intent(MessageDetailActivity.this, ProfileActivity.class);
        MessageDetailActivity.this.startActivity(i);
    }

    public void launchMainActivity(MenuItem item){
        Intent i = new Intent(MessageDetailActivity.this, MainActivity.class);
        MessageDetailActivity.this.startActivity(i);
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
}
