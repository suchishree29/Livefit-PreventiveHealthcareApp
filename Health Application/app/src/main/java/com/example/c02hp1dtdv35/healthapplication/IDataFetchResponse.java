package com.example.c02hp1dtdv35.healthapplication;


import com.example.c02hp1dtdv35.healthapplication.BarcodeScanner.Product;

import java.util.List;
import java.util.Map;

/**
 * Created by priya.rajagopal on 8/4/17.
 */

public interface IDataFetchResponse {
    void postResult(Map<String, Product> jsonData);
}
