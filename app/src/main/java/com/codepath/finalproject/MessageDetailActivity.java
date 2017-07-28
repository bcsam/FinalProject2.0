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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bcsam on 7/14/17.
 */

public class MessageDetailActivity extends AppCompatActivity{
    String name;
    String number;
    SMS sms;
    TextView tvMessage;

    ArrayList<SMS> incomingList;
    ArrayList<SMS> outgoingList;

    //needs author's name, message, analysis info
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        sms = getIntent().getParcelableExtra("sms");
        incomingList = getIntent().getParcelableArrayListExtra("incomingList");
        outgoingList = getIntent().getParcelableArrayListExtra("outgoingList");
        if(sms.getBubbleColor().equals("")){
            AnalyzerClient client = new AnalyzerClient();
            client.getScores(sms);
        }


        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvMessage.setText(sms.getBody());
        tvMessage.setTextColor(Color.parseColor(sms.getTextColor()));

        //Code for tabs below

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TonesFragment(), "Tones", sms, "MessageDetailActivity");
        adapter.addFrag(new StylesFragment(), "Styles", sms, "MessageDetailActivity");
        adapter.addFrag(new SocialFragment(), "Social", sms, "MessageDetailActivity");
        //adapter.addFrag(new UtteranceFragment(), "Utterance", textBody, "PostCheckActivity");

        viewPager.setAdapter(adapter);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.pager_header);
        mTabLayout.setupWithViewPager(viewPager);
        animate();
    }

    public void animate(){
        RelativeLayout bottom_layout = (RelativeLayout) findViewById(R.id.bottom_layout);
        bottom_layout.setVisibility(LinearLayout.VISIBLE);
        Animation animationExpand   =    AnimationUtils.loadAnimation(this, R.anim.expand);
        Animation animationFade = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animationFade.setDuration(500);
        tvMessage.setAnimation(animationExpand);
        tvMessage.animate();
        animationExpand.start();
        bottom_layout.setAnimation(animationFade);
        bottom_layout.animate();
        animationFade.start();
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
        i.putExtra("incomingList", incomingList);
        i.putExtra("outgoingList", outgoingList);
        MessageDetailActivity.this.startActivity(i);
    }

    public void launchMyProfileActivity(MenuItem item) {
        //launches the profile view
        Intent i = new Intent(MessageDetailActivity.this, ProfileActivity.class);
        i.putExtra("incomingList", incomingList);
        i.putExtra("outgoingList", outgoingList);
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
}
