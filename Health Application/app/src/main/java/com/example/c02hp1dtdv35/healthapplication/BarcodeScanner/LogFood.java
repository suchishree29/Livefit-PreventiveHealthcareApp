package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.MutableDocument;
import com.example.c02hp1dtdv35.healthapplication.Application;
import com.example.c02hp1dtdv35.healthapplication.Home.CameraFragment;
import com.example.c02hp1dtdv35.healthapplication.R;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class LogFood extends AppCompatActivity {

    private ImageView prodImg;
    private TextView prodName;
    private TextView nutritionFacts;
    private TextView servingSize;
    private TextView caloriesTxt;
    private TextView allergensTxt;
    private TextView dateTxt;
    private Button logBtn;
    private Spinner spinner;
    Nutriments nutriments;
    private Database db;
    String product;
    String serving_size;
    String calories;
    String allergens;
    String date;
    String imgUrl;
    String selectedMealCourse;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private static final String TAG = LogFood.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_food);
        getSupportActionBar().setTitle("Log Food");
        prodImg = findViewById(R.id.product_image);
        prodName = findViewById(R.id.product_name);
        servingSize = findViewById(R.id.servingSizeTxt);
        caloriesTxt = findViewById(R.id.caloriesTxt);
        allergensTxt = findViewById(R.id.allergensTxt);
        logBtn = findViewById(R.id.logBtn);
        this.prodName.setMovementMethod(new ScrollingMovementMethod());
        nutritionFacts = findViewById(R.id.nutrition_facts);
        nutritionFacts.setMovementMethod(LinkMovementMethod.getInstance());

        //Get the bundle
        Bundle bundle = getIntent().getExtras();

        //Extract the product name and image…
        product = bundle.getString("product_name");
        this.prodName.setText("Product Name: " + product);
        imgUrl = bundle.getString("product_image");
        Picasso.get().load(imgUrl).into(prodImg);

        //Get Serving size data from the bundle
        serving_size = bundle.getString("serving_size");
        this.servingSize.setText("Serving Size: " + serving_size);

        //Get Calories Data from the bundle
        calories = bundle.getString("calories");
        this.caloriesTxt.setText("Calories: " + calories);

        //Get Allergens Data from the bundle
        allergens = bundle.getString("allergens");
        this.allergensTxt.setText("Allergens: " + allergens);

        // Get Nutriments Object
        String jsonMyObject = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("nutriments");
        }
        nutriments = new Gson().fromJson(jsonMyObject, Nutriments.class);

        //Code for Meals dropdown
        spinner = findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meals_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

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
                MutableDocument mDoc = new MutableDocument();
                mDoc.setString("name",product);
                mDoc.setString("product_img",imgUrl);
                mDoc.setString("meal_date",date);
                mDoc.setString("serving_size", serving_size);
                mDoc.setString("meal_course", selectedMealCourse);
                mDoc.setString("type", "task-list");
                mDoc.setString("owner", username);
                try {
                    db.save(mDoc);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            product + "product is logged successfully!", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (CouchbaseLiteException e) {
                    com.couchbase.lite.internal.support.Log.e(TAG, "Failed to save the doc - %s", e, mDoc);
                }
            }
        });

    }

    public void onClick(View v){
        Intent nutrimentsIntent = new Intent(getApplicationContext(),ShowNutriments.class);
        nutrimentsIntent.putExtra("nutriments", new Gson().toJson(nutriments));
        startActivity(nutrimentsIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent cameraFragement= new Intent(LogFood.this, CameraFragment.class);
        startActivity(cameraFragement);
    }
}
