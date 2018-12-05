package com.example.c02hp1dtdv35.healthapplication;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.widget.Toast;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Endpoint;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.lite.URLEndpoint;
import com.couchbase.lite.internal.support.Log;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.DailyValues;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Product;
import com.example.c02hp1dtdv35.healthapplication.Home.WatsonScreen;
import com.example.c02hp1dtdv35.healthapplication.Login.LoginActivity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/*
public interface ReplicatorChangeListener {
    void changed(ReplicatorChange change);
}
 */
public class Application extends android.app.Application implements ReplicatorChangeListener, IDataFetchResponse {

    private static final String TAG = Application.class.getSimpleName();

    private final static boolean LOGIN_FLOW_ENABLED = true;
    private final static boolean SYNC_ENABLED = true;

    private static final String TF_API_MODEL_FILE = "file:///android_asset/frozen_inference_graph.pb";
    //    private static final String TF_OD_API_MODEL_FILE = "file:///android_asset/ssd_mobilenet_v1_android_export.pb";
//  private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt";
    private static final String TF_API_LABELS_FILE = "file:///android_asset/livefit_labels_list.txt";

    private static final int TF_API_INPUT_SIZE = 300;

    private static Classifier detector = null;

    private final static String DATABASE_NAME = "staging";
    private final static String SYNCGATEWAY_URL = "ws://ec2-34-209-11-84.us-west-2.compute.amazonaws.com:4984/staging";

    private Database database = null;
    private Replicator replicator;
    private String username = DATABASE_NAME;

    private Database backup = null;
    private Replicator backupReplicator = null;

    private DailyValues dailyDataOnLoad =null;

