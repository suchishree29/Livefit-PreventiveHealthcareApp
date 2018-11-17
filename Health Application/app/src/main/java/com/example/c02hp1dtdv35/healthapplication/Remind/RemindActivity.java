package com.example.c02hp1dtdv35.healthapplication.Remind;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.c02hp1dtdv35.healthapplication.Classifier;
import com.example.c02hp1dtdv35.healthapplication.R;
import com.example.c02hp1dtdv35.healthapplication.TensorFlowObjectDetectionAPIModel;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class RemindActivity extends AppCompatActivity {

    private enum DetectorMode {
        TF_OD_API, MULTIBOX, YOLO;
    }
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;

    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.6f;

    private static final int TF_OD_API_INPUT_SIZE = 768;
    private static final String TF_OD_API_MODEL_FILE = "file:///android_asset/frozen_inference_graph.pb";
    //private static final String TF_OD_API_MODEL_FILE = "file:///android_asset/saved_model.pb";
    //"file:///android_asset/ssd_mobilenet_v1_android_export.pb";
//  private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/livefit_labels_list.txt";

    private static final boolean MAINTAIN_ASPECT = MODE == DetectorMode.YOLO;

    private Classifier detector;

    private Matrix cropToFrameTransform;

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

        Bitmap cropped = Bitmap.createBitmap(myBitmap,0,0,cropSize,cropSize);


        final List<Classifier.Recognition> results = detector.recognizeImage(cropped);
//        LOGGER.d("results", results);

       // lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

        cropToFrameTransform = new Matrix();

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

        final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();

        for (final Classifier.Recognition result : results) {
            final RectF location = result.getLocation();
            if (location != null && result.getConfidence() >= minimumConfidence) {
                canvas1.drawRect(location, paint);

                cropToFrameTransform.mapRect(location);
                result.setLocation(location);
                mappedRecognitions.add(result);
            }
        }
        System.out.println();
    }
}
