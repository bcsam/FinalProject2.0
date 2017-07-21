package com.codepath.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        User user = getArguments().getParcelable("user");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, user.getAverageToneLevels(0)),
                new DataPoint(1, user.getAverageToneLevels(1)),
                new DataPoint(2, user.getAverageToneLevels(2)),
                new DataPoint(3, user.getAverageToneLevels(3)),
                new DataPoint(4, user.getAverageToneLevels(4))
        });
        graph.addSeries(series);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}