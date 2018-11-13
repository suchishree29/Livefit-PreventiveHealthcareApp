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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize couchbase lite database manager

        Application application = (Application) getApplication();
        
        db = application.getDatabase();
        if (db == null) throw new IllegalArgumentException();

        // Set content layout
        setContentView(R.layout.activity_list);

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
                    .where(Expression.property("type").equalTo(Expression.string("task-list")))
                    .orderBy(Ordering.property("name").ascending());

            // 2. Add a live query listener to continually monitor for changes
            query.addChangeListener(new QueryChangeListener() {
                                        @Override
                                        public void changed(QueryChange change) {
                                            ResultSet resultRows = change.getResults();
                                            Result row;
                                            //List<ProductRecycleView> universities = new ArrayList<ProductRecycleView>();
                                            List<Product> products = new ArrayList<Product>();
                                            // 3. Iterate over changed rows, corresponding documents and map to University POJO
                                            while ((row = resultRows.next()) != null) {
                                                ObjectMapper objectMapper = new ObjectMapper();
                                                // Ignore undeclared properties
                                                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                                                // String productName = row.getString(0);
                                                // String name = row.getString(1);
                                                String name2 = row.getString("name");
                                                // Get dictionary corresponding to the database name
                                                Dictionary valueMap = row.getDictionary(db.getName());

                                                // Convert from dictionary to corresponding University object
                                        /*        Map ab = valueMap.toMap();
                                                String name = (String) ab.get("name");
                                                String serving_size = (String) ab.get("serving_size");
                                                String calories = (String) ab.get("calories");
                                                String allergens = (String) ab.get("allergens");*/

                                                //ProductRecycleView product = new ProductRecycleView(name,serving_size,calories,allergens);
                                                //ProductRecycleView university1 = objectMapper.convertValue(valueMap.toMap(),ProductRecycleView.class);
                                                Product product = objectMapper.convertValue(valueMap.toMap(),Product.class);
                                                products.add(product);
                                            }

                                            // 4. Update the adapter with the newly added University documents
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