package com.example.teachererp.model;

public class NoticeModel {
    private String title;
    private String description;
    private long date;

    public NoticeModel() {
        // Empty constructor needed for Firestore
    }

    public NoticeModel(String title, String description, long date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getDate() {
        return date;
    }
}
