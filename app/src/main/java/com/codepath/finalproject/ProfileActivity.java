package com.codepath.finalproject;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.codepath.finalproject.R.id.tvName;
import static com.codepath.finalproject.R.id.tvNumber;

/**
 * Created by vf608 on 7/13/17.
 */

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sets up the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvNumber = (TextView) findViewById(R.id.tvNumber);
        String number = getIntent().getStringExtra("number");

        if(!number.equals("")){

            tvNumber.setText(number);
            tvName.setText(getIntent().getStringExtra("name"));
);
            // Set the ViewPagerAdapter into ViewPager
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFrag(new TonesFragment(), "Tones", user, "ProfileActivity");
            adapter.addFrag(new StylesFragment(), "Styles", user, "ProfileActivity");
            adapter.addFrag(new SocialFragment(), "Social", user, "ProfileActivity");

        }else {
            User user = getIntent().getParcelableExtra("user");
            tvName.setText(user.getName());
            tvNumber.setText(user.toStringNumber());

            // Set the ViewPagerAdapter into ViewPager
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFrag(new TonesFragment(), "Tones", user, "ProfileActivity");
            adapter.addFrag(new StylesFragment(), "Styles", user, "ProfileActivity");
            adapter.addFrag(new SocialFragment(), "Social", user, "ProfileActivity");
            //adapter.addFrag(new UtteranceFragment(), "Utterance", user, "ProfileActivity");
        tvName.setText(user.getName());
        tvNumber.setText(user.toStringNumber());
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TonesFragment(), "Tones", user, "ProfileActivity");
        adapter.addFrag(new StylesFragment(), "Styles", user, "ProfileActivity");
        adapter.addFrag(new SocialFragment(), "Social", user, "ProfileActivity");
        //adapter.addFrag(new UtteranceFragment(), "Utterance", user, "ProfileActivity");


        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.pager_header);
        mTabLayout.setupWithViewPager(viewPager);

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
