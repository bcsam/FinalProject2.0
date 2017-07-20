package com.codepath.finalproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vf608 on 7/13/17.
 */

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sets up the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        User user = getIntent().getParcelableExtra("user");
        if(user.getName().equals("Me"))
            getMyAverages(user);
        else
            getAverages(user);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvNumber = (TextView) findViewById(R.id.tvNumber);

        tvName.setText(user.getName());
        tvNumber.setText(user.toStringNumber());
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TonesFragment(), "Tones", user, "ProfileActivity");
        adapter.addFrag(new StylesFragment(), "Styles", user, "ProfileActivity");
        adapter.addFrag(new SocialFragment(), "Social", user, "ProfileActivity");
        //adapter.addFrag(new UtteranceFragment(), "Utterance", user, "ProfileActivity");

        viewPager.setAdapter(adapter);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.pager_header);
        mTabLayout.setupWithViewPager(viewPager);

    }
    public void getMyAverages(User user) {
        Uri uri = Uri.parse("content://sms/sent");
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        startManagingCursor(c);
        AnalyzerClient client = new AnalyzerClient();
        String fullText = "";
        // Read the sms data and store it in the listco
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                fullText += ". "+text;
                c.moveToNext();
            }
            SMS sms = new SMS();
            sms.setBody(fullText);
            client.getScores(sms);
            user.updateScores(sms);
        }
        c.close();
    }

    public void getAverages(User user) {
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = getContentResolver().query(uri, null, "address='"+user.getNumber()+"'", null, null);
        startManagingCursor(c);
        AnalyzerClient client = new AnalyzerClient();
        String fullText = "";
        // Read the sms data and store it in the listco
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                fullText += ". "+text;
                c.moveToNext();
            }
            SMS sms = new SMS();
            sms.setBody(fullText);
            client.getScores(sms);
            user.updateScores(sms);
        }
        c.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_unsearchable, menu);
        return true;
    }

    public void launchComposeActivity(MenuItem item) {
        //launches the profile view
        Intent i = new Intent(ProfileActivity.this, ComposeActivity.class);
        ProfileActivity.this.startActivity(i);
    }

    public void launchMyProfileActivity(MenuItem item) {
        //launches the profile view
        User user = new User();
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
        user.setNumber("+"+mPhoneNumber);
        user.setName("Me");
        Log.i("profile", user.getNumber());
        Log.i("profile", user.toStringNumber());
        Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);

        i.putExtra("user", user);
        ProfileActivity.this.startActivity(i);
    }

    public void launchMainActivity(MenuItem item){
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        ProfileActivity.this.startActivity(i);
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

        public void addFrag(Fragment fragment, String title, User user, String activity) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(i);
    }
}