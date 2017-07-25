package com.codepath.finalproject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
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
import android.view.View;
import android.widget.ProgressBar;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Tone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;

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
        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        User user = getIntent().getParcelableExtra("user");
        String id = getIntent().getStringExtra("id");
        ViewPager viewPagerTop = (ViewPager) findViewById(R.id.upper_pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapterTop = new ViewPagerAdapter(getSupportFragmentManager());
        adapterTop.addFrag(new ProfileFragment(), "Profile", user, "ProfileActivity");
        adapterTop.addFrag(new GraphFragment(), "Graph", user, "ProfileActivity");

        viewPagerTop.setAdapter(adapterTop);

        TabLayout mTabLayoutTop = (TabLayout) findViewById(R.id.upper_pager_header);
        mTabLayoutTop.setupWithViewPager(viewPagerTop);

        ProfileAnalyzerClient client = new ProfileAnalyzerClient(this, user);
        if(user.getName().equals("Me"))
            client.execute(getMyAverages(user));
        else
            client.execute(getAverages(user));


        /*long contactIdLong = Long.parseLong(id);

        Bitmap image = BitmapFactory.decodeStream(openPhoto(contactIdLong));

        if (image != null) {
            ivProfileImage.setImageBitmap(null);
            ivProfileImage.setImageBitmap(Bitmap.createScaledBitmap(image, 45, 45, false));
        } else {
            ivProfileImage.setImageResource(R.drawable.ic_person_white);
        }*/

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
        User user = new User(this);
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
        user.setNumber("+"+mPhoneNumber);
        user.setName("Me");

        String id = null;
        ContentResolver contentResolver = this.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mPhoneNumber));

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
                id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }
        user.setContactId(id);

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

    public SMS getMyAverages(User user) {
        Uri uri = Uri.parse("content://sms/sent");
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        startManagingCursor(c);
        String fullText = "";
        // Read the sms data and store it in the listco
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                fullText += ". "+text;
                c.moveToNext();
            }
            //client.getScores(sms);
            //user.updateScores(sms);
        }
        c.close();
        SMS sms = new SMS();
        sms.setBody(fullText);
        return sms;
    }

    public SMS getAverages(User user) {
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = getContentResolver().query(uri, null, "address='"+user.getNumber()+"'", null, null);
        startManagingCursor(c);
        String fullText = "";

        // Read the sms data and store it in the listco
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                fullText += ". "+text;
                c.moveToNext();
            }
            //client.getScores(sms);
            //user.updateScores(sms);
        }
        c.close();
        SMS sms = new SMS();
        sms.setBody(fullText);
        return sms;
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

    public class ProfileAnalyzerClient extends AsyncTask<SMS, String, SMS> {
        //public static final String VERSION = "ToneAnalyzer.VERSION_DATE_2016_05_19";
        public static final String URL = "https://gateway.watsonplatform.net/tone-analyzer/api";
        public static final String USERNAME = "16d48b36-2e71-452c-bd1f-b5419a3ae48a";
        public static final String PASSWORD = "dXaJBi3c2Joj";
        ToneAnalyzer service;
        Context context;
        User user;

        public ProfileAnalyzerClient(Context context, User user){
            this.context = context;
            service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
            service.setEndPoint(URL);
            service.setUsernameAndPassword(USERNAME, PASSWORD);
            this.user = user;
        }

        @Override
        protected SMS doInBackground(SMS... params) {
            Log.i("client", "in background");
            getScores(params[0]);
            user.updateScores(params[0]);
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
                    pbLoading.setVisibility(View.GONE);
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
            });
            return params[0];
        }
        public void getScores(SMS sms) {
            ToneOptions options = new ToneOptions.Builder()
                    .addTone(Tone.EMOTION)
                    .addTone(Tone.LANGUAGE)
                    .addTone(Tone.SOCIAL).build();
            ToneAnalysis tone =
                    service.getTone(sms.getBody(), options).execute();

            for (ToneCategory tc : tone.getDocumentTone().getTones()) {
                for (ToneScore ts : tc.getTones()) {
                    switch (ts.getName()) {
                        case ("Anger"):
                            sms.setToneLevel(0, ts.getScore());
                            break;
                        case ("Disgust"):
                            sms.setToneLevel(1, ts.getScore());
                            break;
                        case ("Fear"):
                            sms.setToneLevel(2, ts.getScore());
                            break;
                        case ("Joy"):
                            sms.setToneLevel(3, ts.getScore());
                            break;
                        case ("Sadness"):
                            sms.setToneLevel(4, ts.getScore());
                            break;
                        case ("Analytical"):
                            sms.setStyleLevel(0, ts.getScore());
                            break;
                        case ("Confident"):
                            sms.setStyleLevel(1, ts.getScore());
                            break;
                        case ("Tentative"):
                            sms.setStyleLevel(2, ts.getScore());
                            break;
                        case ("Openness"):
                            sms.setSocialLevel(0, ts.getScore());
                            break;
                        case ("Conscientiousness"):
                            sms.setSocialLevel(1, ts.getScore());
                            break;
                        case ("Extraversion"):
                            sms.setSocialLevel(2, ts.getScore());
                            break;
                        case ("Agreeableness"):
                            sms.setSocialLevel(3, ts.getScore());
                            break;
                        case ("Emotional Range"):
                            sms.setSocialLevel(4, ts.getScore());
                            break;
                    }
                }
            }
        }
    }
}