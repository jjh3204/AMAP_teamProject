package com.example.amap_teamproject.menu;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private String title;
    private String organization;
    private String deadline;
    private String description;
    private String posterUrl;

    public Event() {
        // Firestore는 빈 생성자가 필요합니다.
    }

    protected Event(Parcel in) {
        title = in.readString();
        organization = in.readString();
        deadline = in.readString();
        description = in.readString();
        posterUrl = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
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
        dest.writeString(deadline);
        dest.writeString(description);
        dest.writeString(posterUrl);
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getOrganization() {
        return organization;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getDescription() {
        return description;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
}



