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
    private List<String> actField;
    private List<String> actRegion;
    private List<String> homepage;
    private List<String> interestField;
    private String noticeUrl;
    private String participants;
    private String subPeriod;

    public Activity() {
        // Firestore는 빈 생성자가 필요합니다.
    }

    protected Activity(Parcel in) {
        title = in.readString();
        organization = in.readString();
        actPeriod = in.readString();
        detail = in.readString();
        posterUrl = in.readString();
        actField = in.createStringArrayList();
        actRegion = in.createStringArrayList();
        homepage = in.createStringArrayList();
        interestField = in.createStringArrayList();
        noticeUrl = in.readString();
        participants = in.readString();
        subPeriod = in.readString();
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
        dest.writeStringList(actField);
        dest.writeStringList(actRegion);
        dest.writeStringList(homepage);
        dest.writeStringList(interestField);
        dest.writeString(noticeUrl);
        dest.writeString(participants);
        dest.writeString(subPeriod);
    }

    // Getter and Setter methods
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getActPeriod() {
        return actPeriod;
    }

    public void setActPeriod(String actPeriod) {
        this.actPeriod = actPeriod;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public List<String> getActField() {
        return actField;
    }

    public void setActField(List<String> actField) {
        this.actField = actField;
    }

    public List<String> getActRegion() {
        return actRegion;
    }

    public void setActRegion(List<String> actRegion) {
        this.actRegion = actRegion;
    }

    public List<String> getHomepage() {
        return homepage;
    }

    public void setHomepage(List<String> homepage) {
        this.homepage = homepage;
    }

    public List<String> getInterestField() {
        return interestField;
    }

    public void setInterestField(List<String> interestField) {
        this.interestField = interestField;
    }

    public String getNoticeUrl() {
        return noticeUrl;
    }

    public void setNoticeUrl(String noticeUrl) {
        this.noticeUrl = noticeUrl;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getSubPeriod() {
        return subPeriod;
    }

    public void setSubPeriod(String subPeriod) {
        this.subPeriod = subPeriod;
    }


}


