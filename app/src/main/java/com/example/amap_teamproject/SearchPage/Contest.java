package com.example.amap_teamproject.SearchPage;

public class Contest {
    private String title;
    private String link;
    private String organization;

    public Contest() {
        // Firestore requires a public no-argument constructor
    }

    public Contest(String title, String link, String organization) {
        this.title = title;
        this.link = link;
        this.organization = organization;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
