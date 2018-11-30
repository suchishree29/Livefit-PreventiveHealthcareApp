package com.example.c02hp1dtdv35.healthapplication;

import android.content.Context;
import android.os.AsyncTask;


import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Product;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by priya.rajagopal on 8/4/17.
 */

// Once could replace this implementation to load data from remote server
public class DataFetcher extends AsyncTask<Void,Void,Map<String, Product>> {
    private Context mContext;
    private IDataFetchResponse mDelegate = null;

    public DataFetcher(Context context,IDataFetchResponse delegate) {
        mContext = context;
        mDelegate = delegate;
    }



    @Override
    protected Map<String, Product> doInBackground(Void... voids) {
        String fileName = "cached_products.txt";
        StringBuilder stringBuilder = new StringBuilder();
        List<Product> universities = null;

        Map<String, Product> mProds = new HashMap<>();
        try {
            // 1. Load data from local sample data file
            InputStream inputStream = mContext.getAssets().open(fileName);
            // 2. use Jackson library to map the JSON to List of University POJO
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            universities = Arrays.asList(mapper.readValue(inputStream, Product[].class));

            for (Product prod: universities)
            {
                mProds.put(prod.getProductName(), prod);
            }
           // return universities;
            return mProds;
        } catch (IOException  e ) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    protected void onPostExecute(Map<String, Product> result) {
        // 3. Notify the IDataFetchResponse delegate (which in this case is ListActivity) of the availability of data
        mDelegate.postResult(result);

    }
}
