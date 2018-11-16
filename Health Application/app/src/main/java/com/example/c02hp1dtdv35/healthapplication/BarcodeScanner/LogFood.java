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
import com.couchbase.lite.Database;
import com.couchbase.lite.MutableDocument;
import com.example.c02hp1dtdv35.healthapplication.Application;
import com.example.c02hp1dtdv35.healthapplication.R;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class LogFood extends AppCompatActivity {

    private ImageView prodImg;
    private TextView dateTxt;
    private Button logBtn;
    private Spinner spinner;
    Nutriments nutriments;
    Product productLog;
    private Database db;
    String product;
    String date,imgUrl,selectedMealCourse;
    int year,day,month;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = LogFood.class.getSimpleName();

    final ArrayList<Product> products = new ArrayList<>();

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


        // Get Nutriments Object
        String nutrimentsJson = "";
        String productJson = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nutrimentsJson = extras.getString("nutriments");
            productJson = extras.getString("products");
        }
        nutriments = new Gson().fromJson(nutrimentsJson, Nutriments.class);
        productLog = new Gson().fromJson(productJson, Product.class);
        //Code for populating Recycler View
        // Initialize products


        products.add(productLog);

        // Create adapter passing in the products data
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
            }
        };

        logBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                selectedMealCourse = spinner.getSelectedItem().toString();

                //Code to save food data to couchbase
                Application application = (Application) getApplication();
                String username = application.getUsername();
                db = application.getDatabase();

                if (db == null) throw new IllegalArgumentException();

                for(Product productLog: products){

                    productLog.setMeal_course(selectedMealCourse);
                    productLog.setType("task-list");
                    productLog.setImageSmallUrl(imgUrl);
                    productLog.setMeal_date(date);
                    productLog.setOwner(username);

                    ObjectMapper objectMapper = new ObjectMapper();
                    // Ignore undeclared properties
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    HashMap<String,Object> productMap = objectMapper.convertValue(productLog,HashMap.class);

                    MutableDocument mDoc= new MutableDocument(productMap);


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
