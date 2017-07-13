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
    TextBody textBody;

    public StylesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_styles, container, false);
        textBody = getArguments().getParcelable("textBody");
        tvAnalyticalScore = (TextView) v.findViewById(R.id.tvAnalyticalScore);
        tvConfidentScore = (TextView) v.findViewById(R.id.tvConfidentScore);
        tvTentativeScore = (TextView) v.findViewById(R.id.tvTentativeScore);
        pbAnalytical = (ProgressBar) v.findViewById(R.id.pbAnalytical);
        pbConfident = (ProgressBar) v.findViewById(R.id.pbConfident);
        pbTentative = (ProgressBar) v.findViewById(R.id.pbTentative);
        setTexts();
        setProgressBars();
        return v;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    public void setTexts() {
        tvAnalyticalScore.setText(String.valueOf(textBody.getStyleLevel(0)));
        tvConfidentScore.setText(String.valueOf(textBody.getStyleLevel(1)));
        tvTentativeScore.setText(String.valueOf(textBody.getStyleLevel(2)));
    }

    public void setProgressBars(){
        pbAnalytical.setMax(100);
        pbAnalytical.setProgress(textBody.getStyleLevel(0));
        pbAnalytical.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getStyleColor(textBody.getStyleLevel(0))), PorterDuff.Mode.SRC_IN);
        pbConfident.setMax(100);
        pbConfident.setProgress(textBody.getStyleLevel(1));
        pbConfident.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getStyleColor(textBody.getStyleLevel(1))), PorterDuff.Mode.SRC_IN);
        pbTentative.setMax(100);
        pbTentative.setProgress(textBody.getStyleLevel(2));
        pbTentative.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getStyleColor(textBody.getStyleLevel(2))), PorterDuff.Mode.SRC_IN);
    }

}
