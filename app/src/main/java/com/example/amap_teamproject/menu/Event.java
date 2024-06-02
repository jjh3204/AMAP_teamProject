package com.example.amap_teamproject.menu;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.PropertyName;
import java.util.List;

public class Event implements Parcelable {
    @PropertyName("title")
    private String title;

    @PropertyName("organization")
    private String organization;

    @PropertyName("sub_period")
    private String subPeriod;

    @PropertyName("detail")
    private String detail;

    @PropertyName("award_scale")
    private String awardScale;

    @PropertyName("contest_field")
    private List<String> contestField;

    @PropertyName("homepage")
    private List<String> homepage;

    @PropertyName("img_src")
    private String imgSrc;

    @PropertyName("notice_url")
    private String noticeUrl;

    @PropertyName("participants")
    private String participants;

    public Event() {
        // Firestore는 빈 생성자가 필요합니다.
    }

    protected Event(Parcel in) {
        title = in.readString();
        organization = in.readString();
        subPeriod = in.readString();
        detail = in.readString();
        awardScale = in.readString();
        contestField = in.createStringArrayList();
        homepage = in.createStringArrayList();
        imgSrc = in.readString();
        noticeUrl = in.readString();
        participants = in.readString();
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
        dest.writeString(subPeriod);
        dest.writeString(detail);
        dest.writeString(awardScale);
        dest.writeStringList(contestField);
        dest.writeStringList(homepage);
        dest.writeString(imgSrc);
        dest.writeString(noticeUrl);
        dest.writeString(participants);
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getOrganization() {
        return organization;
    }

    public String getSubPeriod() {
        return subPeriod;
    }

    public String getDetail() {
        return detail;
    }

    public String getAwardScale() {
        return awardScale;
    }

    public List<String> getContestField() {
        return contestField;
    }

    public List<String> getHomepage() {
        return homepage;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public String getNoticeUrl() {
        return noticeUrl;
    }

    public String getParticipants() {
        return participants;
    }
}
