package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.c02hp1dtdv35.healthapplication.R;

public class BarcodeScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        getSupportActionBar().setTitle("Barcode Scanner");


    }
}
