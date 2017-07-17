package com.codepath.finalproject;

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

    public void getAverages(User user){
        Uri uri = Uri.parse("content://sms/sent");
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        startManagingCursor(c);
        AnalyzerClient client = new AnalyzerClient();
        // Read the sms data and store it in the listco
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                TextBody body = new TextBody();
                body.setMessage(text);
                client.getToneScores(body);
                client.getStyleScores(body);
                client.getSocialScores(body);
                user.updateScores(body);
                c.moveToNext();
            }
        }
        c.close();
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
}
