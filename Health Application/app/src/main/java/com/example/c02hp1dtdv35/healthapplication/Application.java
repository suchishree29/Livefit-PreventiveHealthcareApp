package com.example.c02hp1dtdv35.healthapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Endpoint;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.URLEndpoint;
import com.couchbase.lite.internal.support.Log;
import com.example.c02hp1dtdv35.healthapplication.Home.WatsonScreen;
import com.example.c02hp1dtdv35.healthapplication.Login.LoginActivity;

import java.net.URI;
import java.net.URISyntaxException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/*
public interface ReplicatorChangeListener {
    void changed(ReplicatorChange change);
}
 */
public class Application extends android.app.Application implements ReplicatorChangeListener {

    private static final String TAG = Application.class.getSimpleName();

    private final static boolean LOGIN_FLOW_ENABLED = true;
    private final static boolean SYNC_ENABLED = true;

    private final static String DATABASE_NAME = "staging";
<<<<<<< Application.java
    private final static String SYNCGATEWAY_URL = "ws://10.0.0.220:4984/staging";
=======
    private final static String SYNCGATEWAY_URL = "ws://10.250.145.62:4984/staging";
>>>>>>> Application.java

    private Database database = null;
    private Replicator replicator;
    private String username = DATABASE_NAME;

    private Database backup = null;
    private Replicator backupReplicator = null;

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

        // TODO: After authenticated, move to next screen
<<<<<<< Application.java
        showApp();
=======
       // showApp();
>>>>>>> Application.java
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
        startSession(username, password);
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

//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(getBaseContext());
//    }
}
