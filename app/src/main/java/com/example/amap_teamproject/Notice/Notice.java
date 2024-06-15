package com.example.amap_teamproject.Notice;

public class Notice {
    private String title;
    private String content;
    private long timestamp;
    private String documentId;

    public Notice() {
    }

    public Notice(String title, String content, long timestamp, String documentId) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.documentId = documentId;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}