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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return productName != null ? productName.equals(product.productName) : product.productName == null;
    }

    @Override
    public int hashCode() {
        return productName != null ? productName.hashCode() : 0;
    }

    public Product(NutrientLevels nutrientLevels, String nutritionScoreDebug, Nutriments nutriments, String allergens, String imageSmallUrl, String nutritionGradeFr, String productName, String servingSize, String nutritionGrades, String type, String owner, String meal_course, String meal_date) {
        this.nutrientLevels = nutrientLevels;
        this.nutritionScoreDebug = nutritionScoreDebug;
        this.nutriments = nutriments;
        this.allergens = allergens;
        this.imageSmallUrl = imageSmallUrl;
        this.nutritionGradeFr = nutritionGradeFr;
        this.productName = productName;
        this.servingSize = servingSize;
        this.nutritionGrades = nutritionGrades;
        this.type = type;
        this.owner = owner;
        this.meal_course = meal_course;

        this.meal_date = meal_date;
    }

    @SerializedName("nutriments")
    @Expose
    private Nutriments nutriments;
    @SerializedName("allergens")
    @Expose
    private String allergens;

    public Product() {

    }

    @SerializedName("image_small_url")
    @Expose

    private String imageSmallUrl;
    @SerializedName("nutrition_grade_fr")
    @Expose
    private String nutritionGradeFr;

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

    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("serving_size")
    @Expose
    private String servingSize;
    @SerializedName("nutrition_grades")
    @Expose
    private String nutritionGrades;

    private String type;
    private String owner;
    private String meal_course;
    private String meal_date;

    public String getMeal_course() {
        return meal_course;
    }

    public void setMeal_course(String meal_course) {
        this.meal_course = meal_course;
    }

    public String getMeal_date() {
        return meal_date;
    }

    public void setMeal_date(String meal_date) {
        this.meal_date = meal_date;
    }

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