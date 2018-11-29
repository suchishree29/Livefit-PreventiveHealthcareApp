package com.example.c02hp1dtdv35.healthapplication.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.MutableDocument;
import com.example.c02hp1dtdv35.healthapplication.Application;
import com.example.c02hp1dtdv35.healthapplication.Home.WatsonScreen;
import com.example.c02hp1dtdv35.healthapplication.R;


public class UserProfileActivity extends AppCompatActivity {


    EditText weight, Bloodglucose, Cholesterol, firstName, lastName, emailId;
    Switch Switch1,Switch2;
    String[] diseases_array = {"High Blood Pressure", "Low Blood Pressure", "Diabetes", "Cholesterol"};
    String[] heightarray1 = {"1","2","3","4","5","6","7","8"};
    String[] heightarray2 = {"1","2","3","4","5","6","7","8","9","10","11"};
    String[] allergens_array = {"Eggs", "Milk", "Peanuts", "Fish", "Wheat", "Soy"};
    private Database db;
    String diseases,allergens,Weight,bloodglucose,cholesterol,heightfoot,heightinches;
    private static final String TAG = UserProfileActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        weight = findViewById(R.id.Weight);
        firstName = findViewById(R.id.firstNameTxt);
        lastName = findViewById(R.id.lastNameTxt);
        emailId = findViewById(R.id.emailTxt);

        Switch1 =  findViewById(R.id.switch1);
        Switch2 = findViewById(R.id.switch2);

        Bloodglucose = findViewById(R.id.bloodglucose);
        Cholesterol = findViewById(R.id.cholesterol);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter adapter =  new ArrayAdapter(this,android.R.layout.simple_spinner_item,diseases_array);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        final Spinner allergensSpinner = findViewById(R.id.alergensSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter allergensAdapter =  new ArrayAdapter(this,android.R.layout.simple_spinner_item,allergens_array);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        allergensSpinner.setAdapter(allergensAdapter);


        final Spinner heightspinner1 = (Spinner) findViewById(R.id.heightspinner1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter adapter1 =  new ArrayAdapter(this,android.R.layout.simple_spinner_item,heightarray1);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        heightspinner1.setAdapter(adapter1);

        final Spinner heightspinner2 = (Spinner) findViewById(R.id.heightspinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter adapter2 =  new ArrayAdapter(this,android.R.layout.simple_spinner_item,heightarray2);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        heightspinner2.setAdapter(adapter2);

        Button button = (Button) findViewById(R.id.save);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Simple Button 1", Toast.LENGTH_LONG).show();//display the text of button1
                //SignUpApiCall();

                String statusSwitch1, statusSwitch2;
                if (Switch1.isChecked())
                    statusSwitch1 = Switch1.getTextOn().toString();
                else
                    statusSwitch1 = Switch1.getTextOff().toString();
                if (Switch2.isChecked())
                    statusSwitch2 = Switch2.getTextOn().toString();
                else {
                    statusSwitch2 = Switch2.getTextOff().toString();
                }

                //Height = height.getText().toString();
                Weight = weight.getText().toString();
                bloodglucose = Bloodglucose.getText().toString();
                cholesterol = Cholesterol.getText().toString();

                diseases = spinner.getSelectedItem().toString();
                allergens = allergensSpinner.getSelectedItem().toString();
                heightfoot = heightspinner1.getSelectedItem().toString();
                heightinches = heightspinner2.getSelectedItem().toString();

                //Code to save food data to couchbase
                Application application = (Application) getApplication();
                String username = application.getUsername();
                db = application.getDatabase();
                System.out.println("Database : " + db + "   Username:  " + username);

                if (db == null) throw new IllegalArgumentException();
                MutableDocument mDoc = new MutableDocument();
                mDoc.setString("height",heightfoot + " " + heightinches);
                mDoc.setString("weight",Weight);
                mDoc.setString("bloodglucose",bloodglucose);
                mDoc.setString("cholesterol", cholesterol);
                mDoc.setString("diseases", diseases);
                mDoc.setString("Smoking", statusSwitch1);
                mDoc.setString("Alcoholic", statusSwitch2);
                mDoc.setString("type", "task-list");
                mDoc.setString("owner", username);
                try {
                    db.save(mDoc);

                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Details are added successfully!", Toast.LENGTH_SHORT);
                    toast.show();

                    Intent i = new Intent(UserProfileActivity.this,WatsonScreen.class);
                    startActivity(i);
                } catch (CouchbaseLiteException e) {
                    com.couchbase.lite.internal.support.Log.e(TAG, "Failed to save the doc - %s", e, mDoc);
                }
            }
        });


    }
}
