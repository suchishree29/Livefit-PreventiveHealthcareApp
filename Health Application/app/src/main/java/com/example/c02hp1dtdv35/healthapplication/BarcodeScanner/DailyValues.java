package com.example.c02hp1dtdv35.healthapplication.BarcodeScanner;

/**
 * Created by C02HP1DTDV35 on 11/12/18.
 */

public class DailyValues {

    private String id;
    private String date;
    private Double totalCalories;
    private Double totalSugar;
    private Double totalFat;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private Double totalProtein;
    private Double totalSalt;
    private String type;

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

    private String owner;

    public DailyValues() {

    }

    public DailyValues(String id, String date, Double totalCalories, Double totalSugar, Double totalFat, Double totalProtein, Double totalSalt, String type, String owner) {
        this.id = id;
        this.date = date;
        this.totalCalories = totalCalories;
        this.totalSugar = totalSugar;
        this.totalFat = totalFat;
        this.totalProtein = totalProtein;
        this.totalSalt = totalSalt;
        this.type = type;
        this.owner = owner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public Double getTotalSugar() {
        return totalSugar;
    }

    public void setTotalSugar(Double totalSugar) {
        this.totalSugar = totalSugar;
    }

    public Double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(Double totalFat) {
        this.totalFat = totalFat;
    }

    public Double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(Double totalProtein) {
        this.totalProtein = totalProtein;
    }

    public Double getTotalSalt() {
        return totalSalt;
    }

    public void setTotalSalt(Double totalSalt) {
        this.totalSalt = totalSalt;
    }
}