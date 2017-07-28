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

public class StylesFragment extends Fragment{
    TextView tvAnalyticalScore;
    TextView tvConfidentScore;
    TextView tvTentativeScore;
    ProgressBar pbAnalytical;
    ProgressBar pbConfident;
    ProgressBar pbTentative;
    SMS sms;
    User user;

    public StylesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_styles, container, false);
        tvAnalyticalScore = (TextView) v.findViewById(R.id.tvAnalyticalScore);
        tvConfidentScore = (TextView) v.findViewById(R.id.tvConfidentScore);
        tvTentativeScore = (TextView) v.findViewById(R.id.tvTentativeScore);
        pbAnalytical = (ProgressBar) v.findViewById(R.id.pbAnalytical);
        pbConfident = (ProgressBar) v.findViewById(R.id.pbConfident);
        pbTentative = (ProgressBar) v.findViewById(R.id.pbTentative);
        pbAnalytical.setMax(100);
        pbConfident.setMax(100);
        pbTentative.setMax(100);
        String activity = getArguments().getString("activity");
        if(activity.equals("PostCheckActivity") || activity.equals("MessageDetailActivity")) {
            sms = getArguments().getParcelable("sms");
            pbAnalytical.getProgressDrawable().setColorFilter(Color.parseColor(sms.getStyleColor()), PorterDuff.Mode.SRC_IN);
            pbConfident.getProgressDrawable().setColorFilter(Color.parseColor(sms.getStyleColor()), PorterDuff.Mode.SRC_IN);
            pbTentative.getProgressDrawable().setColorFilter(Color.parseColor(sms.getStyleColor()), PorterDuff.Mode.SRC_IN);
            setTexts();
            setProgressBars();
        }
        else if(activity.equals("ProfileActivity")){
            user = getArguments().getParcelable("user");
            pbAnalytical.getProgressDrawable().setColorFilter(Color.parseColor(user.getStyleColor()), PorterDuff.Mode.SRC_IN);
            pbConfident.getProgressDrawable().setColorFilter(Color.parseColor(user.getStyleColor()), PorterDuff.Mode.SRC_IN);
            pbTentative.getProgressDrawable().setColorFilter(Color.parseColor(user.getStyleColor()), PorterDuff.Mode.SRC_IN);
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
        tvAnalyticalScore.setText(String.valueOf(sms.getStyleLevel(0)));
        tvConfidentScore.setText(String.valueOf(sms.getStyleLevel(1)));
        tvTentativeScore.setText(String.valueOf(sms.getStyleLevel(2)));
    }

    public void setProgressBars(){
        pbAnalytical.setProgress(sms.getStyleLevel(0));
        pbConfident.setProgress(sms.getStyleLevel(1));
        pbTentative.setProgress(sms.getStyleLevel(2));
    }

    public void setProfileTexts() {
        tvAnalyticalScore.setText(String.valueOf(user.getAverageStyleLevels(0)));
        tvConfidentScore.setText(String.valueOf(user.getAverageStyleLevels(1)));
        tvTentativeScore.setText(String.valueOf(user.getAverageStyleLevels(2)));
    }

    public void setProfileProgressBars(){
        pbAnalytical.setProgress(user.getAverageStyleLevels(0));
        pbConfident.setProgress(user.getAverageStyleLevels(1));
        pbTentative.setProgress(user.getAverageStyleLevels(2));
    }

}
