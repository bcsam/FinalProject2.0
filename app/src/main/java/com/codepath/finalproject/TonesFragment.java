package com.codepath.finalproject;

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

    public TonesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tones, container, false);
        textBody = getArguments().getParcelable("textBody");
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
        setTexts();
        setProgressBars();
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

    /*
    public void setScoreTexts(){
        tvAngerScore.setText(String.valueOf(textBody.getToneLevel(0)));
        tvDisgustScore.setText(String.valueOf(textBody.getToneLevel(1)));
        tvFearScore.setText(String.valueOf(textBody.getToneLevel(2)));
        tvJoyScore.setText(String.valueOf(textBody.getToneLevel(3)));
        tvSadnessScore.setText(String.valueOf(textBody.getToneLevel(4)));
    }
    */

    public void setProgressBars(){
        pbAnger.setMax(100);
        pbAnger.setProgress(textBody.getToneLevel(0));
        pbDisgust.setMax(100);
        pbDisgust.setProgress(textBody.getToneLevel(1));
        pbFear.setMax(100);
        pbFear.setProgress(textBody.getToneLevel(2));
        pbJoy.setMax(100);
        pbJoy.setProgress(textBody.getToneLevel(3));
        pbSadness.setMax(100);
        pbSadness.setProgress(textBody.getToneLevel(4));
    }

}
