package com.codepath.finalproject;
//adding Watson Developer Cloud SDK for Java:

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Tone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;

/**
 * Created by vf608 on 7/11/17.
 */

public class AnalyzerClient extends AsyncTask <SMS, String, SMS>{
    //public static final String VERSION = "ToneAnalyzer.VERSION_DATE_2016_05_19";
    public static final String URL = "https://gateway.watsonplatform.net/tone-analyzer/api";
    public static final String USERNAME = "16d48b36-2e71-452c-bd1f-b5419a3ae48a";
    public static final String PASSWORD = "dXaJBi3c2Joj";
    ToneAnalyzer service;
    Context context;
    Drawable drawable;

    public AnalyzerClient(Context context, Drawable d){
        this.context = context;
        service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
        service.setEndPoint(URL);
        service.setUsernameAndPassword(USERNAME, PASSWORD);
        drawable = d;
    }

    public AnalyzerClient(){
        service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
        service.setEndPoint(URL);
        service.setUsernameAndPassword(USERNAME, PASSWORD);
    }

    @Override
    protected SMS doInBackground(SMS... params) {
        Log.i("client", "in background");
        final SMS sms = (SMS) params[0];
        getScores(sms);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                drawable.setColorFilter(Color.parseColor(sms.getBubbleColor()), PorterDuff.Mode.SRC_ATOP);
            }
        });
        return sms;
    }


    public void getScores(SMS sms){
        ToneOptions options = new ToneOptions.Builder()
                .addTone(Tone.EMOTION)
                .addTone(Tone.LANGUAGE)
                .addTone(Tone.SOCIAL).build();
        ToneAnalysis tone =
                service.getTone(sms.getBody(), options).execute();

        for(ToneCategory tc : tone.getDocumentTone().getTones()){
            for(ToneScore ts : tc.getTones()){
                switch(ts.getName()){
                    case("Anger"):
                        sms.setToneLevel(0, ts.getScore());
                        break;
                    case("Disgust"):
                        sms.setToneLevel(1, ts.getScore());
                        break;
                    case("Fear"):
                        sms.setToneLevel(2, ts.getScore());
                        break;
                    case("Joy"):
                        sms.setToneLevel(3, ts.getScore());
                        break;
                    case("Sadness"):
                        sms.setToneLevel(4, ts.getScore());
                        break;
                    case("Analytical"):
                        sms.setStyleLevel(0, ts.getScore());
                        break;
                    case("Confident"):
                        sms.setStyleLevel(1, ts.getScore());
                        break;
                    case("Tentative"):
                        sms.setStyleLevel(2, ts.getScore());
                        break;
                    case("Openness"):
                        sms.setSocialLevel(0, ts.getScore());
                        break;
                    case("Conscientiousness"):
                        sms.setSocialLevel(1, ts.getScore());
                        break;
                    case("Extraversion"):
                        sms.setSocialLevel(2, ts.getScore());
                        break;
                    case("Agreeableness"):
                        sms.setSocialLevel(3, ts.getScore());
                        break;
                    case("Emotional Range"):
                        sms.setSocialLevel(4, ts.getScore());
                        break;
                }
            }
        }
    }

   /* public void getUtteranceScores(TextBody textBody) {
        List<Utterance> utterances = new ArrayList<>();
        Utterance utterance = new Utterance.Builder()
                .text(textBody.getMessage())
                .build();
        utterances.add(utterance);
        ToneChatRequest options = new ToneChatRequest.Builder()
                .utterances(utterances).build();
        UtterancesTone tone = service.getChatTone(options).execute();
        for(UtteranceAnalysis ua : tone.getUtterancesTone()){
            for(ToneScore ts : ua.getTones()){
                Log.i("UtteranceScore", "for loop 2");
                switch(ts.getName()){
                    case("Sad"):
                        Log.i("UtteranceScore", "got score");
                        textBody.setUtteranceLevel(0, ts.getScore());
                        break;
                    case("Frustrated"):
                        textBody.setUtteranceLevel(1, ts.getScore());
                        break;
                    case("Satisfied"):
                        textBody.setUtteranceLevel(2, ts.getScore());
                        break;
                    case("Excited"):
                        textBody.setUtteranceLevel(3, ts.getScore());
                        break;
                    case("Polite"):
                        textBody.setUtteranceLevel(4, ts.getScore());
                        break;
                    case("Impolite"):
                        textBody.setUtteranceLevel(5, ts.getScore());
                        break;
                    case("Sympathetic"):
                        textBody.setUtteranceLevel(6, ts.getScore());
                        break;
                }
            }
        }
    }*/
}
