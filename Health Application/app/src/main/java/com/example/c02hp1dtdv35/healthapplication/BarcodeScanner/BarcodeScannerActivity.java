package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.c02hp1dtdv35.healthapplication.R;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;

public class BarcodeScannerActivity extends AppCompatActivity implements View.OnClickListener{
    private Button scanBtn;
    private RequestQueue requestQueue;

    String baseUrl = "https://world.openfoodfacts.org/api/v0/product/";
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        getSupportActionBar().setTitle("Barcode Scanner");
        scanBtn =  findViewById(R.id.scan_button);
        requestQueue = Volley.newRequestQueue(this);  // This setups up a new request queue which we will need to make HTTP requests.
        scanBtn.setOnClickListener(this);

    }


    private void getData(String barcode) {
        this.url = this.baseUrl + barcode + ".json";

        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check the length of our response
                        if (response.length() > 0) {

                            try {
                                Gson gson = new Gson();
                                ProductFullObject productFullObject =  gson.fromJson(response.toString(), ProductFullObject.class);
                                Product product = productFullObject.getProduct();
                                Nutriments nutriments = product.getNutriments();
                                NutrientLevels nutrientLevels = product.getNutrientLevels();
                                Intent myIntent = new Intent(getApplicationContext(),LogFood.class);
                                //Create the bundle
                                Bundle bundle = new Bundle();

                                //Add your data to bundle
                                bundle.putString("product_name", product.getProductName());
                                bundle.putString("product_image", product.getImageSmallUrl());
                                bundle.putString("serving_size", product.getServingSize());
                                bundle.putString("calories", nutriments.getEnergyValue());
                                bundle.putString("allergens", product.getAllergens());

                                //Add the bundle to the intent
                                myIntent.putExtra("nutriments", new Gson().toJson(nutriments));
                                myIntent.putExtra("nutrientLevels", new Gson().toJson(nutrientLevels));
                                myIntent.putExtras(bundle);
                                startActivity(myIntent);
                            } catch (Exception e) {
                                // If there is an error then output this to the logs.
                                Log.e("Volley", "Invalid JSON Object.");
                            }
                        } else {
                            Toast.makeText(BarcodeScannerActivity.this,"NO Data Found",Toast.LENGTH_SHORT).show();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                         Log.e("Volley", error.toString());
                    }
                }
        );
        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);
    }

    public void onClick (View v){
        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            getData(scanContent);


        }else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
