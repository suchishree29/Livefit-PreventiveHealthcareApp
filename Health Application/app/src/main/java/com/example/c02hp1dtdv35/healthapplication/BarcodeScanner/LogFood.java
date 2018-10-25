package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.c02hp1dtdv35.healthapplication.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class LogFood extends AppCompatActivity {

    private ImageView prodImg;
    private TextView prodName;
    private TextView nutritionFacts;
    private TextView dateTxt;
    Nutriments nutriments;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "LogFood";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_food);
        getSupportActionBar().setTitle("Log Food");
        prodImg = findViewById(R.id.product_image);
        prodName = findViewById(R.id.product_name);
        this.prodName.setMovementMethod(new ScrollingMovementMethod());
        nutritionFacts = findViewById(R.id.nutrition_facts);
        nutritionFacts.setMovementMethod(LinkMovementMethod.getInstance());

        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        //Extract the product name and image…
        String product = bundle.getString("product_name");
        this.prodName.setText("Product Name: " + product);
        String imgUrl = bundle.getString("product_image");
        Picasso.get().load(imgUrl).into(prodImg);

        // Get Nutriments Object
        String jsonMyObject = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("nutriments");
        }
        nutriments = new Gson().fromJson(jsonMyObject, Nutriments.class);

        // Code for Date Picker
        dateTxt = findViewById(R.id.dayTxt);
        dateTxt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

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

                String date = month +"/" + day + "/" + year;
                dateTxt.setText("Day : " + date);
            }
        };

    }

    public void onClick(View v){
        Intent nutrimentsIntent = new Intent(getApplicationContext(),ShowNutriments.class);
        nutrimentsIntent.putExtra("nutriments", new Gson().toJson(nutriments));
        startActivity(nutrimentsIntent);
    }
}
