package com.example.amap_teamproject.menu;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private String title;
    private String organization;
    private String deadline;
    private int imageResourceId;
    private String description;
    private String type;

    public Event(String title, String organization, String deadline, int imageResourceId, String description, String type) {
        this.title = title;
        this.organization = organization;
        this.deadline = deadline;
        this.imageResourceId = imageResourceId;
        this.description = description;
        this.type = type;
    }

    protected Event(Parcel in) {
        title = in.readString();
        organization = in.readString();
        deadline = in.readString();
        imageResourceId = in.readInt();
        description = in.readString();
        type = in.readString();
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
        dest.writeInt(imageResourceId);
        dest.writeString(description);
        dest.writeString(type);
    }

    public String getTitle() {
        return title;
    }

    public String getOrganization() {
        return organization;
    }

    public String getDeadline() {
        return deadline;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }
}

