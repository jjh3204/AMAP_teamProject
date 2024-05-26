package com.example.amap_teamproject.Career;

public class CareerItem {
    private String id;
    private String title;
    private String content;
    private String field;

    public CareerItem() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public CareerItem(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getField() { return field; }

    public void setField(String field) { this.field = field; }
}