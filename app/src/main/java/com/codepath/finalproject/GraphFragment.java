package com.codepath.finalproject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Tone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by bcsam on 7/12/17.
 */

public class GraphFragment extends Fragment {

    GraphAnalyzerClient client;
    public GraphFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        GraphView graph = (GraphView) v.findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(9);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        int color = ContextCompat.getColor(getActivity(), R.color.darkGray);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(color);

        graph.getGridLabelRenderer().setHorizontalAxisTitle("# OF TEXTS");

        //color = ContextCompat.getColor(getActivity(), R.color.darkGray);

        graph.getGridLabelRenderer().setVerticalLabelsColor(color);
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(color);
        graph.getGridLabelRenderer().setGridColor(color);
        graph.getGridLabelRenderer().setVerticalAxisTitle("TONE SCORES");

        User user = getArguments().getParcelable("user");
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(color);
        client = new GraphAnalyzerClient(getContext(), user, graph);
        if(user.getName().equals("Me")) {
            //graph.getGridLabelRenderer().setHorizontalAxisTitle("TEXTS SENT");
            client.execute(getMyGraph(user));
        }
        else {
            //graph.getGridLabelRenderer().setHorizontalAxisTitle("TEXTS RECEIVED");
            client.execute(getGraph(user));
        }
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause(){
        client.cancel(true);
        super.onPause();
    }

    public SMS[] getMyGraph(User user) {
        Uri uri = Uri.parse("content://sms/sent");
        Cursor c = getContext().getContentResolver().query(uri, null, null, null, null);
        ((Activity) getContext()).startManagingCursor(c);
        int count = 0;
        int size = c.getCount()/10;
        if(size == 0)
            size = 1;
        SMS[] smsList = new SMS[10];
        if(c.getCount() == 0){
            for(int i = 0; i < smsList.length; i++){
                smsList[i] = new SMS();
                smsList[i].setBody("");
            }
        }
        // Read the sms data and store it in the listco
        if (c.moveToFirst()) {
            for (int i = 0; i < 10; i++) {
                if(count >= c.getCount()) {
                    smsList[i] = new SMS();
                    smsList[i].setBody("");
                }
                else {
                    String fullText = "";
                    for (int j = 0; j < size; j++) {
                        String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                        fullText += ". " + text;
                        c.moveToNext();
                    }
                    SMS sms = new SMS();
                    sms.setBody(fullText);
                    smsList[i] = sms;
                }
                count++;
            }
        }
        c.close();
        return smsList;
    }

    public SMS[] getGraph(User user) {
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = getContext().getContentResolver().query(uri, null, "address='"+user.getNumber()+"'", null, null);
        ((Activity) getContext()).startManagingCursor(c);
        int count = 0;
        int size = c.getCount()/10;
        if(size == 0)
            size = 1;
        SMS[] smsList = new SMS[10];
        if(c.getCount() == 0){
            for(int i = 0; i < smsList.length; i++){
                smsList[i] = new SMS();
                smsList[i].setBody("");
            }
        }
        // Read the sms data and store it in the listco
        if (c.moveToFirst()) {
            for (int i = 0; i < 10; i++) {
                if(count >= c.getCount()) {
                    smsList[i] = new SMS();
                    smsList[i].setBody("");
                }
                else {
                    String fullText = "";
                    for (int j = 0; j < size; j++) {
                        String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                        fullText += ". " + text;
                        c.moveToNext();
                    }
                    SMS sms = new SMS();
                    sms.setBody(fullText);
                    smsList[i] = sms;
                }
                count++;
            }
        }
        c.close();
        return smsList;
    }

    public class GraphAnalyzerClient extends AsyncTask<SMS, String, SMS> {
        //public static final String VERSION = "ToneAnalyzer.VERSION_DATE_2016_05_19";
        public static final String URL = "https://gateway.watsonplatform.net/tone-analyzer/api";
        public static final String USERNAME = "645552ab-28fd-4b44-9868-0854b00a3495";
        public static final String PASSWORD = "XTcJf0YpdSFv";
        ToneAnalyzer service;
        Context context;
        User user;
        GraphView graph;

        public GraphAnalyzerClient(Context context, User user, GraphView graph){
            this.context = context;
            service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
            service.setEndPoint(URL);
            service.setUsernameAndPassword(USERNAME, PASSWORD);
            this.user = user;
            this.graph = graph;
        }

        @Override
        protected SMS doInBackground(final SMS... params) {
            for(int i = 0; i < params.length; i++) {
                if(params[i].getBody().equals("")){
                    for(int j = 0; j < 5; j++)
                        params[i].setToneLevel(j, 0);
                }
                else
                    getScores(params[i]);
            }
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ProgressBar pbLoading = (ProgressBar) ((Activity) context).findViewById(R.id.pbGraphLoading);
                    pbLoading.setVisibility(View.GONE);
                    for(int i = 0; i< 5; i++) {
                        DataPoint[] dataPoints = new DataPoint[params.length];
                        for (int j = 0; j < params.length; j++)
                            dataPoints[j] = new DataPoint(j, params[j].getToneLevel(i));
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
                        series.setColor(Color.parseColor(params[0].getToneColor(i)));
                        //series.setDrawDataPoints(true);
                        graph.addSeries(series);
                    }
                    graph.setVisibility(View.VISIBLE);
                }
            });
            return params[0];
        }

        public void getScores(SMS sms) {
            ToneOptions options = new ToneOptions.Builder()
                    .addTone(Tone.EMOTION)
                    .addTone(Tone.LANGUAGE)
                    .addTone(Tone.SOCIAL).build();
            ToneAnalysis tone =
                    service.getTone(sms.getBody(), options).execute();

            for (ToneCategory tc : tone.getDocumentTone().getTones()) {
                for (ToneScore ts : tc.getTones()) {
                    switch (ts.getName()) {
                        case ("Anger"):
                            sms.setToneLevel(0, ts.getScore());
                            break;
                        case ("Disgust"):
                            sms.setToneLevel(1, ts.getScore());
                            break;
                        case ("Fear"):
                            sms.setToneLevel(2, ts.getScore());
                            break;
                        case ("Joy"):
                            sms.setToneLevel(3, ts.getScore());
                            break;
                        case ("Sadness"):
                            sms.setToneLevel(4, ts.getScore());
                            break;
                        case ("Analytical"):
                            sms.setStyleLevel(0, ts.getScore());
                            break;
                        case ("Confident"):
                            sms.setStyleLevel(1, ts.getScore());
                            break;
                        case ("Tentative"):
                            sms.setStyleLevel(2, ts.getScore());
                            break;
                        case ("Openness"):
                            sms.setSocialLevel(0, ts.getScore());
                            break;
                        case ("Conscientiousness"):
                            sms.setSocialLevel(1, ts.getScore());
                            break;
                        case ("Extraversion"):
                            sms.setSocialLevel(2, ts.getScore());
                            break;
                        case ("Agreeableness"):
                            sms.setSocialLevel(3, ts.getScore());
                            break;
                        case ("Emotional Range"):
                            sms.setSocialLevel(4, ts.getScore());
                            break;
                    }
                }
            }
        }
    }
}