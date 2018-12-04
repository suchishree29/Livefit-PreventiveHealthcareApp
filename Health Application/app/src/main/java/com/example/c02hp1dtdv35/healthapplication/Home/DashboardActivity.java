package com.example.c02hp1dtdv35.healthapplication.Home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.c02hp1dtdv35.healthapplication.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.numetriclabz.numandroidcharts.BarChart;
import com.numetriclabz.numandroidcharts.ChartData;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        graph = findViewById(R.id.graph);
        BarChart barChart = (BarChart) findViewById(R.id.barChart);

        List<ChartData> value = new ArrayList();
                value.add(new ChartData(4f, "2001"));//values.add(new ChartData(y,&quot;X-Labels&quot;));<br />
        value.add(new ChartData(9f, "2002"));
        value.add(new ChartData(18f, "2003"));
        value.add(new ChartData(6f, "2004"));
        value.add(new ChartData(15f, "2005"));

        List<ChartData> trendLines = new ArrayList();
        trendLines.add(new ChartData(16f, 18f, "Target"));
         barChart.setData(value);
         barChart.setTrendZones(trendLines);
//         barChart.set
        barChart.setGesture(true);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        series.setSpacing(1);
        graph.addSeries(series);

        double xInterval=1.0;
        graph.getViewport().setXAxisBoundsManual(true);

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 5),
                new DataPoint(1, 5),
                new DataPoint(2, 5),
                new DataPoint(3, 5),
                new DataPoint(4, 5)
        });
        series.setColor(Color.GREEN);
        graph.addSeries(series1);
    }

}
