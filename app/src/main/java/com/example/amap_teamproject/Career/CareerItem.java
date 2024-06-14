package com.example.amap_teamproject.Career;

public class CareerItem {
    private String title;
    private String content;
    private String category;
    private String documentId;
    private long timestamp;

    public CareerItem() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public CareerItem(String title, String content, String category, long timestamp) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.timestamp = timestamp;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}