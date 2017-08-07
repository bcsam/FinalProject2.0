package com.codepath.finalproject;
//adding Watson Developer Cloud SDK for Java:

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Tone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;
import com.ibm.watson.developer_cloud.util.GsonSingleton;


/**
 * Created by vf608 on 7/11/17.
 */

public class AnalyzerClient extends AsyncTask <SMS, String, SMS>{
    //public static final String VERSION = "ToneAnalyzer.VERSION_DATE_2016_05_19";
    public static final String URL = "https://gateway.watsonplatform.net/tone-analyzer/api";
    public static final String USERNAME = "a90138db-2017-4c69-ab73-a263d204208b";
    public static final String PASSWORD = "rTuDoZzHfyMA";
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
        final SMS sms = (SMS) params[0];
        getScores(sms);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                drawable.setColorFilter(Color.parseColor(sms.setBubbleColor()), PorterDuff.Mode.SRC_ATOP);
            }
        });
        return sms;
    }

    private ToneAnalysis getToneAnalysis(SMS sms) {
        ToneOptions options = new ToneOptions.Builder()
                .addTone(Tone.EMOTION)
                .addTone(Tone.LANGUAGE)
                .addTone(Tone.SOCIAL).build();
        return service.getTone(sms.getBody(), options).execute();
    }

    private ToneAnalysis jsonToToneAnalysis(String json) {
        return GsonSingleton.getGson().fromJson(json, ToneAnalysis.class);
    }

    private ToneAnalysis getToneAnalysisWithDB(SMS sms) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("tones.db", null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Tones(response TEXT, sms_id INTEGER PRIMARY KEY);");
        Cursor cursor = db.rawQuery("SELECT response from Tones where sms_id = " + sms.getId() + " LIMIT 1", null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            String json = cursor.getString(0);
            cursor.close();
            return jsonToToneAnalysis(json);
        }
        cursor.close();
        ToneAnalysis toneAnalysis = getToneAnalysis(sms);
        String json = toneAnalysis.toString();
        ContentValues values = new ContentValues();
        values.put("response", json);
        values.put("sms_id", sms.getId());
        db.insert("Tones", null, values);
        db.close();
        return toneAnalysis;
    }


    public void getScores(SMS sms){
        ToneAnalysis tone = getToneAnalysisWithDB(sms);

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
}