    private Map<String, Product> sampleData =new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        if (LOGIN_FLOW_ENABLED)
            showLoginUI();
        else
            startSession(DATABASE_NAME, null);
    }

    @Override
    public void onTerminate() {
        closeDatabase();
        super.onTerminate();
    }

    public Database getDatabase() {
        return database;
    }

    public Map<String, Product> getProducts() {
        return sampleData;
    }

    public String getUsername() {
        return username;
    }

    // -------------------------
    // Session/Login/Logout
    // -------------------------
    private void startSession(String username, String password) {
        openDatabase(username);
        this.username = username;
        startReplication(username, password);
        localBackup(username);
        //showApp();
    }

    // show loginUI
    private void showLoginUI() {
//        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

        Intent homeScreenIntent= new Intent(this,LoginActivity.class);
//        login();
        homeScreenIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeScreenIntent);
    }

    public void login(String username, String password) {
        this.username = username;
//        DataFetcher fetcher = new DataFetcher(this,this);
//        fetcher.execute();
        String fileName = "cached_products.txt";
        AssetManager assetManager = getAssets();

        startSession(username, password);


        try {
            detector = TensorFlowObjectDetectionAPIModel.create(
                    getAssets(), TF_API_MODEL_FILE, TF_API_LABELS_FILE, TF_API_INPUT_SIZE);

        } catch (final IOException e) {
//            LOGGER.e("Exception initializing classifier!", e);
//            Toast toast =
//                    Toast.makeText(
//                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
//            toast.show();
//            finish();
        }


        int year,day,month;
        String date;
        DailyValues dailyData =null;

        Double totalCalories =0.0, totalSugar=0.0,totalFat=0.0, totalProtein=0.0,totalSalt=0.0;

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        date = (month+1) +"/" + day + "/" + year;
        Query query;   query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
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
                Dictionary valueMap = row.getDictionary(database.getName());

                dailyData = objectMapper.convertValue(valueMap.toMap(),DailyValues.class);
                totalCalories += dailyData.getTotalCalories();
                totalFat+= dailyData.getTotalFat();
                totalProtein += dailyData.getTotalProtein();
                totalSugar += dailyData.getTotalSugar();
                totalSalt += dailyData.getTotalSalt();

            }

            dailyDataOnLoad = new DailyValues("", date,totalCalories,totalSugar,totalFat,totalProtein,totalSalt,"",username);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }



        StringBuilder stringBuilder = new StringBuilder();
        List<Product> universities = null;

        Map<String, Product> mProds = new HashMap<>();
        try {
            // 1. Load data from local sample data file
            InputStream inputStream = assetManager.open(fileName);
            // 2. use Jackson library to map the JSON to List of University POJO
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            universities = Arrays.asList(mapper.readValue(inputStream, Product[].class));

            for (Product prod: universities)
            {
                mProds.put(prod.getProductName(), prod);
            }
            // return universities;
            // return mProds;
            sampleData = mProds;
        } catch (IOException e ) {
            e.printStackTrace();
            // return null;
        }
        catch (Exception e ) {
            e.printStackTrace();
            // return null;
        }
    }

    public void logout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopReplication();
                closeLocalBackup();
                closeDatabase();
                Application.this.username = null;
                showLoginUI();
            }
        });
    }

    private void showApp() {
//        Intent intent = new Intent(getApplicationContext(), ListsActivity.class);
//        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

        Intent homeScreenIntent= new Intent(this,WatsonScreen.class);
//        login();
        homeScreenIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeScreenIntent);
    }

    // -------------------------
    // Database operation
    // -------------------------

    private void openDatabase(String dbname) {
        DatabaseConfiguration config = new DatabaseConfiguration(getApplicationContext());
        try {
            database = new Database(dbname, config);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Failed to create Database instance: %s - %s", e, dbname, config);
            // TODO: error handling
        }
    }

    private void closeDatabase() {
        if (database != null) {
            try {
                database.close();
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Failed to close Database", e);
                // TODO: error handling
            }
        }
    }

    private void createDatabaseIndex() {

    }

    // -------------------------
    // Replicator operation
    // -------------------------
    private void startReplication(String username, String password) {
        if (!SYNC_ENABLED) return;

        URI uri;
        try {
            uri = new URI(SYNCGATEWAY_URL);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Failed parse URI: %s", e, SYNCGATEWAY_URL);
            return;
        }

        Endpoint endpoint = new URLEndpoint(uri);
        ReplicatorConfiguration config = new ReplicatorConfiguration(database, endpoint)
                .setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL)
                .setContinuous(true);

        // authentication
        if (username != null && password != null)
            config.setAuthenticator(new BasicAuthenticator(username, password));

        replicator = new Replicator(config);
        replicator.addChangeListener(this);
        replicator.start();
    }

    private void stopReplication() {
        if (!SYNC_ENABLED) return;

        replicator.stop();
    }

    private void runOnUiThread(Runnable runnable) {
        new Handler(getApplicationContext().getMainLooper()).post(runnable);
    }

    // EE features
    private void localBackup(String username) {
        /*
        // backup db
        String dbname = username + "-backup";
        DatabaseConfiguration config = new DatabaseConfiguration(getApplicationContext());
        try {
            backup = new Database(dbname, config);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Failed to create Database instance: %s - %s", e, dbname, config);
        }

        // backup replication
        Endpoint endpoint = new DatabaseEndpoint(backup);
        ReplicatorConfiguration replConfig = new ReplicatorConfiguration(database, endpoint)
                .setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH)
                .setContinuous(true);
        backupReplicator = new Replicator(replConfig);
        backupReplicator.addChangeListener(this);
        backupReplicator.start();
        */
    }

    private void closeLocalBackup() {
        /*
        // stop replicator
        if (backupReplicator != null)
            backupReplicator.stop();

        // close db
        if (backup != null) {
            try {
                backup.close();
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Failed to close Database", e);
                // TODO: error handling
            }
        }
        */
    }


    // --------------------------------------------------
    // ReplicatorChangeListener implementation
    // --------------------------------------------------
    @Override
    public void changed(ReplicatorChange change) {
        Log.i(TAG, "[Todo] Replicator: status -> %s", change.getStatus());
        if (change.getStatus().getError() != null && change.getStatus().getError().getCode() == 401) {
            Toast.makeText(getApplicationContext(), "Authentication Error: Your username or password is not correct.", Toast.LENGTH_LONG).show();
            logout();
        }

    }

    @Override public void postResult(Map<String, Product> jsonData) {
        // Store the JSON data that is loaded from assets file .
        // This data will inserted into the Couchbase Lite DB whenever
        // a request to "fetch data" is made by the user in order to simulate
        // database updates
        sampleData = jsonData;

        // Initialize Query to fetch documents
        //QueryForListOfUniversities();

    }


    private void setDailyData()
    {

        int year,day,month;
        String date;
        DailyValues dailyData =null;

        Double totalCalories =0.0, totalSugar=0.0,totalFat=0.0, totalProtein=0.0,totalSalt=0.0;

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        date = (month+1) +"/" + day + "/" + year;
        Query query;   query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
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
                Dictionary valueMap = row.getDictionary(database.getName());

                dailyData = objectMapper.convertValue(valueMap.toMap(),DailyValues.class);
                totalCalories += dailyData.getTotalCalories();
                totalFat+= dailyData.getTotalFat();
                totalProtein += dailyData.getTotalProtein();
                totalSugar += dailyData.getTotalSugar();
                totalSalt += dailyData.getTotalSalt();

            }

            dailyDataOnLoad = new DailyValues("", date,totalCalories,totalSugar,totalFat,totalProtein,totalSalt,"",username);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public DailyValues getDailyDataOnLoad()
    {
        return dailyDataOnLoad;
    }

    public Classifier getDetector()
    {
        return detector;
    }

//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(getBaseContext());
//    }
}