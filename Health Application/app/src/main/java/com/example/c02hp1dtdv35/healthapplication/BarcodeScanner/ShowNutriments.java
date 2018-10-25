package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.c02hp1dtdv35.healthapplication.R;
import com.google.gson.Gson;

public class ShowNutriments extends AppCompatActivity {
    private TextView nutritionFacts;
    Nutriments nutriments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_facts);
        getSupportActionBar().setTitle("Nutrition Facts");
        nutritionFacts = findViewById(R.id.nutrition_facts);
        this.nutritionFacts.setMovementMethod(new ScrollingMovementMethod());

        // Get Nutriments Object
        String jsonMyObject = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("nutriments");
        }
        nutriments = new Gson().fromJson(jsonMyObject, Nutriments.class);
        nutritionFacts.setText("Carbohydrate value: "  +nutriments.getCarbohydratesValue() + nutriments.getCarbohydratesUnit()
                + "\nEnergy Value: " + nutriments.getEnergyValue() + nutriments.getEnergyUnit() +"\nFat: " +nutriments.getFatValue() + nutriments.getFatUnit()
                +"\nProtein: " +nutriments.getProteinsValue() + nutriments.getProteinsUnit() +"\nSugar: " +nutriments.getSugarsValue() +nutriments.getSugarsUnit()
        +"\nSalt: "+nutriments.getSaltValue() + nutriments.getSaltUnit() + "\nSaturated Fat Value: " +nutriments.getSaturatedFatValue() + nutriments.getSaturatedFatValue());
    }
}
