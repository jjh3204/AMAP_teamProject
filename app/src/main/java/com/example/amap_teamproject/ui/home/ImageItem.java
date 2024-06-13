package com.example.amap_teamproject.ui.home;

import com.example.amap_teamproject.menu.Activity;

public class ImageItem {
    private String posterUrl;
    private String title;

    public ImageItem(String posterUrl, String title) {
        this.posterUrl = posterUrl;
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
