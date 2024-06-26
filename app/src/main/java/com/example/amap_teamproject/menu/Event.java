package com.example.amap_teamproject.menu;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import java.util.List;

public class Event implements Parcelable {
    private String title;
    private String organization;
    @PropertyName("sub_period")
    private String subPeriod;
    private String detail;
    @PropertyName("award_scale")
    private String awardScale;
    @PropertyName("contest_field")
    private List<String> contestField;
    private List<String> homepage;
    @PropertyName("img_src")
    private String imgSrc;
    @PropertyName("notice_url")
    private String noticeUrl;
    private String participants;
    private int hits;
    private int likes;
    private Timestamp timestamp;

    public Event() {
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
        hits = in.readInt();
        likes = in.readInt();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
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
        dest.writeInt(hits);
        dest.writeInt(likes);
        dest.writeParcelable(timestamp, flags);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganization() {
        return organization;
    }

    @PropertyName("sub_period")
    public String getSubPeriod() {
        return subPeriod;
    }

    @PropertyName("detail")
    public String getDetail() {
        return detail;
    }

    @PropertyName("award_scale")
    public String getAwardScale() {
        return awardScale;
    }

    @PropertyName("contest_field")
    public List<String> getContestField() {
        return contestField;
    }

    @PropertyName("homepage")
    public List<String> getHomepage() {
        return homepage;
    }

    @PropertyName("img_src")
    public String getImgSrc() {
        return imgSrc;
    }

    @PropertyName("notice_url")
    public String getNoticeUrl() {
        return noticeUrl;
    }

    @PropertyName("participants")
    public String getParticipants() {
        return participants;
    }

    @PropertyName("hits")
    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    @PropertyName("likes")
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @PropertyName("timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
