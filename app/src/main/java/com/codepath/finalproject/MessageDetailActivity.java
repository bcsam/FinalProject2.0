package com.codepath.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    ArrayList<User> users;

    //needs author's name, message, analysis info
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        Fade fade = new Fade(Fade.IN);
        fade.setDuration(1200);
        //getWindow().setSharedElementsUseOverlay(false);
        //getWindow().setEnterTransition(fade);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvMessage.setVisibility(View.INVISIBLE);
        sms = getIntent().getParcelableExtra("sms");
        incomingList = getIntent().getParcelableArrayListExtra("incomingList");
        outgoingList = getIntent().getParcelableArrayListExtra("outgoingList");
        users = getIntent().getParcelableArrayListExtra("users");
        if(sms.getBubbleColor().equals("")){
            AnalyzerClient client = new AnalyzerClient();
            client.getScores(sms);
        }




        //Code for tabs below

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        //viewPager.setVisibility(View.INVISIBLE);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TonesFragment(), "Tones", sms, "MessageDetailActivity");
        adapter.addFrag(new StylesFragment(), "Styles", sms, "MessageDetailActivity");
        adapter.addFrag(new SocialFragment(), "Social", sms, "MessageDetailActivity");
        //adapter.addFrag(new UtteranceFragment(), "Utterance", textBody, "PostCheckActivity");

        viewPager.setAdapter(adapter);

        final TabLayout mTabLayout = (TabLayout) findViewById(R.id.pager_header);
        //mTabLayout.setVisibility(View.INVISIBLE);

        mTabLayout.setupWithViewPager(viewPager);

        /*
        Animation animationFade = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animationFade.setDuration(2000);
        RelativeLayout bottom_layout = (RelativeLayout) findViewById(R.id.bottom_layout);
        bottom_layout.setAnimation(animationFade);
        mTabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        animationFade.start();*/
        //animate();

        /*
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                Set set = sharedElements.keySet();
                Collection collection = sharedElements.values();
                View keySharedElementView = sharedElements.get("messageDetailTransition");
                if(keySharedElementView != null){
                    ViewCompat.animate(keySharedElementView).setListener(new ViewPropertyAnimatorListenerAdapter(){
                        @Override
                        public void onAnimationEnd(View view) {
                            viewPager.setVisibility(View.VISIBLE);
                           mTabLayout.setVisibility(View.VISIBLE);
                            super.onAnimationEnd(view);
                        }
                    });
                }
            }
        });*/
        tvMessage.setText(sms.getBody());
        tvMessage.setTextColor(Color.parseColor(sms.getTextColor()));
        tvMessage.setVisibility(View.VISIBLE);
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
        i.putExtra("users", users);
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
