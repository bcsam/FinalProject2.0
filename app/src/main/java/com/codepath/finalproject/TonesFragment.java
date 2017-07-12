package com.codepath.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    TextBody textBody;

    public TonesFragment() {
        // Required empty public constructor
    }

    /*_
    public static TonesFragment newInstance(){
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }
    */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*textBody = getArguments().getParcelable("textBody");
        tvAngerScore = (TextView) getView().findViewById(R.id.tvAngerScore);
        tvDisgustScore = (TextView) getView().findViewById(R.id.tvDisgustScore);
        tvFearScore = (TextView) getView().findViewById(R.id.tvFearScore);
        tvJoyScore = (TextView) getView().findViewById(R.id.tvJoyScore);
        tvSadnessScore = (TextView) getView().findViewById(R.id.tvSadnessScore);

        //setTexts();*/
    }

    public void setTexts(){
        tvAngerScore.setText(String.valueOf(textBody.getToneLevel(0)));
        tvDisgustScore.setText(String.valueOf(textBody.getToneLevel(1)));
        tvFearScore.setText(String.valueOf(textBody.getToneLevel(2)));
        tvJoyScore.setText(String.valueOf(textBody.getToneLevel(3)));
        tvSadnessScore.setText(String.valueOf(textBody.getToneLevel(4)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tones, container, false);
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


}
