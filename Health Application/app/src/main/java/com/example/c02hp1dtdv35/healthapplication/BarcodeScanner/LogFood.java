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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class LogFood extends AppCompatActivity {

    private ImageView prodImg;
    private TextView dateTxt;
    private Button logBtn;
    private Spinner spinner;
    Nutriments nutriments;
    Product productLog;
    private Database db;
    String product,serving_size,calories,allergens;
    String date,imgUrl,selectedMealCourse;
    int year,day,month;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = LogFood.class.getSimpleName();

    ArrayList<ProductRecycleView> products , product_list;


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

        //Extract the product name and image…
        product = bundle.getString("product_name");
        //product_list = bundle.getString("productList");
        //this.prodName.setText("Product Name: " + product);
        imgUrl = bundle.getString("product_image");
        Picasso.get().load(imgUrl).into(prodImg);

        //Get Serving size data from the bundle
        serving_size = bundle.getString("serving_size");

        //Get Calories Data from the bundle
        calories = bundle.getString("calories");

        //Get Allergens Data from the bundle
        allergens = bundle.getString("allergens");

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
        products = ProductRecycleView.createProductList(product,serving_size,calories,allergens,1);
        ArrayList<Product> productsMain = new ArrayList<>();

        productsMain.add(productLog);

        // Create adapter passing in the sample user data
        ProductsAdapter adapter = new ProductsAdapter(productsMain);

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

                for(ProductRecycleView product: products){
                    System.out.println(product);



                    productLog.setMeal_course(selectedMealCourse);
                    productLog.setType("task-list");
                    productLog.setImageSmallUrl(imgUrl);
                    productLog.setMeal_date(date);
                    productLog.setOwner(username);

                    ObjectMapper objectMapper = new ObjectMapper();
                    // Ignore undeclared properties
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                   // HashMap<String,Object> universityMap = objectMapper.convertValue(product,HashMap.class);
                    HashMap<String,Object> universityMap = objectMapper.convertValue(productLog,HashMap.class);

                    MutableDocument mDoc= new MutableDocument(universityMap);

       /*             MutableDocument mDoc = new MutableDocument();

                    mDoc.setString("name",product.getName());
                    mDoc.setString("product_img",imgUrl);
                    mDoc.setString("meal_date",date);
                    mDoc.setString("serving_size", product.getServing_size());
                    mDoc.setString("calories", product.getCalories());
                    mDoc.setString("allergens", product.getAllergens());
                    mDoc.setString("meal_course", selectedMealCourse);
                    mDoc.setString("type", "task-list");
                    mDoc.setString("owner", username);*/

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

    public void onClickNutriments(View v){
        Intent nutrimentsIntent = new Intent(getApplicationContext(),ShowNutriments.class);
        nutrimentsIntent.putExtra("nutriments", new Gson().toJson(nutriments));
        startActivity(nutrimentsIntent);
    }

}
