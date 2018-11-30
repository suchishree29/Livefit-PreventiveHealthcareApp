package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.c02hp1dtdv35.healthapplication.Application;

import com.example.c02hp1dtdv35.healthapplication.R;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogFood extends AppCompatActivity {

    private ImageView prodImg;
    private TextView dateTxt;
    private Button logBtn;
    private Spinner spinner;
    Product productLog;
    private Database db;
    String date,imgUrl,selectedMealCourse;

    int year,day,month;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = LogFood.class.getSimpleName();
    final ArrayList<Product> products = new ArrayList<>();
    private Query query;
    DailyValues dailyData;
    Double totalCalories =0.0, totalSugar=0.0,totalFat=0.0, totalProtein=0.0,totalSalt=0.0;
    Double dailyCalories =0.0, dailySugar=0.0,dailyFat=0.0, dailyProtein=0.0,dailySalt=0.0;

    ObjectMapper objectMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_food);
        getSupportActionBar().setTitle("Log Food");
        prodImg = findViewById(R.id.product_image);
        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        logBtn = findViewById(R.id.logBtn);

        // Get Nutriments Object
        String productJson = "";
        Bundle extras = getIntent().getExtras();
        List<Product> productsFromCamera;
        String type ;
        if (extras != null) {
            productJson = extras.getString("products");
            type = extras.getString("type");

            if(type.equals("camera"))
            {

                byte[] byteArray = getIntent().getByteArrayExtra("product_image");
                if(byteArray != null) {
                    final Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    if(bmp != null) {
                        prodImg.setImageBitmap(bmp);

                        uploadBitmap(bmp);
                    }
                }
                productJson = extras.getString("productsCamera");
                Products ps = new Gson().fromJson(productJson, Products.class);
                products.addAll(ps.getProductList());
            }
            else
            {
                //Get the bundle
                Bundle bundle = getIntent().getExtras();
                imgUrl = bundle.getString("product_image");
                Picasso.get().load(imgUrl).into(prodImg);

                productJson = extras.getString("products");
                productLog = new Gson().fromJson(productJson, Product.class);

                if(productLog.getNutriments().getEnergyValue() == null )
                {
                    productLog.getNutriments().setEnergyValue("0");
                }

                if(productLog.getNutriments().getFat() == null )
                {
                    productLog.getNutriments().setFat("0");
                }
                if(productLog.getNutriments().getProteins() == null )
                {
                    productLog.getNutriments().setProteins("0");
                }
                if(productLog.getNutriments().getSugars() == null )
                {
                    productLog.getNutriments().setSugars("0");
                }

                if(productLog.getNutriments().getSalt() == null )
                {
                    productLog.getNutriments().setSalt("0");
                }

                products.add(productLog);
            }
        }


