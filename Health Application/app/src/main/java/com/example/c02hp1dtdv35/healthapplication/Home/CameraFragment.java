package com.example.c02hp1dtdv35.healthapplication.Home;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.LogFood;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.NutrientLevels;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Nutriments;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Product;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.ProductVO;
import com.example.c02hp1dtdv35.healthapplication.R;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by C02HP1DTDV35 on 5/12/18.
 */

public class CameraFragment extends Fragment {
    final int REQUEST_CAMERA_CODE=1;
    private Button btnCamera,btnBarcode;
    private TextView txtContent;
    private RequestQueue requestQueue;
    private IntentIntegrator qrScan;
    private Intent myIntent;

    String baseUrl = "https://world.openfoodfacts.org/api/v0/product/";
    String url;
    public CameraFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_camera_copy, container, false);
        btnCamera=view.findViewById(R.id.btn_capture_image);
        btnBarcode=view.findViewById(R.id.btn_scan_barcode);
        txtContent =view.findViewById(R.id.txtContent);

        qrScan = new IntentIntegrator(getActivity());
        onClick();
        return view;

    }

    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA_CODE);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void onClick(){
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent= new Intent(getActivity(),BarcodeScannerActivity.class);
//                startActivity(intent);
//
                Toast.makeText(getActivity(),"Barcode CLicked", Toast.LENGTH_SHORT).show();
                qrScan.initiateScan();


            }
        });

    }

    private void setContentTxt(String str) {
        this.txtContent.setText("Product: " + str);
    }
    private void getData(String barcode) {
        // First, we insert the username into the repo url.
        this.url = this.baseUrl + barcode + ".json";



        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check the length of our response (to see if the user has any repos)
                        if (response.length() > 0) {

                            try {
                                // String code = response.get("code").toString();
//                                Product product = (Product)response.toString();
                                Gson gson = new Gson();
                                ProductVO productVO =  gson.fromJson(response.toString(), ProductVO.class);
                                Product product = productVO.getProduct();
                                Nutriments nutriments = product.getNutriments();
                                NutrientLevels nutrientLevels = product.getNutrientLevels();

                                //Create the bundle
                                Bundle bundle = new Bundle();

                                //Add your data to bundle
                                bundle.putString("product_name", product.getProductName());
                                bundle.putString("product_image", product.getImageSmallUrl());
                                bundle.putString("serving_size", product.getServingSize());
                                bundle.putString("calories", nutriments.getEnergyValue());
                                bundle.putString("allergens", product.getAllergens());

                                myIntent = new Intent(getActivity(),LogFood.class);
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
                            setContentTxt("No data found.");
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        setContentTxt("Error while calling REST API");
                        Log.e("Volley", error.toString());
                    }
                }
        );
        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        Log.d("Volley", "Invalid JSON Object.");
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            getData(scanContent);


        }else{
            Toast toast = Toast.makeText(getActivity(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
