package com.codepath.finalproject;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by bcsam on 7/12/17.
 */

public class SocialFragment extends Fragment{

    TextView tvOpennessScore;
    TextView tvConscientiousnessScore;
    TextView tvExtraversionScore;
    TextView tvAgreeablenessScore;
    TextView tvEmotionalRangeScore;
    ProgressBar pbOpenness;
    ProgressBar pbConscientiousness;
    ProgressBar pbExtraversion;
    ProgressBar pbAgreeableness;
    ProgressBar pbEmotionalRange;
    TextBody textBody;
    User user;

    public SocialFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_social, container, false);
        tvOpennessScore = (TextView) v.findViewById(R.id.tvOpennessScore);
        tvConscientiousnessScore = (TextView) v.findViewById(R.id.tvConscientiousnessScore);
        tvExtraversionScore = (TextView) v.findViewById(R.id.tvExtraversionScore);
        tvAgreeablenessScore = (TextView) v.findViewById(R.id.tvAgreeablenessScore);
        tvEmotionalRangeScore = (TextView) v.findViewById(R.id.tvEmotionalRangeScore);
        pbOpenness = (ProgressBar) v.findViewById(R.id.pbOpenness);
        pbConscientiousness = (ProgressBar) v.findViewById(R.id.pbConscientiousness);
        pbExtraversion = (ProgressBar) v.findViewById(R.id.pbExtraversion);
        pbAgreeableness = (ProgressBar) v.findViewById(R.id.pbAgreeableness);
        pbEmotionalRange = (ProgressBar) v.findViewById(R.id.pbEmotionalRange);
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
        tvOpennessScore.setText(String.valueOf(textBody.getSocialLevel(0)));
        tvConscientiousnessScore.setText(String.valueOf(textBody.getSocialLevel(1)));
        tvExtraversionScore.setText(String.valueOf(textBody.getSocialLevel(2)));
        tvAgreeablenessScore.setText(String.valueOf(textBody.getSocialLevel(3)));
        tvEmotionalRangeScore.setText(String.valueOf(textBody.getSocialLevel(4)));
    }

    public void setProgressBars(){
        pbOpenness.setMax(100);
        pbOpenness.setProgress(textBody.getSocialLevel(0));
        pbOpenness.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getSocialColor()), PorterDuff.Mode.SRC_IN);
        pbConscientiousness.setMax(100);
        pbConscientiousness.setProgress(textBody.getSocialLevel(1));
        pbConscientiousness.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getSocialColor()), PorterDuff.Mode.SRC_IN);
        pbExtraversion.setMax(100);
        pbExtraversion.setProgress(textBody.getSocialLevel(2));
        pbExtraversion.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getSocialColor()), PorterDuff.Mode.SRC_IN);
        pbAgreeableness.setMax(100);
        pbAgreeableness.setProgress(textBody.getSocialLevel(3));
        pbAgreeableness.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getSocialColor()), PorterDuff.Mode.SRC_IN);
        pbEmotionalRange.setMax(100);
        pbEmotionalRange.setProgress(textBody.getSocialLevel(4));
        pbEmotionalRange.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getSocialColor()), PorterDuff.Mode.SRC_IN);
    }

    public void setProfileTexts() {
        tvOpennessScore.setText(String.valueOf(user.getAverageSocialLevels(0)));
        tvConscientiousnessScore.setText(String.valueOf(user.getAverageSocialLevels(1)));
        tvExtraversionScore.setText(String.valueOf(user.getAverageSocialLevels(2)));
        tvAgreeablenessScore.setText(String.valueOf(user.getAverageSocialLevels(3)));
        tvEmotionalRangeScore.setText(String.valueOf(user.getAverageSocialLevels(4)));
    }

    public void setProfileProgressBars(){
        pbOpenness.setMax(100);
        pbOpenness.setProgress(user.getAverageSocialLevels(0));
        pbOpenness.getProgressDrawable().setColorFilter(Color.parseColor(user.getSocialColor()), PorterDuff.Mode.SRC_IN);
        pbConscientiousness.setMax(100);
        pbConscientiousness.setProgress(user.getAverageSocialLevels(1));
        pbConscientiousness.getProgressDrawable().setColorFilter(Color.parseColor(user.getSocialColor()), PorterDuff.Mode.SRC_IN);
        pbExtraversion.setMax(100);
        pbExtraversion.setProgress(user.getAverageSocialLevels(2));
        pbExtraversion.getProgressDrawable().setColorFilter(Color.parseColor(user.getSocialColor()), PorterDuff.Mode.SRC_IN);
        pbAgreeableness.setMax(100);
        pbAgreeableness.setProgress(user.getAverageSocialLevels(3));
        pbAgreeableness.getProgressDrawable().setColorFilter(Color.parseColor(user.getSocialColor()), PorterDuff.Mode.SRC_IN);
        pbEmotionalRange.setMax(100);
        pbEmotionalRange.setProgress(user.getAverageSocialLevels(4));
        pbEmotionalRange.getProgressDrawable().setColorFilter(Color.parseColor(user.getSocialColor()), PorterDuff.Mode.SRC_IN);
    }
}
