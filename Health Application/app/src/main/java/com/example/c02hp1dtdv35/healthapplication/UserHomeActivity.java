package com.example.c02hp1dtdv35.healthapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.BarcodeScannerActivity;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.LogFood;
import com.example.c02hp1dtdv35.healthapplication.Home.CameraActivity;
import com.example.c02hp1dtdv35.healthapplication.Home.DashboardActivity;
import com.example.c02hp1dtdv35.healthapplication.Home.DetectorActivity;
import com.example.c02hp1dtdv35.healthapplication.LoggedFoodDisplay.ShowLoggedFoodActivity;
import com.example.c02hp1dtdv35.healthapplication.Login.UserProfileActivity;
import com.example.c02hp1dtdv35.healthapplication.R;
import com.example.c02hp1dtdv35.healthapplication.Remind.ReminderActivity;

public class UserHomeActivity extends AppCompatActivity {

    // UI references.
    private ImageView userProfile;
    private CardView dashboard;
    private CardView foodLog;
    private CardView camera;
    private CardView barcode;
    private CardView reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        userProfile = findViewById(R.id.userProfile);
        dashboard = findViewById(R.id.dashboard);
        foodLog = findViewById(R.id.foodLog);
        camera = findViewById(R.id.camera);
        barcode = findViewById(R.id.barcode);
        reminder = findViewById(R.id.reminder);

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        foodLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, ShowLoggedFoodActivity.class);
                startActivity(intent);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, DetectorActivity.class);
                startActivity(intent);
            }
        });

        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, BarcodeScannerActivity.class);
                startActivity(intent);
            }
        });

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, ReminderActivity.class);
                startActivity(intent);
            }
        });

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) { ;
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
