package com.example.c02hp1dtdv35.healthapplication.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
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
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.DailyValues;
import com.example.c02hp1dtdv35.healthapplication.Home.WatsonScreen;
import com.example.c02hp1dtdv35.healthapplication.R;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class UserProfileActivity extends AppCompatActivity {


    EditText weight, Bloodglucose, Cholesterol, firstName, lastName, emailId;
    Switch Switch1,Switch2;
    String[] diseases_array = {"High Blood Pressure", "Low Blood Pressure", "Diabetes", "Cholesterol"};
    String[] heightarray1 = {"1","2","3","4","5","6","7","8"};
    String[] heightarray2 = {"1","2","3","4","5","6","7","8","9","10","11"};
    String[] allergens_array = {"Eggs", "Milk", "Peanuts", "Fish", "Wheat", "Soy"};

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    boolean smoker, alcoholic;


    List<String> list1=     new ArrayList<String>(Arrays.asList(diseases_array));
       List<String> list=     new ArrayList<String>(Arrays.asList(allergens_array));
    private Database db;
    String Weight,bloodglucose,cholesterol,heightfoot,heightinches, firstname, lastname, email,gender;

    private static final String TAG = UserProfileActivity.class.getSimpleName();

    private ArrayList<String> diseasesList = new ArrayList<>();

    private ArrayList<String> allergensList= new ArrayList<>();

    private Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Application application = (Application) getApplication();
        final String username = application.getUsername();
        db = application.getDatabase();

        UserProfile fromDB = null;

        query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(db))
                .where(Expression.property("type").equalTo(Expression.string("profile")))
                .orderBy(Ordering.property("dateUpdated").descending());

        try {
            ResultSet rs = query.execute();

            Result row;



            while ((row = rs.next()) != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                // Ignore undeclared properties
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                Dictionary valueMap = row.getDictionary(db.getName());

               fromDB = objectMapper.convertValue(valueMap.toMap(),UserProfile.class);
//
//                totalCalories += dailyData.getTotalCalories();
//                totalFat+= dailyData.getTotalFat();
//                totalProtein += dailyData.getTotalProtein();
//                totalSugar += dailyData.getTotalSugar();
//                totalSalt += dailyData.getTotalSalt();
                break;

            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        weight = findViewById(R.id.Weight);

        firstName = findViewById(R.id.firstNameTxt);
        lastName = findViewById(R.id.lastNameTxt);
        emailId = findViewById(R.id.emailTxt);

        Switch1 =  findViewById(R.id.switch1);
        Switch2 = findViewById(R.id.switch2);

        Bloodglucose = findViewById(R.id.bloodglucose);
        Cholesterol = findViewById(R.id.cholesterol);






        MultiSpinnerSearch allerList = (MultiSpinnerSearch) findViewById(R.id.alergensSpinner);
        final List<KeyPairBoolData> listArray = new ArrayList<KeyPairBoolData>();

        for(int i=0; i<list.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i+1);
            h.setName(list.get(i));
            h.setSelected(false);
            listArray.add(h);
        }

/***
 * -1 is no by default selection
 * 0 to length will select corresponding values
 */



        final MultiSpinnerSearch diseaseList = (MultiSpinnerSearch) findViewById(R.id.diseasesSpinner);
        final List<KeyPairBoolData> listArray1 = new ArrayList<KeyPairBoolData>();

        for(int i=0; i<list1.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i+1);
            h.setName(list1.get(i));
            h.setSelected(false);
            listArray1.add(h);
        }

