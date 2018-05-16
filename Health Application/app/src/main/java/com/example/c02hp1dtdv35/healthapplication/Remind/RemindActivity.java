package com.example.c02hp1dtdv35.healthapplication.Remind;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.c02hp1dtdv35.healthapplication.R;

public class RemindActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        getSupportActionBar().setTitle("Reminder");
    }
}
