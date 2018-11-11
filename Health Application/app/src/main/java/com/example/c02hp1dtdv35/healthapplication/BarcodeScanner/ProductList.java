package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import java.util.ArrayList;

public class ProductList {
    private String productName;
    private String servingSize;
    private String calories;
    private String allergens;


    public ProductList(String productName, String servingSize, String calories, String allergens) {
        this.productName = productName;
        this.servingSize = servingSize;
        this.calories = calories;
        this.allergens = allergens;
    }

    public String getName() {
        return productName;
    }

    public String getServingSize() {
        return servingSize;
    }

    public String getCalories() {
        return calories;
    }

    public String getAllergens() {
        return allergens;
    }


    public static ArrayList<ProductList> createProductList(String productName, String servingSize, String calories, String allergens, int numProducts) {
        ArrayList<ProductList> products = new ArrayList<>();

        for (int i = 1; i <= numProducts; i++) {
            products.add(new ProductList(productName , servingSize, calories,allergens));
        }

        return products;
    }
}
