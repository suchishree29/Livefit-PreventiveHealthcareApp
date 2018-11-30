package com.example.c02hp1dtdv35.healthapplication.Login;

import java.util.ArrayList;
import java.util.Date;

public class UserProfile {

    private String firstName;
    private String lastName;

    private String emailId;
    private String gender;
    private String height;
    private String weight;

    private String type;
    private boolean smoker;

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    private boolean alcoholic;

    private String bloodGlucose;

    private String cholestorol;

    private Date dateUpdated;

    private ArrayList<String> diseases;

    private ArrayList<String> Allergens;

    public UserProfile() {
       // this.firstName = firstName;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", gender='" + gender + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", type='" + type + '\'' +
                ", smoker=" + smoker +
                ", alcoholic=" + alcoholic +
                ", bloodGlucose='" + bloodGlucose + '\'' +
                ", cholestorol='" + cholestorol + '\'' +
                ", diseases=" + diseases +
                ", Allergens=" + Allergens +
                '}';
    }

    public UserProfile(String firstName, String lastName, String emailId, String gender, String height, String weight, String type, boolean smoker, boolean alcoholic, String bloodGlucose, String cholestorol, Date dateUpdated, ArrayList<String> diseases, ArrayList<String> allergens) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailId = emailId;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.type = type;
        this.smoker = smoker;
        this.alcoholic = alcoholic;
        this.bloodGlucose = bloodGlucose;
        this.cholestorol = cholestorol;
        this.dateUpdated = dateUpdated;
        this.diseases = diseases;
        Allergens = allergens;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public boolean isSmoker() {
        return smoker;
    }

    public void setSmoker(boolean smoker) {
        this.smoker = smoker;
    }

    public boolean isAlcoholic() {
        return alcoholic;
    }

    public void setAlcoholic(boolean alcoholic) {
        this.alcoholic = alcoholic;
    }

    public String getBloodGlucose() {
        return bloodGlucose;
    }

    public void setBloodGlucose(String bloodGlucose) {
        this.bloodGlucose = bloodGlucose;
    }

    public String getCholestorol() {
        return cholestorol;
    }

    public void setCholestorol(String cholestorol) {
        this.cholestorol = cholestorol;
    }

    public ArrayList<String> getDiseases() {
        return diseases;
    }

    public void setDiseases(ArrayList<String> diseases) {
        this.diseases = diseases;
    }

    public ArrayList<String> getAllergens() {
        return Allergens;
    }

    public void setAllergens(ArrayList<String> allergens) {
        Allergens = allergens;
    }
}
