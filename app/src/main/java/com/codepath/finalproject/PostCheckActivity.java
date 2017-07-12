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
    String text;
    Button btSend;
    Button btEdit;
    TextView tvAngerScore;
    TextView tvDisgustScore;
    TextView tvFearScore;
    TextView tvJoyScore;
    TextView tvSadnessScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post_check);
        text = getIntent().getStringExtra("message");

        textBody = new TextBody();
        textBody.setMessage(text);

        AnalyzerClient client = new AnalyzerClient();
        client.getToneScores(textBody);

        TextView tvTextBody = (TextView) findViewById(R.id.tvTextBody);
        tvTextBody.setText(text); //check on why this doesn't work // TODO: 7/12/17
        tvTextBody.setTextColor(Color.parseColor(textBody.getTextColor()));

        //new code for tabs below

        // Locate the viewpager in activity_main.xml
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TonesFragment(), "Tones");
        adapter.addFrag(new StylesFragment(), "Styles");
        adapter.addFrag(new SocialFragment(), "Social");//need to replace with fragments

        viewPager.setAdapter(adapter);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.pager_header);
        mTabLayout.setupWithViewPager(viewPager);

        initializeViews();
        setOnClickListeners();
        setScoreTexts();
    }

    public void sendEmail(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"andreadeoliveira123@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT, text);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(PostCheckActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setOnClickListeners(){
        btSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String message = tvTextBody.getText().toString();
                Intent i = new Intent(PostCheckActivity.this, MainActivity.class);
                PostCheckActivity.this.startActivity(i);
        }});

        btEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String message = tvTextBody.getText().toString();
                Intent intent = new Intent(PostCheckActivity.this, MainActivity.class);
                intent.putExtra("message", message);
                PostCheckActivity.this.startActivity(intent);
            }
        });
    }

    public void initializeViews(){
        tvAngerScore = (TextView) findViewById(R.id.tvAngerScore);
        tvDisgustScore = (TextView) findViewById(R.id.tvDisgustScore);
        tvFearScore = (TextView) findViewById(R.id.tvFearScore);
        tvJoyScore = (TextView) findViewById(R.id.tvJoyScore);
        tvSadnessScore = (TextView) findViewById(R.id.tvSadnessScore);
        btSend = (Button) findViewById(R.id.btSend);
        btEdit = (Button) findViewById(R.id.btEdit);
    }

    public void setScoreTexts(){
        tvAngerScore.setText(String.valueOf(textBody.getToneLevel(0)));
        tvDisgustScore.setText(String.valueOf(textBody.getToneLevel(1)));
        tvFearScore.setText(String.valueOf(textBody.getToneLevel(2)));
        tvJoyScore.setText(String.valueOf(textBody.getToneLevel(3)));
        tvSadnessScore.setText(String.valueOf(textBody.getToneLevel(4)));
    }

    public void setProgressBars(){
        tvAngerScore.setText(String.valueOf(textBody.getToneLevel(0)));
        tvDisgustScore.setText(String.valueOf(textBody.getToneLevel(1)));
        tvFearScore.setText(String.valueOf(textBody.getToneLevel(2)));
        tvJoyScore.setText(String.valueOf(textBody.getToneLevel(3)));
        tvSadnessScore.setText(String.valueOf(textBody.getToneLevel(4)));
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

            public void addFrag(Fragment fragment, String title) {
                mFragmentList.add(fragment);
                mFragmentTitleList.add(title);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentTitleList.get(position);
            }
    }
}

