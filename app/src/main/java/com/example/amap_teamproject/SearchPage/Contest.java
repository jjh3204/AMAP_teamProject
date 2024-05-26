package com.example.amap_teamproject.SearchPage;

import java.util.Objects;

public class Contest {
    private String title;          // 제목
    private String link;           // 링크
    private String organization;   // 주최기관
    private String awardScale;     // 상금 규모
    private String contestField;   // 공모 분야
    private String detail;         // 상세 정보
    private String homepage;       // 홈페이지
    private String imgSrc;         // 이미지 소스
    private String noticeUrl;      // 공지 URL
    private String participants;   // 참가 대상
    private String subPeriod;      // 제출 기간
    private String timestamp;      // 타임스탬프

    // Firestore에 필요한 기본 생성자
    public Contest() {
    }

    public Contest(String title, String link, String organization, String awardScale, String contestField, String detail, String homepage, String imgSrc, String noticeUrl, String participants, String subPeriod, String timestamp) {
        this.title = title;
        this.link = link;
        this.organization = organization;
        this.awardScale = awardScale;
        this.contestField = contestField;
        this.detail = detail;
        this.homepage = homepage;
        this.imgSrc = imgSrc;
        this.noticeUrl = noticeUrl;
        this.participants = participants;
        this.subPeriod = subPeriod;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getAwardScale() {
        return awardScale;
    }

    public void setAwardScale(String awardScale) {
        this.awardScale = awardScale;
    }

    public String getContestField() {
        return contestField;
    }

    public void setContestField(String contestField) {
        this.contestField = contestField;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Contest{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", organization='" + organization + '\'' +
                ", awardScale='" + awardScale + '\'' +
                ", contestField='" + contestField + '\'' +
                ", detail='" + detail + '\'' +
                ", homepage='" + homepage + '\'' +
                ", imgSrc='" + imgSrc + '\'' +
                ", noticeUrl='" + noticeUrl + '\'' +
                ", participants='" + participants + '\'' +
                ", subPeriod='" + subPeriod + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contest contest = (Contest) o;
        return Objects.equals(title, contest.title) &&
                Objects.equals(link, contest.link) &&
                Objects.equals(organization, contest.organization) &&
                Objects.equals(awardScale, contest.awardScale) &&
                Objects.equals(contestField, contest.contestField) &&
                Objects.equals(detail, contest.detail) &&
                Objects.equals(homepage, contest.homepage) &&
                Objects.equals(imgSrc, contest.imgSrc) &&
                Objects.equals(noticeUrl, contest.noticeUrl) &&
                Objects.equals(participants, contest.participants) &&
                Objects.equals(subPeriod, contest.subPeriod) &&
                Objects.equals(timestamp, contest.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, link, organization, awardScale, contestField, detail, homepage, imgSrc, noticeUrl, participants, subPeriod, timestamp);
    }
}
