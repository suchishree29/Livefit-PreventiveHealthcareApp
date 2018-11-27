package com.example.c02hp1dtdv35.healthapplication.Home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
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
import com.android.volley.toolbox.Volley;
import com.example.c02hp1dtdv35.healthapplication.Application;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.LogFood;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.NutrientLevels;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Nutriments;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Product;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.ProductVO;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Products;
import com.example.c02hp1dtdv35.healthapplication.Classifier;
import com.example.c02hp1dtdv35.healthapplication.R;
import com.example.c02hp1dtdv35.healthapplication.TensorFlowObjectDetectionAPIModel;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private int REQUEST_CAMERA = 0;

    private enum DetectorMode {
        TF_OD_API, MULTIBOX, YOLO;
    }
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;

    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.6f;

    private static  int TF_OD_API_INPUT_SIZE = 768;
    private static final String TF_OD_API_MODEL_FILE = "file:///android_asset/frozen_inference_graph.pb";
    //private static final String TF_OD_API_MODEL_FILE = "file:///android_asset/saved_model.pb";
    //"file:///android_asset/ssd_mobilenet_v1_android_export.pb";
//  private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/livefit_labels_list.txt";

    private static final boolean MAINTAIN_ASPECT = MODE == DetectorMode.YOLO;

    private Classifier detector;

    private Matrix cropToFrameTransform;
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
        requestQueue = Volley.newRequestQueue(getActivity());
        qrScan = new IntentIntegrator(getActivity());
        onClick();
        //getActivity().getAssets();
        return view;

    }

    public void openCamera() {
//        Intent takePictureIntent = new Intent(getActivity(), CameraActivity.class);
//        startActivity(takePictureIntent);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(getActivity(),
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_CAMERA_CODE);
//            }
//        }
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
//startActivityForResult(createScanIntent(), REQUEST_CODE);
                //IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
                onClickBarCodeScanner();
//                Toast.makeText(getActivity(),"Barcode CLicked", Toast.LENGTH_SHORT).show();
//                qrScan.initiateScan();


            }
        });

    }

    private void onClickBarCodeScanner() {
        IntentIntegrator.forSupportFragment(this).initiateScan();
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


                                myIntent = new Intent(getActivity(),LogFood.class);
                                //Add the bundle to the intent
                                Bundle bundle = new Bundle();

                                //Add your product image data to bundle
                                bundle.putString("product_image", product.getImageSmallUrl());

                                //Add the bundle to the intent
                                myIntent.putExtra("products",new Gson().toJson(product));
                                myIntent.putExtra("nutriments", new Gson().toJson(nutriments));
                                myIntent.putExtra("nutrientLevels", new Gson().toJson(nutrientLevels));
                                myIntent.putExtra("type","barcode");
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        } if(requestCode == 49374)
            onScannerOutput(requestCode, resultCode, data);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        TF_OD_API_INPUT_SIZE = thumbnail.getHeight();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int cropSize = TF_OD_API_INPUT_SIZE;


        try {
            detector = TensorFlowObjectDetectionAPIModel.create(
                    getActivity().getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            // LOGGER.e("Exception initializing classifier!", e);
            Toast toast =
                    Toast.makeText(
                            getActivity().getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            getActivity().finish();
        }

        Bitmap myBitmap = BitmapFactory.decodeResource(
                getActivity().getApplicationContext().getResources(),
                R.drawable.twoapples);

        Bitmap cropped = Bitmap.createBitmap(myBitmap,0,0,cropSize,cropSize);


        final List<Classifier.Recognition> results = detector.recognizeImage(thumbnail);
//        LOGGER.d("results", results);

        // lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

        cropToFrameTransform = new Matrix();

        Bitmap cropCopyBitmap = Bitmap.createBitmap(thumbnail);
//        final Canvas canvas1 = new Canvas(cropCopyBitmap);
        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);

        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
        switch (MODE) {
            case TF_OD_API:
                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                break;
        }

        final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();

        Application application = (Application) getActivity().getApplication();

        List<Product> prods = new ArrayList<>();


        Map<String, Product> mProds = application.getProducts();

        for (final Classifier.Recognition result : results) {
            final RectF location = result.getLocation();
            if (location != null && result.getConfidence() >= minimumConfidence) {
//                canvas1.drawRect(location, paint);

                System.out.println(result.getTitle() + " " + result.getConfidence());

                cropToFrameTransform.mapRect(location);
                result.setLocation(location);
                mappedRecognitions.add(result);
                if(mProds.containsKey("Apple"))
                {
                    prods.add(mProds.get(result.getTitle()));
                }
            }
        }

        System.out.println();

        Products products = new Products();
        products.setProductList(prods);

        Intent myIntent = new Intent(getActivity(),LogFood.class);
        myIntent.putExtra("productsCamera",new Gson().toJson(products));
        myIntent.putExtra("products",new Gson().toJson(mProds.get("Apple")));
        myIntent.putExtra("type","camera");
        startActivity(myIntent);
    }

    public void onScannerOutput(int requestCode, int resultCode, Intent data) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            getData(scanContent);


        }else{
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
