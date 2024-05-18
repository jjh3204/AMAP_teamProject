package com.example.amap_teamproject;

public class Contest {
    public String title;
    public String description;
    public String date;

    public Contest() {
        // Default constructor required for calls to DataSnapshot.getValue(Contest.class)
    }

    public Contest(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    // Getters and setters (if needed)
}
