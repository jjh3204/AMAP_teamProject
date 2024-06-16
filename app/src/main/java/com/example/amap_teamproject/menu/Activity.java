package com.example.amap_teamproject.menu;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.Timestamp;
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
    private int hits; // 조회수 필드 추가
    private int likes; // 좋아요 필드 추가
    private Timestamp timestamp; // 타임스탬프 필드 추가

    public Activity() {
    }

    public Activity(Parcel in) {
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
        hits = in.readInt();
        likes = in.readInt();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
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
        dest.writeInt(hits);
        dest.writeInt(likes);
        dest.writeParcelable(timestamp, flags);
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

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

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

