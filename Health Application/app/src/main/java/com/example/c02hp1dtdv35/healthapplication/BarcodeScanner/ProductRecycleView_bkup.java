package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import java.util.ArrayList;
import java.util.List;

public class ProductRecycleView_bkup {
    private String name;
    private String servingSize;
    private String calories;
    private String allergens;


    public ProductRecycleView_bkup(String name, String servingSize, String calories, String allergens) {
        this.name = name;
        this.servingSize = servingSize;
        this.calories = calories;
        this.allergens = allergens;
    }


    public ProductRecycleView_bkup(List<ProductRecycleView_bkup> products, String mode) {
        this.name = name;
        this.servingSize = servingSize;
        this.calories = calories;
        this.allergens = allergens;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void set(String name) {
        this.name = name;
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


    public static ArrayList<ProductRecycleView_bkup> createProductList(String productName, String servingSize, String calories, String allergens, int numProducts) {
        ArrayList<ProductRecycleView_bkup> products = new ArrayList<>();

        for (int i = 1; i <= numProducts; i++) {
            products.add(new ProductRecycleView_bkup(productName , servingSize, calories,allergens));
        }

        return products;
    }
}
