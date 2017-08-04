package com.codepath.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
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
import android.widget.Toast;

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

public class ProfileActivity extends AppCompatActivity { // TODO: 8/1/17 be able to message the profile owner, next to phone number?

    ArrayList<SMS> incomingList;
    ArrayList<SMS> outgoingList;
    ArrayList<User> users;
    int position;
    String from;
    Cursor c;
    Cursor c1;
    Cursor c2;
    ProfileAnalyzerClient client;
    User user;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sets up the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        postponeEnterTransition();

        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        user = getIntent().getParcelableExtra("user");
        position = getIntent().getIntExtra("position", -1);
        users = getIntent().getParcelableArrayListExtra("users");
        if(position == -1) {
            Log.i("Profile", "-1");
            user = getIntent().getParcelableExtra("user");
        }
        else{
            Log.i("Profile", String.valueOf(position));
            user = users.get(position);
        }

        if (user.getName() != null && user.getName().equals("")) { // TODO: 7/31/17 check on null pointer here
            getSupportActionBar().setTitle(user.getName());
        } else {
            getSupportActionBar().setTitle("ToneTeller");
        }


        String id = getIntent().getStringExtra("id");
        incomingList = getIntent().getParcelableArrayListExtra("incomingList");
        outgoingList = getIntent().getParcelableArrayListExtra("outgoingList");
        from = getIntent().getStringExtra("from");
        ViewPager viewPagerTop = (ViewPager) findViewById(R.id.upper_pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapterTop = new ViewPagerAdapter(getSupportFragmentManager());
        adapterTop.addFrag(new ProfileFragment(), "Profile", user, "ProfileActivity");
        adapterTop.addFrag(new GraphFragment(), "Graph", user, "ProfileActivity");

        viewPagerTop.setAdapter(adapterTop);

        TabLayout mTabLayoutTop = (TabLayout) findViewById(R.id.upper_pager_header);
        mTabLayoutTop.setupWithViewPager(viewPagerTop);
        client = new ProfileAnalyzerClient(this, user);
        if(user.getName().equals("Me")){
            SMS myTexts = getMyAverages(user);
            client.execute(myTexts);
        }
        else {
            SMS allTexts = getAverages(user);
            if(user.getAllTexts().equals(allTexts.getBody()))
            {
                Log.i("Profile", user.getAllTexts());
                pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
                pbLoading.setVisibility(View.GONE);
                ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

                // Set the ViewPagerAdapter into ViewPager
                ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
                adapter.addFrag(new TonesFragment(), "Tones", user, "ProfileActivity");
                adapter.addFrag(new StylesFragment(), "Styles", user, "ProfileActivity");
                adapter.addFrag(new SocialFragment(), "Social", user, "ProfileActivity");

                viewPager.setAdapter(adapter);

                TabLayout mTabLayout = (TabLayout) findViewById(R.id.pager_header);
                mTabLayout.setupWithViewPager(viewPager);
            }
            else {
                user.setAllTexts(allTexts.getBody());
                client.execute(allTexts);
            }
        }


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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem miProfile = menu.findItem(R.id.miProfile);

        if(user.getName().equals("Me")) {
            miProfile.setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
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
        i.putExtra("incomingList", incomingList);
        i.putExtra("users", users);
        i.putExtra("outgoingList", outgoingList);
        ProfileActivity.this.startActivity(i);
    }

    public void launchMyProfileActivity(MenuItem item) {
        //launches the profile view
        User user = new User(this);
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
        Toast.makeText(this, mPhoneNumber, Toast.LENGTH_LONG).show();
        if (!mPhoneNumber.equals("")) {
            user.setNumber("+" + mPhoneNumber); //this is why the + shows up
        }

        user.setName("Me");

        String id = null;
        /*ContentResolver contentResolver = this.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mPhoneNumber));

        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        c =
                contentResolver.query( // TODO: 7/25/17 This line crashes the app 
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if(c != null) {
            while(c.moveToNext()){
                id = c.getString(c.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
        }
        user.setContactId(id);*/

        Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);

        i.putExtra("user", user);
        i.putExtra("incomingList", incomingList);
        i.putExtra("outgoingList", outgoingList);
        ProfileActivity.this.startActivity(i);
    }

    public void launchMainActivity(MenuItem item){
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        i.putExtra("incomingList", incomingList);
        i.putExtra("outgoingList", outgoingList);
        ProfileActivity.this.startActivity(i);
    }

    public SMS getMyAverages(User user) {
        Uri uri = Uri.parse("content://sms/sent");
        c1 = getContentResolver().query(uri, null, null, null, null);
        startManagingCursor(c1);
        String fullText = "";
        // Read the sms data and store it in the listco
        if (c1.moveToFirst()) {
            for (int i = 0; i < c1.getCount(); i++) {
                String text = c1.getString(c1.getColumnIndexOrThrow("body")).toString();
                fullText += ". "+text;
                c1.moveToNext();
            }
        }
        SMS sms = new SMS();
        sms.setBody(fullText);
        return sms;
    }

    public SMS getAverages(User user) {
        Uri uri = Uri.parse("content://sms/inbox");
        c2 = getContentResolver().query(uri, null, "address='"+user.getNumber()+"'", null, null);
        startManagingCursor(c2);
        String fullText = "";

        // Read the sms data and store it in the listco
        if (c2.moveToFirst()) {
            for (int i = 0; i < c2.getCount(); i++) {
                String text = c2.getString(c2.getColumnIndexOrThrow("body")).toString();
                fullText += ". "+text;
                c2.moveToNext();
            }
        }
        SMS sms = new SMS();
        sms.setBody(fullText);
        return sms;
    }

    @Override
    public void onDestroy(){
        if(c != null)
            c.close();
        if(c1 != null)
            c1.close();
        if(c2 != null)
            c2.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){ // TODO: 8/3/17 why does it go only to messaging and main
        Intent i;
        if(from != null && from.equals("messaging"))
            i =  new Intent(ProfileActivity.this, MessagingActivity.class);
        else {
            i = new Intent(ProfileActivity.this, MainActivity.class);
        }
        if(users != null && user != null && !user.getName().equals("Me"))
            users.set(position, user);

        i.putParcelableArrayListExtra("incomingList", incomingList);
        i.putParcelableArrayListExtra("outgoingList", outgoingList);
        i.putParcelableArrayListExtra("users", users);
        i.putExtra("position", position);
        setResult(RESULT_OK, i);
        finish();
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
        public static final String USERNAME = "a90138db-2017-4c69-ab73-a263d204208b";
        public static final String PASSWORD = "rTuDoZzHfyMA";
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
            if(params[0].getBody().equals("")){
                for(int j = 0; j < 5; j++) {
                    if(j<3)
                        params[0].setStyleLevel(j, 0);
                    params[0].setToneLevel(j, 0);
                    params[0].setSocialLevel(j, 0);
                }
            }
            else
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