/***
 * -1 is no by default selection
 * 0 to length will select corresponding values
 */



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

        if(fromDB!=null)
        {
            weight.setText(fromDB.getWeight());
            firstName.setText(fromDB.getFirstName());

            Bloodglucose.setText(fromDB.getBloodGlucose());

            Cholesterol.setText(fromDB.getCholestorol());
            lastName.setText(fromDB.getLastName());
            emailId.setText(fromDB.getEmailId());

            if(fromDB.getHeight() != null) {
                String[] heightInches = fromDB.getHeight().split(" ");

                heightspinner1.setSelection(getIndex(heightspinner1, heightInches[0]));

                heightspinner2.setSelection(getIndex(heightspinner2, heightInches[1]));
            }

//            heightspinner1.se
//            heightspinner2

            String gend = fromDB.getGender();

            RadioButton radioMale = (RadioButton) findViewById(R.id.radiobutton_male);
            RadioButton radiofemale = (RadioButton) findViewById(R.id.radiobutton_female);



            RadioGroup mGroup = (RadioGroup) findViewById(R.id.radiogroup_gender);

            if(gend != null) {
                if (gend.equals("male")) {
                    mGroup.check(radioMale.getId());
                } else
                    mGroup.check(radiofemale.getId());
            }

            Switch1.setChecked(fromDB.isSmoker());

            Switch2.setChecked(fromDB.isAlcoholic());

            if(fromDB.getDiseases() != null) {

                for (String disease : fromDB.getDiseases()) {
                    //list1.indexOf(disease);
                    listArray1.get(list1.indexOf(disease)).setSelected(true);
                }
            }

            if(fromDB.getAllergens() != null) {
                for (String allergen : fromDB.getAllergens()) {
                    //list1.indexOf(disease);
                    listArray.get(list.indexOf(allergen)).setSelected(true);
                }
            }

        }

        diseaseList.setItems(listArray1, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                        diseasesList.add(items.get(i).getName());
                    }
                }
            }
        });

        allerList.setItems(listArray, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                        allergensList.add(items.get(i).getName());
                    }
                }
            }
        });

        Button button = (Button) findViewById(R.id.save);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String statusSwitch1, statusSwitch2;
                smoker = Switch1.isChecked();
                alcoholic = Switch2.isChecked();


                //Height = height.getText().toString();
                Weight = weight.getText().toString();

                firstname = firstName.getText().toString();

                lastname = lastName.getText().toString();
                bloodglucose = Bloodglucose.getText().toString();
                cholesterol = Cholesterol.getText().toString();

                //diseases = spinner.getSelectedItem().toString();
               // allergens = allergensSpinner.getSelectedItem().toString();
                heightfoot = heightspinner1.getSelectedItem().toString();
                heightinches = heightspinner2.getSelectedItem().toString();

                radioGroup = (RadioGroup) findViewById(R.id.radiogroup_gender);
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButton = (RadioButton) findViewById(selectedId);

                gender = radioButton.getText().toString();

                //Code to save food data to couchbase

                System.out.println("Database : " + db + "   Username:  " + username);
                email = username;
                //                mDoc.setString("Smoking", statusSwitch1);
//                mDoc.setString("Alcoholic", statusSwitch2);


                UserProfile user = new UserProfile( firstname,  lastname,  email,  gender, heightfoot + " " + heightinches  ,Weight, "profile",  smoker,  alcoholic,  bloodglucose,  cholesterol, new Date() , diseasesList, allergensList);


              //  UserProfile user = new UserProfile();
                if (db == null) throw new IllegalArgumentException();
//                MutableDocument mDoc = new MutableDocument();
//                mDoc.setString("height",heightfoot + " " + heightinches);
//                mDoc.setString("weight",Weight);
//                mDoc.setString("bloodglucose",bloodglucose);
//                mDoc.setString("cholesterol", cholesterol);
//                mDoc.setString("diseases", diseases);
//                mDoc.setString("Smoking", statusSwitch1);
//                mDoc.setString("Alcoholic", statusSwitch2);
//                mDoc.setString("type", "task-list");
//                mDoc.setString("owner", username);
//                try {
//                    db.save(mDoc);
//
//                    Toast toast = Toast.makeText(getApplicationContext(),
//                            "Details are added successfully!", Toast.LENGTH_SHORT);
//                    toast.show();
//
//                    Intent i = new Intent(UserProfileActivity.this,WatsonScreen.class);
//                    startActivity(i);
//                } catch (CouchbaseLiteException e) {
//                    com.couchbase.lite.internal.support.Log.e(TAG, "Failed to save the doc - %s", e, mDoc);
//                }

                ObjectMapper objectMapper = new ObjectMapper();
                // Ignore undeclared properties
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                HashMap<String, Object> userMap = objectMapper.convertValue(user, HashMap.class);

                MutableDocument mDoc = new MutableDocument(userMap);

                try {
                    db.save(mDoc);
                    Toast toast = Toast.makeText(getApplicationContext(),
                             "User detils saved!", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (CouchbaseLiteException e) {
                    com.couchbase.lite.internal.support.Log.e(TAG, "Failed to save the doc - %s", e, mDoc);
                }
            }
        });


    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
}
