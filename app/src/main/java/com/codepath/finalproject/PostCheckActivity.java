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
    String subject;
    String recipient;
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
        recipient = getIntent().getStringExtra("to");
        subject = getIntent().getStringExtra("subject");

        textBody = new TextBody();
        textBody.setMessage(text);

        AnalyzerClient client = new AnalyzerClient();
        client.getToneScores(textBody);
        client.getStyleScores(textBody);
        client.getSocialScores(textBody);


        tvTextBody = (TextView) findViewById(R.id.tvTextBody);
        tvTextBody.setText(text);
        tvTextBody.setTextColor(Color.parseColor(textBody.getTextColor()));

        //new code for tabs below

        // Locate the viewpager in activity_main.xml
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TonesFragment(), "Tones", textBody);
        adapter.addFrag(new StylesFragment(), "Styles", textBody);
        adapter.addFrag(new SocialFragment(), "Social", textBody);

        viewPager.setAdapter(adapter);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.pager_header);
        mTabLayout.setupWithViewPager(viewPager);

        initializeViews();
        setOnClickListeners();
    }

    public void sendEmail(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
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
                //String message = tvTextBody.getText().toString();
                Intent i = new Intent(PostCheckActivity.this, MainActivity.class);
                PostCheckActivity.this.startActivity(i);
        }});

        btEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(PostCheckActivity.this, MainActivity.class);
                intent.putExtra("message", text);
                intent.putExtra("subject", subject);
                intent.putExtra("recipient", recipient);
                PostCheckActivity.this.startActivity(intent);
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

            public void addFrag(Fragment fragment, String title, TextBody textBody) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("textBody", textBody);
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

