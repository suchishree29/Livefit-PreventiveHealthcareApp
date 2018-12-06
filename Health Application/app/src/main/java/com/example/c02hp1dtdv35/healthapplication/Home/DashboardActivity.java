package com.example.c02hp1dtdv35.healthapplication.Home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.c02hp1dtdv35.healthapplication.Application;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.DailyValues;
import com.example.c02hp1dtdv35.healthapplication.Login.UserProfile;
import com.example.c02hp1dtdv35.healthapplication.R;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.numetriclabz.numandroidcharts.BarChart;
import com.numetriclabz.numandroidcharts.ChartData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    //GraphView graph;

    private Database database;

     String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
       // graph = findViewById(R.id.graph);
        BarChart barChart =  findViewById(R.id.barChart);

        String date1,date2,date3,date4,date5;
        int year,day,month;

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);


//        day = cal.get(Calendar.DAY_OF_MONTH);

        date1 = (month+1) +"/" + day + "/" + year;

        DailyValues dv1 = dailyValuesForDate(date1);

        day = day -1;
        date2 = (month+1) +"/" + day + "/" + year;
        DailyValues dv2 = dailyValuesForDate(date2);

        day = day -1;
        date3 = (month+1) +"/" + day + "/" + year;
        DailyValues dv3 = dailyValuesForDate(date3);


        day = day -1;
        date4 = (month+1) +"/" + day + "/" + year;
        DailyValues dv4 = dailyValuesForDate(date4);

        day = day -1;
        date5 = (month+1) +"/" + day + "/" + year;
        DailyValues dv5 = dailyValuesForDate(date5);



        List<ChartData> value = new ArrayList();
        value.add(new ChartData(dv5.getTotalCalories().floatValue(), date5));//values.add(new ChartData(y,&quot;X-Labels&quot;));<br />
        value.add(new ChartData(dv4.getTotalCalories().floatValue(), date4));
        value.add(new ChartData(dv3.getTotalCalories().floatValue(), date3));
        value.add(new ChartData(dv2.getTotalCalories().floatValue(), date2));
        value.add(new ChartData(dv1.getTotalCalories().floatValue(), date1));


        UserProfile fromDB = null;
        Query query;
        query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
                .where(Expression.property("type").equalTo(Expression.string("profile")))
                .orderBy(Ordering.property("dateUpdated").descending());

        try {
            ResultSet rs = query.execute();

            Result row;



            while ((row = rs.next()) != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                // Ignore undeclared properties
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                Dictionary valueMap = row.getDictionary(database.getName());

                fromDB = objectMapper.convertValue(valueMap.toMap(),UserProfile.class);
                break;

            }


        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

//        value.add(new ChartData(4f, "2001"));//values.add(new ChartData(y,&quot;X-Labels&quot;));<br />
//        value.add(new ChartData(9f, "2002"));
//        value.add(new ChartData(18f, "2003"));
//        value.add(new ChartData(6f, "2004"));
//        value.add(new ChartData(15f, "2005"));


        String goalCalorie = fromDB.getGoalcalories();
        Float val = new Float(goalCalorie);

        List<ChartData> trendLines = new ArrayList();
        trendLines.add(new ChartData(val, val+10, "Target"));
         barChart.setData(value);
         barChart.setTrendZones(trendLines);
//         barChart.set
        barChart.setGesture(false);

        barChart.setDescription("Calorie Goals VS Intake");


    }

    DailyValues dailyValuesForDate(String date)
    {

        DailyValues dailyDataOnLoad = null;

        Application application = (Application) getApplication();
         username= application.getUsername();


        // dailyDataOnLoad = null;
        database = application.getDatabase();

        DailyValues dailyData =null;

        Double totalCalories =0.0, totalSugar=0.0,totalFat=0.0, totalProtein=0.0,totalSalt=0.0;


        Query query;
        query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.database(database))
            .where(Expression.property("type").equalTo(Expression.string("daily-data"))
                    .and(Expression.property("date").equalTo(Expression.string(date)))
                    .and(Expression.property("owner").equalTo(Expression.string(username))));
        try {
            ResultSet rs = query.execute();

            Result row;

            while ((row = rs.next()) != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                // Ignore undeclared properties
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                Dictionary valueMap = row.getDictionary(database.getName());

                dailyData = objectMapper.convertValue(valueMap.toMap(),DailyValues.class);
                totalCalories += dailyData.getTotalCalories();
                totalFat+= dailyData.getTotalFat();
                totalProtein += dailyData.getTotalProtein();
                totalSugar += dailyData.getTotalSugar();
                totalSalt += dailyData.getTotalSalt();

            }

            dailyDataOnLoad = new DailyValues( date,  totalCalories,  totalSugar,  totalFat,  totalProtein,  totalSalt,  "daily-data",  username);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return dailyDataOnLoad;
    }

    public static Date subtractDay(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }


}
