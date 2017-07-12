package com.codepath.finalproject;
//adding Watson Developer Cloud SDK for Java:

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Tone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;

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
                        textBody.setAngerLevel(ts.getScore());
                        break;
                    case("Disgust"):
                        textBody.setDigustLevel(ts.getScore());
                        break;
                    case("Fear"):
                        textBody.setFearLevel(ts.getScore());
                        break;
                    case("Joy"):
                        textBody.setJoyLevel(ts.getScore());
                        break;
                    case("Sadness"):
                        textBody.setSadnessLevel(ts.getScore());
                        break;
                }
            }
        }
    }
}
