package com.example.amap_teamproject.menu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Activity implements Parcelable {
    private String title;
    private String organization;
    private String actPeriod;
    private String detail;
    private String posterUrl;

    public Activity() {
        // Firestore는 빈 생성자가 필요합니다.
    }

    protected Activity(Parcel in) {
        title = in.readString();
        organization = in.readString();
        actPeriod = in.readString();
        detail = in.readString();
        posterUrl = in.readString();
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(organization);
        dest.writeString(actPeriod);
        dest.writeString(detail);
        dest.writeString(posterUrl);
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getOrganization() {
        return organization;
    }

    public String getActPeriod() {
        return actPeriod;
    }

    public String getDetail() {
        return detail;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
}

