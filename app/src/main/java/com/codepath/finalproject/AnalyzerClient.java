package com.codepath.finalproject;
//adding Watson Developer Cloud SDK for Java:

import android.util.Log;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Tone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneChatRequest;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Utterance;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.UtteranceAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.UtterancesTone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vf608 on 7/11/17.
 */

public class AnalyzerClient {
    //public static final String VERSION = "ToneAnalyzer.VERSION_DATE_2016_05_19";
    public static final String URL = "https://gateway.watsonplatform.net/tone-analyzer/api";
    public static final String USERNAME = "c6b490cd-ac76-4d2b-b14b-33b1dd9171f3";
    public static final String PASSWORD = "VrKRnKuXRhcs";
    ToneAnalyzer service;

    public AnalyzerClient(){
        service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
        service.setEndPoint(URL);
        service.setUsernameAndPassword(USERNAME, PASSWORD);
    }

    public void getToneScores(TextBody textBody) {
        ToneOptions options = new ToneOptions.Builder()
                .addTone(Tone.EMOTION).build();
        ToneAnalysis tone =
                service.getTone(textBody.getMessage(), options).execute();

        for(ToneCategory tc : tone.getDocumentTone().getTones()){
            for(ToneScore ts : tc.getTones()){
                switch(ts.getName()){
                    case("Anger"):
                        textBody.setToneLevel(0, ts.getScore());
                        break;
                    case("Disgust"):
                        textBody.setToneLevel(1, ts.getScore());
                        break;
                    case("Fear"):
                        textBody.setToneLevel(2, ts.getScore());
                        break;
                    case("Joy"):
                        textBody.setToneLevel(3, ts.getScore());
                        break;
                    case("Sadness"):
                        textBody.setToneLevel(4, ts.getScore());
                        break;
                }
            }
        }
    }

    public void getStyleScores(TextBody textBody) {
        ToneOptions options = new ToneOptions.Builder()
                .addTone(Tone.LANGUAGE).build();
        ToneAnalysis tone =
                service.getTone(textBody.getMessage(), options).execute();

        for(ToneCategory tc : tone.getDocumentTone().getTones()){
            for(ToneScore ts : tc.getTones()){
                switch(ts.getName()){
                    case("Analytical"):
                        textBody.setStyleLevel(0, ts.getScore());
                        break;
                    case("Confident"):
                        textBody.setStyleLevel(1, ts.getScore());
                        break;
                    case("Tentative"):
                        textBody.setStyleLevel(2, ts.getScore());
                        break;
                }
            }
        }
    }

    public void getSocialScores(TextBody textBody) {
        ToneOptions options = new ToneOptions.Builder()
                .addTone(Tone.SOCIAL).build();
        ToneAnalysis tone =
                service.getTone(textBody.getMessage(), options).execute();

        for(ToneCategory tc : tone.getDocumentTone().getTones()){
            for(ToneScore ts : tc.getTones()){
                switch(ts.getName()){
                    case("Openness"):
                        textBody.setSocialLevel(0, ts.getScore());
                        break;
                    case("Conscientiousness"):
                        textBody.setSocialLevel(1, ts.getScore());
                        break;
                    case("Extraversion"):
                        textBody.setSocialLevel(2, ts.getScore());
                        break;
                    case("Agreeableness"):
                        textBody.setSocialLevel(3, ts.getScore());
                        break;
                    case("Emotional Range"):
                        textBody.setSocialLevel(4, ts.getScore());
                        break;
                }
            }
        }
    }

    public void getUtteranceScores(TextBody textBody) {
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
                switch(ts.getName()){
                    case("Sad"):
                        Log.i("Sad", "got score");
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
    }
}
