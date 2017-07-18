package com.codepath.finalproject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by bcsam on 7/12/17.
 */

public class TonesFragment extends Fragment {

    TextView tvAngerScore;
    TextView tvDisgustScore;
    TextView tvFearScore;
    TextView tvJoyScore;
    TextView tvSadnessScore;
    ProgressBar pbAnger;
    ProgressBar pbDisgust;
    ProgressBar pbFear;
    ProgressBar pbJoy;
    ProgressBar pbSadness;
    TextBody textBody;
    User user;

    public TonesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tones, container, false);
        tvAngerScore = (TextView) v.findViewById(R.id.tvAngerScore);
        tvDisgustScore = (TextView) v.findViewById(R.id.tvDisgustScore);
        tvFearScore = (TextView) v.findViewById(R.id.tvFearScore);
        tvJoyScore = (TextView) v.findViewById(R.id.tvJoyScore);
        tvSadnessScore = (TextView) v.findViewById(R.id.tvSadnessScore);
        pbAnger = (ProgressBar) v.findViewById(R.id.pbAnger);
        pbDisgust = (ProgressBar) v.findViewById(R.id.pbDisgust);
        pbFear = (ProgressBar) v.findViewById(R.id.pbFear);
        pbJoy = (ProgressBar) v.findViewById(R.id.pbJoy);
        pbSadness = (ProgressBar) v.findViewById(R.id.pbSadness);
        String activity = getArguments().getString("activity");
        if(activity.equals("PostCheckActivity") || activity.equals("MessageDetailActivity")) {
            textBody = getArguments().getParcelable("textBody");
            setTexts();
            setProgressBars();
        }
        else if(activity.equals("ProfileActivity")){
            user = getArguments().getParcelable("user");
            setProfileTexts();
            setProfileProgressBars();
        }
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    public void setTexts() {
        tvAngerScore.setText(String.valueOf(textBody.getToneLevel(0)));
        tvDisgustScore.setText(String.valueOf(textBody.getToneLevel(1)));
        tvFearScore.setText(String.valueOf(textBody.getToneLevel(2)));
        tvJoyScore.setText(String.valueOf(textBody.getToneLevel(3)));
        tvSadnessScore.setText(String.valueOf(textBody.getToneLevel(4)));
    }

    public void setProgressBars(){
        pbAnger.setMax(100);
        pbAnger.setProgress(textBody.getToneLevel(0));
        pbAnger.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getToneColor(0)), PorterDuff.Mode.SRC_IN);
        pbDisgust.setMax(100);
        pbDisgust.setProgress(textBody.getToneLevel(1));
        pbDisgust.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getToneColor(1)), PorterDuff.Mode.SRC_IN);
        pbFear.setMax(100);
        pbFear.setProgress(textBody.getToneLevel(2));
        pbFear.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getToneColor(2)), PorterDuff.Mode.SRC_IN);
        pbJoy.setMax(100);
        pbJoy.setProgress(textBody.getToneLevel(3));
        pbJoy.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getToneColor(3)), PorterDuff.Mode.SRC_IN);
        pbSadness.setMax(100);
        pbSadness.setProgress(textBody.getToneLevel(4));
        pbSadness.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getToneColor(4)), PorterDuff.Mode.SRC_IN);
    }

    public void setProfileTexts() {
        tvAngerScore.setText(String.valueOf(user.getAverageToneLevels(0)));
        tvDisgustScore.setText(String.valueOf(user.getAverageToneLevels(1)));
        tvFearScore.setText(String.valueOf(user.getAverageToneLevels(2)));
        tvJoyScore.setText(String.valueOf(user.getAverageToneLevels(3)));
        tvSadnessScore.setText(String.valueOf(user.getAverageToneLevels(4)));
    }

    public void setProfileProgressBars(){
        pbAnger.setMax(100);
        pbAnger.setProgress(user.getAverageToneLevels(0));
        pbAnger.getProgressDrawable().setColorFilter(Color.parseColor(user.getToneColor(0)), PorterDuff.Mode.SRC_IN);
        pbDisgust.setMax(100);
        pbDisgust.setProgress(user.getAverageToneLevels(1));
        pbDisgust.getProgressDrawable().setColorFilter(Color.parseColor(user.getToneColor(1)), PorterDuff.Mode.SRC_IN);
        pbFear.setMax(100);
        pbFear.setProgress(user.getAverageToneLevels(2));
        pbFear.getProgressDrawable().setColorFilter(Color.parseColor(user.getToneColor(2)), PorterDuff.Mode.SRC_IN);
        pbJoy.setMax(100);
        pbJoy.setProgress(user.getAverageToneLevels(3));
        pbJoy.getProgressDrawable().setColorFilter(Color.parseColor(user.getToneColor(3)), PorterDuff.Mode.SRC_IN);
        pbSadness.setMax(100);
        pbSadness.setProgress(user.getAverageToneLevels(4));
        pbSadness.getProgressDrawable().setColorFilter(Color.parseColor(user.getToneColor(4)), PorterDuff.Mode.SRC_IN);
    }

   public void getAverages(){
       TelephonyManager tMgr = (TelephonyManager)this.getContext().getSystemService(Context.TELEPHONY_SERVICE);
       String mPhoneNumber = tMgr.getLine1Number();
       if(user.getNumber().equals(mPhoneNumber)){

       }
   }

}
