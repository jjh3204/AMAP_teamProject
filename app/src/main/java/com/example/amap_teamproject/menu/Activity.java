package com.example.amap_teamproject.menu;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class Activity implements Parcelable {
    private String title;
    private String organization;
    @PropertyName("act_period")
    private String actPeriod;
    @PropertyName("sub_period")
    private String subPeriod;
    private String detail;
    @PropertyName("poster_url")
    private String posterUrl;
    private String participants;
    @PropertyName("act_field")
    private List<String> actField;
    @PropertyName("act_region")
    private List<String> actRegion;
    @PropertyName("interest_field")
    private List<String> interestField;
    @PropertyName("notice_url")
    private String noticeUrl;
    private List<String> homepage;

    public Activity() {
        // Firestore는 빈 생성자가 필요합니다.
    }

    protected Activity(Parcel in) {
        title = in.readString();
        organization = in.readString();
        actPeriod = in.readString();
        subPeriod = in.readString();
        detail = in.readString();
        posterUrl = in.readString();
        participants = in.readString();
        actField = in.createStringArrayList();
        actRegion = in.createStringArrayList();
        interestField = in.createStringArrayList();
        noticeUrl = in.readString();
        homepage = in.createStringArrayList();
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
        dest.writeString(subPeriod);
        dest.writeString(detail);
        dest.writeString(posterUrl);
        dest.writeString(participants);
        dest.writeStringList(actField);
        dest.writeStringList(actRegion);
        dest.writeStringList(interestField);
        dest.writeString(noticeUrl);
        dest.writeStringList(homepage);
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getOrganization() {
        return organization;
    }

    @PropertyName("act_period")
    public String getActPeriod() {
        return actPeriod;
    }

    @PropertyName("sub_period")
    public String getSubPeriod() {
        return subPeriod;
    }

    public String getDetail() {
        return detail;
    }

    @PropertyName("poster_url")
    public String getPosterUrl() {
        return posterUrl;
    }

    public String getParticipants() {
        return participants;
    }

    @PropertyName("act_field")
    public List<String> getActField() {
        return actField;
    }

    @PropertyName("act_region")
    public List<String> getActRegion() {
        return actRegion;
    }

    @PropertyName("interest_field")
    public List<String> getInterestField() {
        return interestField;
    }

    @PropertyName("notice_url")
    public String getNoticeUrl() {
        return noticeUrl;
    }

    public List<String> getHomepage() {
        return homepage;
    }

}
