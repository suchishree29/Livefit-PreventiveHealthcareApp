package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NutrientLevels {

    @SerializedName("saturated-fat")
    @Expose
    private String saturatedFat;
    @SerializedName("salt")
    @Expose
    private String salt;
    @SerializedName("sugars")
    @Expose
    private String sugars;
    @SerializedName("fat")
    @Expose
    private String fat;

    public String getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(String saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSugars() {
        return sugars;
    }

    public void setSugars(String sugars) {
        this.sugars = sugars;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

}