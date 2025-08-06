package com.example.teachererp.model;

public class HolidayModel {
    String date, day, reason, addedBy;

    public HolidayModel() {} // Required for Firestore

    public HolidayModel(String date, String day, String reason, String addedBy) {
        this.date = date;
        this.day = day;
        this.reason = reason;
        this.addedBy = addedBy;
    }

    public String getDate() { return date; }
    public String getDay() { return day; }
    public String getReason() { return reason; }
    public String getAddedBy() { return addedBy; }
}
