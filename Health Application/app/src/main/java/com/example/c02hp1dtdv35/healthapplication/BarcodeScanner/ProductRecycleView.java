package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import java.util.ArrayList;
import java.util.List;

public class ProductRecycleView {
    private String name;
    private String product_img;
    private String meal_date;
    private String serving_size;

    private String calories;
    private String allergens;
    private String meal_course;
    private String type;
    private String owner;


    public ProductRecycleView(String name, String product_img, String meal_date, String serving_size, String calories, String allergens, String meal_course, String type, String owner) {
        this.name = name;
        this.product_img = product_img;
        this.meal_date = meal_date;
        this.serving_size = serving_size;
        this.calories = calories;
        this.allergens = allergens;
        this.meal_course = meal_course;
        this.type = type;
        this.owner = owner;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    public String getMeal_date() {
        return meal_date;
    }

    public void setMeal_date(String meal_date) {
        this.meal_date = meal_date;
    }

    public String getServing_size() {
        return serving_size;
    }

    public void setServing_size(String serving_size) {
        this.serving_size = serving_size;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public String getMeal_course() {
        return meal_course;
    }

    public void setMeal_course(String meal_course) {
        this.meal_course = meal_course;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ProductRecycleView(String name, String servingSize, String calories, String allergens) {
        this.name = name;
        this.serving_size = servingSize;
        this.calories = calories;
        this.allergens = allergens;
    }

    public static ArrayList<ProductRecycleView> createProductList(String productName, String servingSize, String calories, String allergens, int numProducts) {
        ArrayList<ProductRecycleView> products = new ArrayList<>();

        for (int i = 1; i <= numProducts; i++) {
            products.add(new ProductRecycleView(productName , servingSize, calories,allergens));
        }

        return products;
    }


}
