/*
 * Copyright 2016 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.c02hp1dtdv35.healthapplication.Home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.example.c02hp1dtdv35.healthapplication.Application;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.LogFood;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Product;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Products;
import com.example.c02hp1dtdv35.healthapplication.Classifier;
import com.example.c02hp1dtdv35.healthapplication.Home.OverlayView.DrawCallback;
import com.example.c02hp1dtdv35.healthapplication.Home.env.BorderedText;
import com.example.c02hp1dtdv35.healthapplication.Home.env.ImageUtils;
import com.example.c02hp1dtdv35.healthapplication.Home.env.Logger;
import com.example.c02hp1dtdv35.healthapplication.Home.tracking.MultiBoxTracker;
import com.example.c02hp1dtdv35.healthapplication.R; // Explicit import needed for internal Google builds.
import com.example.c02hp1dtdv35.healthapplication.TensorFlowObjectDetectionAPIModel;
import com.google.gson.Gson;


public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();


  private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final String TF_OD_API_MODEL_FILE = "file:///android_asset/food_inference_graph.pb";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/livefit_labels_list.txt";

  private enum DetectorMode {
    TF_OD_API, MULTIBOX, YOLO;
  }
  private static final DetectorMode MODE = DetectorMode.TF_OD_API;

  private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.8f;
  private static final float MINIMUM_CONFIDENCE_MULTIBOX = 0.1f;
  private static final float MINIMUM_CONFIDENCE_YOLO = 0.25f;

  private static final boolean MAINTAIN_ASPECT = MODE == DetectorMode.YOLO;

  private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);

  private static final boolean SAVE_PREVIEW_BITMAP = false;
  private static final float TEXT_SIZE_DIP = 10;

  private Integer sensorOrientation;

  private Classifier detector;

  private long lastProcessingTimeMs;
  private Bitmap rgbFrameBitmap = null;
  private Bitmap croppedBitmap = null;
  private Bitmap cropCopyBitmap = null;

  private boolean computingDetection = false;

  private long timestamp = 0;

  private Matrix frameToCropTransform;
  private Matrix cropToFrameTransform;

  private MultiBoxTracker tracker;

  private byte[] luminanceCopy;

  private BorderedText borderedText;
  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {
    final float textSizePx =
            TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    tracker = new MultiBoxTracker(this);

    int cropSize = TF_OD_API_INPUT_SIZE;

    try {
        detector = TensorFlowObjectDetectionAPIModel.create(
                getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE);
        cropSize = TF_OD_API_INPUT_SIZE;
      } catch (final IOException e) {
        LOGGER.e("Exception initializing classifier!", e);
        Toast toast =
                Toast.makeText(
                        getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
        toast.show();
        finish();
      }

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();
    LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
    croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

    frameToCropTransform =
            ImageUtils.getTransformationMatrix(
                    previewWidth, previewHeight,
                    cropSize, cropSize,
                    sensorOrientation, MAINTAIN_ASPECT);

    cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);

    trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
    trackingOverlay.addCallback(
            new DrawCallback() {
              @Override
              public void drawCallback(final Canvas canvas) {
                tracker.draw(canvas);
                if (isDebug()) {
                  tracker.drawDebug(canvas);
                }
              }
            });

    addCallback(
            new DrawCallback() {
              @Override
              public void drawCallback(final Canvas canvas) {
                if (!isDebug()) {
                  return;
                }
                final Bitmap copy = cropCopyBitmap;
                if (copy == null) {
                  return;
                }

                final int backgroundColor = Color.argb(100, 0, 0, 0);
                canvas.drawColor(backgroundColor);

                final Matrix matrix = new Matrix();
                final float scaleFactor = 2;
                matrix.postScale(scaleFactor, scaleFactor);
                matrix.postTranslate(
                        canvas.getWidth() - copy.getWidth() * scaleFactor,
                        canvas.getHeight() - copy.getHeight() * scaleFactor);
                canvas.drawBitmap(copy, matrix, new Paint());

                final Vector<String> lines = new Vector<String>();
                if (detector != null) {
                  final String statString = detector.getStatString();
                  final String[] statLines = statString.split("\n");
                  for (final String line : statLines) {
                    lines.add(line);
                  }
                }
                lines.add("");

                lines.add("Frame: " + previewWidth + "x" + previewHeight);
                lines.add("Crop: " + copy.getWidth() + "x" + copy.getHeight());
                lines.add("View: " + canvas.getWidth() + "x" + canvas.getHeight());
                lines.add("Rotation: " + sensorOrientation);
                lines.add("Inference time: " + lastProcessingTimeMs + "ms");

                borderedText.drawLines(canvas, 10, canvas.getHeight() - 10, lines);
              }
            });
  }

  OverlayView trackingOverlay;

    public void clickNew(View v)
    {
        //Toast.makeText(this, "Show some text on the screen.", Toast.LENGTH_LONG).show();
        Application application = (Application) getApplication();

        List<Product> prods = new ArrayList<>();

        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
        switch (MODE) {
            case TF_OD_API:
                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                break;
        }
        Map<String, Product> mProds = application.getProducts();
        for (final Classifier.Recognition result : results) {
            if (result.getConfidence() >= minimumConfidence) {
//                canvas1.drawRect(location, paint);

                System.out.println(result.getTitle() + " " + result.getConfidence());

                if(mProds.containsKey(result.getTitle()))
                {
                    prods.add(mProds.get(result.getTitle()));
                }
            }
        }




        Products products = new Products();
        products.setProductList(prods);

        Intent myIntent = new Intent(getApplicationContext(),LogFood.class);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        myIntent.putExtra("product_image", byteArray);
        myIntent.putExtra("productsCamera",new Gson().toJson(products));
        myIntent.putExtra("products",new Gson().toJson(mProds.get("Apple")));
        myIntent.putExtra("type","camera");
        startActivity(myIntent);
    }

     List<Classifier.Recognition> results = null;

  @Override
  protected void processImage() {
    ++timestamp;
    final long currTimestamp = timestamp;
    byte[] originalLuminance = getLuminance();
    tracker.onFrame(
            previewWidth,
            previewHeight,
            getLuminanceStride(),
            sensorOrientation,
            originalLuminance,
            timestamp);
    trackingOverlay.postInvalidate();

    // No mutex needed as this method is not reentrant.
    if (computingDetection) {
      readyForNextImage();
      return;
    }
    computingDetection = true;
    LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

    if (luminanceCopy == null) {
      luminanceCopy = new byte[originalLuminance.length];
    }
    System.arraycopy(originalLuminance, 0, luminanceCopy, 0, originalLuminance.length);
    readyForNextImage();

    final Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

    results = detector.recognizeImage(croppedBitmap);

    float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
    switch (MODE) {
      case TF_OD_API:
        minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
        break;
      case MULTIBOX:
        minimumConfidence = MINIMUM_CONFIDENCE_MULTIBOX;
        break;
      case YOLO:
        minimumConfidence = MINIMUM_CONFIDENCE_YOLO;
        break;
    }

    final List<Classifier.Recognition> mappedRecognitions =
            new LinkedList<Classifier.Recognition>();

    for (final Classifier.Recognition result : results) {
      final RectF location = result.getLocation();
      if (location != null && result.getConfidence() >= minimumConfidence) {
       // canvas.drawRect(location, paint);

        cropToFrameTransform.mapRect(location);
        result.setLocation(location);
        mappedRecognitions.add(result);
      }
    }


    // For examining the actual TF input.
    if (SAVE_PREVIEW_BITMAP) {
      ImageUtils.saveBitmap(croppedBitmap);
    }

    runInBackground(
            new Runnable() {
              @Override
              public void run() {
                LOGGER.i("Running detection on image " + currTimestamp);
                final long startTime = SystemClock.uptimeMillis();
                final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                final Canvas canvas = new Canvas(cropCopyBitmap);
                final Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(2.0f);

                float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                switch (MODE) {
                  case TF_OD_API:
                    minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                    break;
                  case MULTIBOX:
                    minimumConfidence = MINIMUM_CONFIDENCE_MULTIBOX;
                    break;
                  case YOLO:
                    minimumConfidence = MINIMUM_CONFIDENCE_YOLO;
                    break;
                }

                final List<Classifier.Recognition> mappedRecognitions =
                        new LinkedList<Classifier.Recognition>();

                for (final Classifier.Recognition result : results) {
                  final RectF location = result.getLocation();
                  if ((result.getTitle().equals("apple") || result.getTitle().equals("orange") || result.getTitle().equals("banana"))
                          && location != null && result.getConfidence() >= minimumConfidence) {
                    canvas.drawRect(location, paint);

                    cropToFrameTransform.mapRect(location);
                    result.setLocation(location);
                    mappedRecognitions.add(result);
                  }
                }

                tracker.trackResults(mappedRecognitions, luminanceCopy, currTimestamp);
                trackingOverlay.postInvalidate();

                requestRender();
                computingDetection = false;
              }
            });
  }

  @Override
  protected int getLayoutId() {
    return R.layout.camera_connection_fragment_tracking;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  @Override
  public void onSetDebug(final boolean debug) {
    detector.enableStatLogging(debug);
  }
}