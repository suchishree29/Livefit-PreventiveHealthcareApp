package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("serving_size")
    @Expose
    private String servingSize;
    @SerializedName("product_name")
    @Expose
    private String product_name;
    @SerializedName("image_small_url")
    @Expose
    private String imageSmallUrl;
    @SerializedName("nutrition_score_debug")
    @Expose
    private String nutritionScoreDebug;
    @SerializedName("nutriments")
    @Expose
    private Nutriments nutriments;
    @SerializedName("nutrient_levels")
    @Expose
    private NutrientLevels nutrientLevels;

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String productName) {
        this.product_name = productName;
    }
    public String getImageSmallUrl() {
        return imageSmallUrl;
    }

    public void setImageSmallUrl(String imageSmallUrl) {
        this.imageSmallUrl = imageSmallUrl;
    }

    public String getNutritionScoreDebug() {
        return nutritionScoreDebug;
    }

    public void setNutritionScoreDebug(String nutritionScoreDebug) {
        this.nutritionScoreDebug = nutritionScoreDebug;
    }

    public Nutriments getNutriments() {
        return nutriments;
    }

    public void setNutriments(Nutriments nutriments) {
        this.nutriments = nutriments;
    }

    public NutrientLevels getNutrientLevels() {
        return nutrientLevels;
    }

    public void setNutrientLevels(NutrientLevels nutrientLevels) {
        this.nutrientLevels = nutrientLevels;
    }

}