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
import android.util.Log;
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
        User user = getArguments().getParcelable("user");
        GraphAnalyzerClient client = new GraphAnalyzerClient(getContext(), user, graph);
        if(user.getName().equals("Me"))
            client.execute(getMyGraph(user));
        else
            client.execute(getGraph(user));
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public SMS[] getMyGraph(User user) {
        Uri uri = Uri.parse("content://sms/sent");
        Cursor c = getContext().getContentResolver().query(uri, null, null, null, null);
        ((Activity) getContext()).startManagingCursor(c);
        int count = c.getCount()/10;
        SMS[] smsList = new SMS[10];
        // Read the sms data and store it in the listco
        if (c.moveToFirst()) {
            for (int i = 0; i < 10; i++) {
                String fullText = "";
                for(int j = 0; j < count; j++) {
                    String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                    fullText += ". " + text;
                    c.moveToNext();
                }
                SMS sms = new SMS();
                sms.setBody(fullText);
                smsList[i] = sms;
            }
        }
        c.close();
        return smsList;
    }

    public SMS getGraph(User user) {
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = getContext().getContentResolver().query(uri, null, "address='"+user.getNumber()+"'", null, null);
        ((Activity) getContext()).startManagingCursor(c);
        String fullText = "";

        // Read the sms data and store it in the listco
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                fullText += ". "+text;
                c.moveToNext();
            }
        }
        c.close();
        SMS sms = new SMS();
        sms.setBody(fullText);
        return sms;
    }

    public class GraphAnalyzerClient extends AsyncTask<SMS, String, SMS> {
        //public static final String VERSION = "ToneAnalyzer.VERSION_DATE_2016_05_19";
        public static final String URL = "https://gateway.watsonplatform.net/tone-analyzer/api";
        public static final String USERNAME = "16d48b36-2e71-452c-bd1f-b5419a3ae48a";
        public static final String PASSWORD = "dXaJBi3c2Joj";
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
            Log.i("client", "in background");
            final SMS[] smsList = params;
            for(int i = 0; i < params.length; i++) {
                getScores(params[i]);
                user.updateScores(params[i]);
            }
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ProgressBar pbLoading = (ProgressBar) ((Activity) context).findViewById(R.id.pbGraphLoading);
                    pbLoading.setVisibility(View.GONE);
                    for(int i = 0; i< 5; i++) {
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                                new DataPoint(0, smsList[0].getToneLevel(i)),
                                new DataPoint(1, smsList[1].getToneLevel(i)),
                                new DataPoint(2, smsList[2].getToneLevel(i)),
                                new DataPoint(3, smsList[3].getToneLevel(i)),
                                new DataPoint(4, smsList[4].getToneLevel(i)),
                                new DataPoint(5, smsList[5].getToneLevel(i)),
                                new DataPoint(6, smsList[6].getToneLevel(i)),
                                new DataPoint(7, smsList[7].getToneLevel(i)),
                                new DataPoint(8, smsList[8].getToneLevel(i)),
                                new DataPoint(9, smsList[9].getToneLevel(i))
                        });
                        series.setColor(Color.parseColor(smsList[0].getToneColor(i)));
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