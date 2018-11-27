package com.example.c02hp1dtdv35.healthapplication.Remind;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.c02hp1dtdv35.healthapplication.Application;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.LogFood;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Product;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Products;
import com.example.c02hp1dtdv35.healthapplication.Classifier;
import com.example.c02hp1dtdv35.healthapplication.ImageUtils;
import com.example.c02hp1dtdv35.healthapplication.R;
import com.example.c02hp1dtdv35.healthapplication.TensorFlowObjectDetectionAPIModel;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RemindActivity extends AppCompatActivity {

    private enum DetectorMode {
        TF_OD_API, MULTIBOX, YOLO;
    }
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;

    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.6f;

    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final String TF_OD_API_MODEL_FILE = "file:///android_asset/frozen_inference_graph.pb";
    //private static final String TF_OD_API_MODEL_FILE = "file:///android_asset/saved_model.pb";
    //"file:///android_asset/ssd_mobilenet_v1_android_export.pb";
//  private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/livefit_labels_list.txt";

    private static final boolean MAINTAIN_ASPECT = MODE == DetectorMode.YOLO;

    private Classifier detector;
    private int[] rgbBytes = null;

    private Matrix cropToFrameTransform;
    private Matrix frameToCropTransform;

    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private int[] intValues ;
    private byte[] byteValues;

    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    protected int previewWidth = 0;
    protected int previewHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        getSupportActionBar().setTitle("Reminder");


//        Button btn = findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ImageView myImageView = findViewById(R.id.imgview);
//                Bitmap myBitmap = BitmapFactory.decodeResource(
//                        getApplicationContext().getResources(),
//                        R.drawable.twoapples);
//                myImageView.setImageBitmap(myBitmap);
//            }
//        });

        int cropSize = TF_OD_API_INPUT_SIZE;


        try {
            detector = TensorFlowObjectDetectionAPIModel.create(
                    getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
           // LOGGER.e("Exception initializing classifier!", e);
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        Bitmap myBitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.drawable.twoapples);

        previewWidth = myBitmap.getWidth();
        previewHeight = myBitmap.getHeight();

        ByteArrayOutputStream bytes1 = new ByteArrayOutputStream();

        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes1);

        byte[] byteArray = bytes1.toByteArray();

        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);


        Bitmap cropped = Bitmap.createBitmap(myBitmap,0,0,cropSize,cropSize);

        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);

        int sensorOrientation = 0;

        if (rgbBytes == null) {
            rgbBytes = new int[previewWidth * previewHeight];
        }

        final int lnth=myBitmap.getByteCount();
        ByteBuffer dst= ByteBuffer.allocate(lnth);
        myBitmap.copyPixelsToBuffer( dst);
        byte[] bytes=dst.array();

        intValues = new int[previewWidth * previewHeight];

        myBitmap.getPixels(intValues, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());

        int val = intValues.length;

        byteValues = new byte[previewWidth * previewHeight * 3];

        for (int i = 0; i < intValues.length; ++i) {
            byteValues[i * 3 + 2] = (byte) (intValues[i] & 0xFF);
            byteValues[i * 3 + 1] = (byte) ((intValues[i] >> 8) & 0xFF);
            byteValues[i * 3 + 0] = (byte) ((intValues[i] >> 16) & 0xFF);
        }

    //    Bitmap bitmap = new BitmapFactory().decodeByteArray(bytes, 0/* starting index*/, bytes.length/*no of byte to read*/);

        ImageUtils.convertYUV420SPToARGB8888(byteValues, previewWidth, previewHeight, rgbBytes);

        //ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);

        rgbFrameBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

        final List<Classifier.Recognition> results = detector.recognizeImage(cropped);
//        LOGGER.d("results", results);

       // lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
        Bitmap cropCopyBitmap = Bitmap.createBitmap(cropped);
        final Canvas canvas1 = new Canvas(cropCopyBitmap);
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


        Application application = (Application) getApplication();

        List<Product> prods = new ArrayList<>();

        Map<String, Product> mProds = application.getProducts();

        final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();

        for (final Classifier.Recognition result : results) {
            final RectF location = result.getLocation();
            if (location != null && result.getConfidence() >= minimumConfidence) {
                canvas1.drawRect(location, paint);

                cropToFrameTransform.mapRect(location);
                result.setLocation(location);
                mappedRecognitions.add(result);

                if(mProds.containsKey("Apple"))
                {
                    prods.add(mProds.get("Apple"));
                    prods.add(mProds.get("Apple"));
                }
            }
        }
        System.out.println();

        Products products = new Products();
        products.setProductList(prods);

        Intent myIntent = new Intent(getApplicationContext(),LogFood.class);
        myIntent.putExtra("productsCamera",new Gson().toJson(products));
        myIntent.putExtra("products",new Gson().toJson(mProds.get("Apple")));
        myIntent.putExtra("type","camera");
        startActivity(myIntent);
    }
}
