package com.example.c02hp1dtdv35.healthapplication.Remind.model;

import io.realm.RealmObject;

public class EventModelDB extends RealmObject {


    private String event, time, date, timestamp;


    public EventModelDB() {
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