//        Code for populating Recycler View
//         Initialize products




        // Create adapter passing in the products data
        ProductsAdapter adapter = new ProductsAdapter(products);

        // Attach the adapter to the recyclerview to populate items
        rvProducts.setAdapter(adapter);
        // Set layout manager to position the items
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        //Code for Meals dropdown
        spinner = findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.meals_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);

        // Code for Date Picker
        dateTxt = findViewById(R.id.dayTxt);
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        date = (month+1) +"/" + day + "/" + year;
        dateTxt.setText("Day : " + date);


        Application application = (Application) getApplication();
        final String username = application.getUsername();
        db = application.getDatabase();

        if (db == null) throw new IllegalArgumentException();

        query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(db))
                .where(Expression.property("type").equalTo(Expression.string("daily-data"))
                        .and(Expression.property("date").equalTo(Expression.string(date)))
                        .and(Expression.property("owner").equalTo(Expression.string(username))));
        try {
            ResultSet rs = query.execute();

            Result row;

            while ((row = rs.next()) != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                // Ignore undeclared properties
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                Dictionary valueMap = row.getDictionary(db.getName());

                dailyData = objectMapper.convertValue(valueMap.toMap(),DailyValues.class);
                totalCalories += dailyData.getTotalCalories();
                totalFat+= dailyData.getTotalFat();
                totalProtein += dailyData.getTotalProtein();
                totalSugar += dailyData.getTotalSugar();
                totalSalt += dailyData.getTotalSalt();

            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }


        dateTxt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                DatePickerDialog dialog = new DatePickerDialog(LogFood.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG,"onDateSet: Day: "+ month +"/" + day + "/" + year);

                date = month +"/" + day + "/" + year;
                dateTxt.setText("Day : " + date);

                totalCalories =0.0;
                totalSugar=0.0;
                totalFat=0.0;
                totalProtein=0.0;
                totalSalt=0.0;

                query = QueryBuilder.select(SelectResult.all())
                        .from(DataSource.database(db))
                        .where(Expression.property("type").equalTo(Expression.string("daily-data"))
                                .and(Expression.property("date").equalTo(Expression.string(date)))
                                .and(Expression.property("owner").equalTo(Expression.string(username))));
                try {
                    ResultSet rs = query.execute();

                    Result row;

                    while ((row = rs.next()) != null) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        // Ignore undeclared properties
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        Dictionary valueMap = row.getDictionary(db.getName());

                        dailyData = objectMapper.convertValue(valueMap.toMap(),DailyValues.class);

                        totalCalories += dailyData.getTotalCalories();
                        totalFat+= dailyData.getTotalFat();
                        totalProtein += dailyData.getTotalProtein();
                        totalSugar += dailyData.getTotalSugar();
                        totalSalt += dailyData.getTotalSalt();


                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };

        logBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                selectedMealCourse = spinner.getSelectedItem().toString();

                for(Product product: products){

                    product.setMeal_course(selectedMealCourse);
                    product.setType("task-list");
                    product.setImageSmallUrl(imgUrl);
                    product.setMeal_date(date);
                    product.setOwner(username);

                    String en = product.getNutriments().getEnergyValue();

                    dailyCalories+= Double.valueOf(en);
                    dailyFat+= Double.valueOf(product.getNutriments().getFat());
                    dailyProtein += Double.valueOf(product.getNutriments().getProteins());
                    dailySugar += Double.valueOf(product.getNutriments().getSugars());
                    dailySalt += Double.valueOf(product.getNutriments().getSalt());

                    totalCalories += dailyCalories;
                    totalFat+= dailyFat;
                    totalProtein += dailyProtein;
                    totalSugar += dailySugar;
                    totalSalt += dailySalt;

                }

                if(totalCalories > 600){
                    AlertDialog alertDialog = new AlertDialog.Builder(LogFood.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Total calories for the day has exceeded 600 limit. Do you still want to log this item?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    for (Product product : products) {
                                        objectMapper = new ObjectMapper();
                                        // Ignore undeclared properties
                                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                                        HashMap<String, Object> productMap = objectMapper.convertValue(product, HashMap.class);

                                        MutableDocument mDoc = new MutableDocument(productMap);

                                        try {
                                            db.save(mDoc);
                                            Toast toast = Toast.makeText(getApplicationContext(),
                                                    product + "product is logged successfully!", Toast.LENGTH_SHORT);
                                            toast.show();
                                        } catch (CouchbaseLiteException e) {
                                            com.couchbase.lite.internal.support.Log.e(TAG, "Failed to save the doc - %s", e, mDoc);
                                        }

                                    }

                                    DailyValues logDailyValue = new DailyValues();
                                    if (dailyData == null)
                                        dailyData = new DailyValues();

                                    logDailyValue.setDate(date);
                                    logDailyValue.setId(date);
                                    logDailyValue.setTotalCalories(dailyCalories);
                                    logDailyValue.setTotalFat(dailyFat);
                                    logDailyValue.setTotalProtein(dailyProtein);
                                    logDailyValue.setTotalSugar(dailySugar);
                                    logDailyValue.setTotalSalt(dailySalt);
                                    logDailyValue.setType("daily-data");
                                    logDailyValue.setOwner(username);

                                    objectMapper = new ObjectMapper();

                                    HashMap<String, Object> dv = objectMapper.convertValue(logDailyValue, HashMap.class);
                                    MutableDocument nDV = new MutableDocument(dv);
                                    try {
                                        db.save(nDV);
                                    } catch (CouchbaseLiteException e) {
                                        com.couchbase.lite.internal.support.Log.e(TAG, "Failed to save the doc - %s", e, nDV);
                                    }
                                    dialog.dismiss();


                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //pressed = false;

                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else
                {
                    for (Product product : products) {
                        objectMapper = new ObjectMapper();
                        // Ignore undeclared properties
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        HashMap<String, Object> productMap = objectMapper.convertValue(product, HashMap.class);

                        MutableDocument mDoc = new MutableDocument(productMap);

                        try {
                            db.save(mDoc);
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    product + "product is logged successfully!", Toast.LENGTH_SHORT);
                            toast.show();
                        } catch (CouchbaseLiteException e) {
                            com.couchbase.lite.internal.support.Log.e(TAG, "Failed to save the doc - %s", e, mDoc);
                        }

                    }

                    DailyValues logDailyValue = new DailyValues();
                    if (dailyData == null)
                        dailyData = new DailyValues();

                    logDailyValue.setDate(date);
                    logDailyValue.setId(date);
                    logDailyValue.setTotalCalories(dailyCalories);
                    logDailyValue.setTotalFat(dailyFat);
                    logDailyValue.setTotalProtein(dailyProtein);
                    logDailyValue.setTotalSugar(dailySugar);
                    logDailyValue.setTotalSalt(dailySalt);
                    logDailyValue.setType("daily-data");
                    logDailyValue.setOwner(username);

                    objectMapper = new ObjectMapper();

                    HashMap<String, Object> dv = objectMapper.convertValue(logDailyValue, HashMap.class);
                    MutableDocument nDV = new MutableDocument(dv);
                    try {
                        db.save(nDV);
                    } catch (CouchbaseLiteException e) {
                        com.couchbase.lite.internal.support.Log.e(TAG, "Failed to save the doc - %s", e, nDV);
                    }
                }

            }
        });


    }

    /*
     * The method is taking Bitmap as an argument
     * then it will return the byte[] array for the given bitmap
     * and we will send this array to the server
     * here we are using PNG Compression with 80% quality
     * you can give quality between 0 to 100
     * 0 means worse quality
     * 100 means best quality
     * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap) {


        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //params.put("tags", tags);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

}