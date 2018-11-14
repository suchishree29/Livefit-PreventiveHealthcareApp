package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.c02hp1dtdv35.healthapplication.Application;

import com.example.c02hp1dtdv35.healthapplication.R;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import com.example.c02hp1dtdv35.healthapplication.Home.CameraFragment;


import com.example.c02hp1dtdv35.healthapplication.R;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import com.google.zxing.integration.android.IntentIntegrator;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LogFood extends AppCompatActivity {

    private ImageView prodImg;



    private TextView prodName,nutritionFacts,servingSize,caloriesTxt,allergensTxt,dateTxt;



    private Button logBtn;
    private Spinner spinner;
    Nutriments nutriments;
    Product productLog;
    private Database db;


    String date,imgUrl,selectedMealCourse;

    int year,day,month;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = LogFood.class.getSimpleName();


    final ArrayList<Product> products = new ArrayList<>();


    private Query query;
    DailyValues dailyData;
    Double totalCalories =0.0, totalSugar=0.0,totalFat=0.0, totalProtein=0.0,totalSalt=0.0;

    ObjectMapper objectMapper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_food);
        getSupportActionBar().setTitle("Log Food");
        prodImg = findViewById(R.id.product_image);
        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        logBtn = findViewById(R.id.logBtn);







        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        imgUrl = bundle.getString("product_image");
        Picasso.get().load(imgUrl).into(prodImg);

        //Extract the product name and image…



        //Extract the product name and image…




        // Get Nutriments Object
        String jsonMyObject = "";
        String productJson = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("nutriments");
            productJson = extras.getString("products");
        }
        nutriments = new Gson().fromJson(jsonMyObject, Nutriments.class);

        productLog = new Gson().fromJson(productJson, Product.class);

        //Code for populating Recycler View
        // Initialize products

        productLog = new Gson().fromJson(productJson, Product.class);
        //Code for populating Recycler View
        // Initialize products



        products.add(productLog);


        // Create adapter passing in the sample user data
        ProductsAdapter adapter = new ProductsAdapter(products);

        // Attach the adapter to the recyclerview to populate items
        rvProducts.setAdapter(adapter);
        // Set layout manager to position the items
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        //Code for Meals dropdown
        spinner = findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.meals_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);

        // Code for Date Picker
        dateTxt = findViewById(R.id.dayTxt);
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        date = (month+1) +"/" + day + "/" + year;
        dateTxt.setText("Day : " + date);


        Application application = (Application) getApplication();
        final String username = application.getUsername();
        db = application.getDatabase();

        if (db == null) throw new IllegalArgumentException();


        query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(db))
                .where(Expression.property("type").equalTo(Expression.string("daily-data"))
                        .and(Expression.property("date").equalTo(Expression.string(date))));
        // .orderBy(Ordering.property("name").ascending());
        try {
            ResultSet rs = query.execute();

            Result row;

            while ((row = rs.next()) != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                // Ignore undeclared properties
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                // String productName = row.getString(0);
                // String name = row.getString(1);
//                String name2 = row.getString("name");
                // Get dictionary corresponding to the database name
                Dictionary valueMap = row.getDictionary(db.getName());

                // Convert from dictionary to corresponding University object
                                        /*        Map ab = valueMap.toMap();
                                                String name = (String) ab.get("name");
                                                String serving_size = (String) ab.get("serving_size");
                                                String calories = (String) ab.get("calories");
                                                String allergens = (String) ab.get("allergens");*/

                //ProductRecycleView product = new ProductRecycleView(name,serving_size,calories,allergens);
                //ProductRecycleView university1 = objectMapper.convertValue(valueMap.toMap(),ProductRecycleView.class);
                Map mp= valueMap.toMap();
                dailyData = objectMapper.convertValue(valueMap.toMap(),DailyValues.class);


                totalCalories += dailyData.getTotalCalories();
                totalFat+= dailyData.getTotalFat();
                totalProtein += dailyData.getTotalProtein();
                totalSugar += dailyData.getTotalSugar();
                totalSalt += dailyData.getTotalSalt();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }


        dateTxt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                DatePickerDialog dialog = new DatePickerDialog(LogFood.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG,"onDateSet: Day: "+ month +"/" + day + "/" + year);

                date = month +"/" + day + "/" + year;
                dateTxt.setText("Day : " + date);

                totalCalories =0.0;
                totalSugar=0.0;
                totalFat=0.0;
                totalProtein=0.0;
                totalSalt=0.0;

                query = QueryBuilder.select(SelectResult.all())
                        .from(DataSource.database(db))
                        .where(Expression.property("type").equalTo(Expression.string("daily-data"))
                                .and(Expression.property("date").equalTo(Expression.string(date))));
                // .orderBy(Ordering.property("name").ascending());
                try {
                    ResultSet rs = query.execute();

                    Result row;

                    while ((row = rs.next()) != null) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        // Ignore undeclared properties
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        // String productName = row.getString(0);
                        // String name = row.getString(1);
//                String name2 = row.getString("name");
                        // Get dictionary corresponding to the database name
                        Dictionary valueMap = row.getDictionary(db.getName());

                        // Convert from dictionary to corresponding University object
                                        /*        Map ab = valueMap.toMap();
                                                String name = (String) ab.get("name");
                                                String serving_size = (String) ab.get("serving_size");
                                                String calories = (String) ab.get("calories");
                                                String allergens = (String) ab.get("allergens");*/

                        //ProductRecycleView product = new ProductRecycleView(name,serving_size,calories,allergens);
                        //ProductRecycleView university1 = objectMapper.convertValue(valueMap.toMap(),ProductRecycleView.class);
                        Map mp= valueMap.toMap();
                        dailyData = objectMapper.convertValue(valueMap.toMap(),DailyValues.class);


                        totalCalories += dailyData.getTotalCalories();
                        totalFat+= dailyData.getTotalFat();
                        totalProtein += dailyData.getTotalProtein();
                        totalSugar += dailyData.getTotalSugar();
                        totalSalt += dailyData.getTotalSalt();
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };

        logBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                selectedMealCourse = spinner.getSelectedItem().toString();

                Double dailyCalories =0.0, dailySugar=0.0,dailyFat=0.0, dailyProtein=0.0,dailySalt=0.0;

                //Code to save food data to couchbase
                /*Application application = (Application) getApplication();
                String username = application.getUsername();
                db = application.getDatabase();

                if (db == null) throw new IllegalArgumentException();*/


                for(Product product: products){

                    product.setMeal_course(selectedMealCourse);
                    product.setType("task-list");
                    product.setImageSmallUrl(imgUrl);
                    product.setMeal_date(date);
                    product.setOwner(username);

                    objectMapper = new ObjectMapper();
                    // Ignore undeclared properties
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    // HashMap<String,Object> universityMap = objectMapper.convertValue(product,HashMap.class);
                    HashMap<String,Object> universityMap = objectMapper.convertValue(product,HashMap.class);

                    MutableDocument mDoc= new MutableDocument(universityMap);
                    String en = product.getNutriments().getEnergyValue();

                    Double val = new Double(en);
                    dailyCalories+= Double.valueOf(en);
                    dailyFat+= Double.valueOf(product.getNutriments().getFat());
                    dailyProtein += Double.valueOf(product.getNutriments().getProteins());
                    dailySugar += Double.valueOf(product.getNutriments().getSugars());
                    dailySalt += Double.valueOf(product.getNutriments().getSalt());

                    DailyValues logDailyValue = new DailyValues();;
                    if(dailyData== null)
                        dailyData = new DailyValues();

                    logDailyValue.setDate(date);
                    logDailyValue.setId(date);
                    logDailyValue.setTotalCalories(dailyCalories);
                    logDailyValue.setTotalFat(dailyFat);
                    logDailyValue.setTotalProtein(dailyProtein);
                    logDailyValue.setTotalSugar(dailySugar);
                    logDailyValue.setTotalSalt(dailySalt);
                    logDailyValue.setType("daily-data");
                    logDailyValue.setOwner(username);

                    objectMapper = new ObjectMapper();

                    HashMap<String,Object> dv = objectMapper.convertValue(logDailyValue,HashMap.class);
                    MutableDocument nDV= new MutableDocument(dv);
                    try {
                        db.save(nDV);
//                    Toast toast = Toast.makeText(getApplicationContext(),
//                            product + "product is logged successfully!", Toast.LENGTH_SHORT);
//                    toast.show();
                    } catch (CouchbaseLiteException e) {
                        com.couchbase.lite.internal.support.Log.e(TAG, "Failed to save the doc - %s", e, nDV);
                    }


                    try {
                        db.save(mDoc);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                product + "product is logged successfully!", Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (CouchbaseLiteException e) {
                        com.couchbase.lite.internal.support.Log.e(TAG, "Failed to save the doc - %s", e, mDoc);
                    }
                }


            }
        });

    }

}