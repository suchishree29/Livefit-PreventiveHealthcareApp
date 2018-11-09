package com.example.c02hp1dtdv35.healthapplication.Login;

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
import com.example.c02hp1dtdv35.healthapplication.R;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {


    EditText height, weight, Bloodglucose, Cholestrol;
    Switch Switch1,Switch2;
    private Spinner spinner;
    ArrayList<Login> loginArrayList=new ArrayList<>();
    String[] diseases_array = {"High Blood Pressure", "Low Blood Pressure", "Diabetes", "Cholestrol"};
    private Database db;
    String diseases, Height, Weight,bloodglucose,cholestrol;
    private static final String TAG = UserProfileActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        height = (EditText) findViewById(R.id.Height);
        Height = height.getText().toString();
        weight = (EditText) findViewById(R.id.Weight);
        Weight = weight.getText().toString();
        Switch1 = (Switch) findViewById(R.id.switch1);
        Switch2 = (Switch) findViewById(R.id.switch2);

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

        Bloodglucose = (EditText) findViewById(R.id.bloodglucose);
        bloodglucose = Bloodglucose.getText().toString();
        Cholestrol = (EditText) findViewById(R.id.cholestrol);
        cholestrol = Cholestrol.getText().toString();


        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter adapter =  new ArrayAdapter(this,android.R.layout.simple_spinner_item,diseases_array);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.save);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Simple Button 1", Toast.LENGTH_LONG).show();//display the text of button1
                //SignUpApiCall();

                diseases = spinner.getSelectedItem().toString();


                //Code to save food data to couchbase
                Application application = (Application) getApplication();
                String username = application.getUsername();
                db = application.getDatabase();

                if (db == null) throw new IllegalArgumentException();
                MutableDocument mDoc = new MutableDocument();
                mDoc.setString("height",Height);
                mDoc.setString("weight",Weight);
                mDoc.setString("bloodglucose",bloodglucose);
                mDoc.setString("cholestrol", cholestrol);
                mDoc.setString("diseases", diseases);
                mDoc.setString("type", "task-list");
                mDoc.setString("owner", username);
                try {
                    db.save(mDoc);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            height + "details are added successfully!", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (CouchbaseLiteException e) {
                    com.couchbase.lite.internal.support.Log.e(TAG, "Failed to save the doc - %s", e, mDoc);
                }
            }
        });





/*
        private void SignUpApiCall() {
            // get & set progressbar dialog
            //ShowDialog();
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            String json = "[{\"User_FirstName\":\"" + et_signup_firstname.getText().toString().trim() + "\"," +
                    "\"User_LastName\":\"" + et_signup_lastname.getText().toString().trim() + "\"," +
                    "\"User_ScreenName\":\"" + et_signup_screenname.getText().toString().trim() + "\"," +
                    "\"User_Email\":\"" + et_signup_email.getText().toString().trim() + "\"," +
                    "\"User_Password\":\"" + et_signup_pswd.getText().toString().trim() + "\"," +
                    "\"User_Latitude\":\"1234561\"," +
                    "\"User_Longitude\":\"1234561\"," +
                    "\"User_Device_Type\":\"android\"," +
                    "\"User_Device_ID\":\"cxczxczxxcczxcxz\"," +
                    "\"api_type\":\"android\"," +
                    "\"api_userid\":\"\"," +
                    "\"Version\":\"1\"}]";
            CustomLog.d("System out", "sign up json___" + json);
            Call<ArrayList<SignUp>> call = apiService.signUp(json);
            call.enqueue(new Callback<ArrayList<SignUp>>() {
                @Override
                public void onResponse(Call<ArrayList<SignUp>> call, Response<ArrayList<SignUp>> response) {
                    if (response.body() != null) {
                        DismissDialog();
                        if (response.body().get(0).getStatus().equalsIgnoreCase("true")) {

                            Constants.snackbar(getActivity(), ll_main, "" + response.body().get(0).getMessage());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1500);
                                        (getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((MainActivity) getActivity()).replace_fragmnet(new LoginFragment());
                                            }
                                        });
                                    } catch (Exception e) {
                                        CustomLog.w("Exception in thread", ""+e);
                                    }

                                }
                            }).start();

                        } else {
                            //Constants.snackbar(getActivity(), ll_main, "" + response.body().get(0).getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<SignUp>> call, Throwable t) {
                    //Constants.snackbar(getActivity(), ll_main, "" + getResources().getString(R.string.server_error));

                }
            });

        }

*/

    }
}
