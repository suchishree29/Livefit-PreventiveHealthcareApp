package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.c02hp1dtdv35.healthapplication.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class LogFood extends AppCompatActivity {

    private ImageView prodImg;
    private TextView prodName;
    private TextView nutritionFacts;
    Nutriments nutriments;

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
//        String text = "<a href='http://www.google.com'> NUTRITION FACTS </a>";
//        nutritionFacts.setText(Html.fromHtml(text));

        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        //Extract the data…
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



    }

    public void onClick(View v){
        Intent nutrimentsIntent = new Intent(getApplicationContext(),ShowNutriments.class);
        nutrimentsIntent.putExtra("nutriments", new Gson().toJson(nutriments));
        startActivity(nutrimentsIntent);
    }
}
