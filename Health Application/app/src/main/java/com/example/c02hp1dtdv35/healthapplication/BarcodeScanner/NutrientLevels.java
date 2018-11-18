package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NutrientLevels {

    @SerializedName("fat")
    @Expose
    private String fat;
    @SerializedName("salt")
    @Expose
    private String salt;
    @SerializedName("saturated-fat")
    @Expose
    private String saturatedFat;
    @SerializedName("sugars")
    @Expose
    private String sugars;

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(String saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public String getSugars() {
        return sugars;
    }

    public void setSugars(String sugars) {
        this.sugars = sugars;
    }

    private String high = "";

    public String getHigh() {
        setHigh();
        return high;
    }

    public void setHigh() {
        if(getFat().equals("high"))
        {
            high = "fat ";
        }

        if(getSalt().equals("high"))
        {
            high = high + "Salt ";
        }

        if(getSaturatedFat().equals("high"))
        {
            high = high + "saturated-fat ";
        }

        if(getSugars().equals("high"))
        {
            high = high + "sugars ";
        }
    }


}