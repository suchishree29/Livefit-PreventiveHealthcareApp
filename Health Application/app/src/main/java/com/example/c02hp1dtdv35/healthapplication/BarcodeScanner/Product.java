package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("nutrient_levels")
    @Expose
    private NutrientLevels nutrientLevels;
    @SerializedName("nutrition_score_debug")
    @Expose
    private String nutritionScoreDebug;
    @SerializedName("nutriments")
    @Expose
    private Nutriments nutriments;
    @SerializedName("allergens")
    @Expose
    private String allergens;
    @SerializedName("image_small_url")
    @Expose
    private String imageSmallUrl;
    @SerializedName("nutrition_grade_fr")
    @Expose
    private String nutritionGradeFr;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("serving_size")
    @Expose
    private String servingSize;
    @SerializedName("nutrition_grades")
    @Expose
    private String nutritionGrades;

    public NutrientLevels getNutrientLevels() {
        return nutrientLevels;
    }

    public void setNutrientLevels(NutrientLevels nutrientLevels) {
        this.nutrientLevels = nutrientLevels;
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

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public String getImageSmallUrl() {
        return imageSmallUrl;
    }

    public void setImageSmallUrl(String imageSmallUrl) {
        this.imageSmallUrl = imageSmallUrl;
    }

    public String getNutritionGradeFr() {
        return nutritionGradeFr;
    }

    public void setNutritionGradeFr(String nutritionGradeFr) {
        this.nutritionGradeFr = nutritionGradeFr;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public String getNutritionGrades() {
        return nutritionGrades;
    }

    public void setNutritionGrades(String nutritionGrades) {
        this.nutritionGrades = nutritionGrades;
    }

}