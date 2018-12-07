package com.example.c02hp1dtdv35.healthapplication.LoggedFoodDisplay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.c02hp1dtdv35.healthapplication.Application;
import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Product;

import com.example.c02hp1dtdv35.healthapplication.R;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ShowLoggedFoodActivity extends AppCompatActivity{

    private final ProductListAdapter adapter = new ProductListAdapter(this);
    private Query query;

    private Database db;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize couchbase lite database manager

        Application application = (Application) getApplication();
        db = application.getDatabase();
        if (db == null) throw new IllegalArgumentException();
        username = application.getUsername();
        // Set content layout
        setContentView(R.layout.activity_list);
        getSupportActionBar().setTitle("Logged Foods");
        QueryForListOfProducts();
        // Get recycler view
        RecyclerView recyclerView = findViewById(R.id.rvLoggedProducts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void QueryForListOfProducts() {
        try {
            // 1. Create a liveQuery to fetch all documents from database
            query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db))
                    .where(Expression.property("type").equalTo(Expression.string("task-list"))
                    .and(Expression.property("owner").equalTo(Expression.string(username))))
                    .orderBy(Ordering.property("meal_date").descending());

            // 2. Add a live query listener to continually monitor for changes
            query.addChangeListener(new QueryChangeListener() {
                                        @Override
                                        public void changed(QueryChange change) {
                                            ResultSet resultRows = change.getResults();
                                            Result row;
                                            List<Product> products = new ArrayList<Product>();
                                            // 3. Iterate over changed rows, corresponding documents and map to Product POJO
                                            while ((row = resultRows.next()) != null) {
                                                ObjectMapper objectMapper = new ObjectMapper();
                                                // Ignore undeclared properties
                                                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                                                // Get dictionary corresponding to the database name
                                                Dictionary valueMap = row.getDictionary(db.getName());

                                                Product product = objectMapper.convertValue(valueMap.toMap(),Product.class);
                                                products.add(product);
                                            }

                                            // 4. Update the adapter with the newly added products documents
                                            adapter.setProductList(products);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // 5. Notify adapter of changed data
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }
            );
            // 6. Run Query
            query.execute();
        }
        catch (IllegalArgumentException e) {

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

}