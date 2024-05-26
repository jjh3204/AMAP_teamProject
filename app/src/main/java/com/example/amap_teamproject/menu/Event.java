package com.example.amap_teamproject.menu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Event implements Parcelable {
    private String title;
    private String organization;
    private String deadline;
    private String description;
    private String type;
    private String posterUrl;
    private String detail;
    private String homepage;
    private List<String> actField;
    private String actPeriod;
    private List<String> actRegion;
    private List<String> interestField;
    private String noticeUrl;
    private String participants;
    private String subPeriod;
    private long timestamp;

    public Event() {
        // Firestore는 빈 생성자가 필요합니다.
    }

    public Event(String title, String organization, String deadline, String description, String type,
                 String posterUrl, String detail, String homepage, List<String> actField, String actPeriod,
                 List<String> actRegion, List<String> interestField, String noticeUrl, String participants,
                 String subPeriod, long timestamp) {
        this.title = title;
        this.organization = organization;
        this.deadline = deadline;
        this.description = description;
        this.type = type;
        this.posterUrl = posterUrl;
        this.detail = detail;
        this.homepage = homepage;
        this.actField = actField;
        this.actPeriod = actPeriod;
        this.actRegion = actRegion;
        this.interestField = interestField;
        this.noticeUrl = noticeUrl;
        this.participants = participants;
        this.subPeriod = subPeriod;
        this.timestamp = timestamp;
    }

    protected Event(Parcel in) {
        title = in.readString();
        organization = in.readString();
        deadline = in.readString();
        description = in.readString();
        type = in.readString();
        posterUrl = in.readString();
        detail = in.readString();
        homepage = in.readString();
        actField = in.createStringArrayList();
        actPeriod = in.readString();
        actRegion = in.createStringArrayList();
        interestField = in.createStringArrayList();
        noticeUrl = in.readString();
        participants = in.readString();
        subPeriod = in.readString();
        timestamp = in.readLong();
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
        dest.writeString(type);
        dest.writeString(posterUrl);
        dest.writeString(detail);
        dest.writeString(homepage);
        dest.writeStringList(actField);
        dest.writeString(actPeriod);
        dest.writeStringList(actRegion);
        dest.writeStringList(interestField);
        dest.writeString(noticeUrl);
        dest.writeString(participants);
        dest.writeString(subPeriod);
        dest.writeLong(timestamp);
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

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getDetail() {
        return detail;
    }

    public String getHomepage() {
        return homepage;
    }

    public List<String> getActField() {
        return actField;
    }

    public String getActPeriod() {
        return actPeriod;
    }

    public List<String> getActRegion() {
        return actRegion;
    }

    public List<String> getInterestField() {
        return interestField;
    }

    public String getNoticeUrl() {
        return noticeUrl;
    }

    public String getParticipants() {
        return participants;
    }

    public String getSubPeriod() {
        return subPeriod;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

