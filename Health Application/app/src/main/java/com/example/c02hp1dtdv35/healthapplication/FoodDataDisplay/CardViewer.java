package com.example.c02hp1dtdv35.healthapplication.FoodDataDisplay;

/**
 * Created by C02HP1DTDV35 on 5/15/18.
 */

public class CardViewer {

    private String imageUrl,foodName;

    public CardViewer(String imageUrl, String foodName) {
        this.imageUrl = imageUrl;
        this.foodName = foodName;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
