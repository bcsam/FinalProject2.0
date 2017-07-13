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

public class UtteranceFragment extends Fragment {

    TextView tvSadScore;
    TextView tvFrustratedScore;
    TextView tvSatisfiedScore;
    TextView tvExcitedScore;
    TextView tvPoliteScore;
    TextView tvImpoliteScore;
    TextView tvSympatheticScore;
    ProgressBar pbSad;
    ProgressBar pbFrustrated;
    ProgressBar pbSatisfied;
    ProgressBar pbExcited;
    ProgressBar pbPolite;
    ProgressBar pbImpolite;
    ProgressBar pbSympathetic;
    TextBody textBody;

    public UtteranceFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_utterance, container, false);
        textBody = getArguments().getParcelable("textBody");
        tvSadScore = (TextView) v.findViewById(R.id.tvSadScore);
        tvFrustratedScore = (TextView) v.findViewById(R.id.tvFrustratedScore);
        tvSatisfiedScore = (TextView) v.findViewById(R.id.tvSatisfiedScore);
        tvExcitedScore = (TextView) v.findViewById(R.id.tvExcitedScore);
        tvPoliteScore = (TextView) v.findViewById(R.id.tvPoliteScore);
        tvImpoliteScore = (TextView) v.findViewById(R.id.tvImpoliteScore);
        tvSympatheticScore = (TextView) v.findViewById(R.id.tvSympatheticScore);
        pbSad = (ProgressBar) v.findViewById(R.id.pbSad);
        pbFrustrated = (ProgressBar) v.findViewById(R.id.pbFrustrated);
        pbSatisfied = (ProgressBar) v.findViewById(R.id.pbSatisfied);
        pbExcited = (ProgressBar) v.findViewById(R.id.pbExcited);
        pbPolite = (ProgressBar) v.findViewById(R.id.pbPolite);
        pbImpolite = (ProgressBar) v.findViewById(R.id.pbImpolite);
        pbSympathetic = (ProgressBar) v.findViewById(R.id.pbSympathetic);
        setTexts();
        setProgressBars();
        return v;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    public void setTexts() {
        tvSadScore.setText(String.valueOf(textBody.getUtteranceLevel(0)));
        tvFrustratedScore.setText(String.valueOf(textBody.getUtteranceLevel(1)));
        tvSatisfiedScore.setText(String.valueOf(textBody.getUtteranceLevel(2)));
        tvExcitedScore.setText(String.valueOf(textBody.getUtteranceLevel(3)));
        tvPoliteScore.setText(String.valueOf(textBody.getUtteranceLevel(4)));
        tvImpoliteScore.setText(String.valueOf(textBody.getUtteranceLevel(5)));
        tvSympatheticScore.setText(String.valueOf(textBody.getUtteranceLevel(6)));
    }

    public void setProgressBars(){
        pbSad.setMax(100);
        pbSad.setProgress(textBody.getUtteranceLevel(0));
        pbSad.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getUtteranceColor(textBody.getUtteranceLevel(0))), PorterDuff.Mode.SRC_IN);
        pbFrustrated.setMax(100);
        pbFrustrated.setProgress(textBody.getUtteranceLevel(1));
        pbFrustrated.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getUtteranceColor(textBody.getUtteranceLevel(1))), PorterDuff.Mode.SRC_IN);
        pbSatisfied.setMax(100);
        pbSatisfied.setProgress(textBody.getUtteranceLevel(2));
        pbSatisfied.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getUtteranceColor(textBody.getUtteranceLevel(2))), PorterDuff.Mode.SRC_IN);
        pbExcited.setMax(100);
        pbExcited.setProgress(textBody.getUtteranceLevel(3));
        pbExcited.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getUtteranceColor(textBody.getUtteranceLevel(3))), PorterDuff.Mode.SRC_IN);
        pbPolite.setMax(100);
        pbPolite.setProgress(textBody.getUtteranceLevel(4));
        pbPolite.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getUtteranceColor(textBody.getUtteranceLevel(4))), PorterDuff.Mode.SRC_IN);
        pbImpolite.setMax(100);
        pbImpolite.setProgress(textBody.getUtteranceLevel(5));
        pbImpolite.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getUtteranceColor(textBody.getUtteranceLevel(5))), PorterDuff.Mode.SRC_IN);
        pbSympathetic.setMax(100);
        pbSympathetic.setProgress(textBody.getUtteranceLevel(6));
        pbSympathetic.getProgressDrawable().setColorFilter(Color.parseColor(textBody.getUtteranceColor(textBody.getUtteranceLevel(6))), PorterDuff.Mode.SRC_IN);
    }

}
