package com.example.amap_teamproject.TeamPage;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    private String type;
    private String content;
    private long timestamp;
    private String authorId;
    private boolean isAuthor;
    private String postId;
    private String parentCommentId;
    private String commentId;
    private String postAuthorId;
    private List<Comment> replies;

    public Comment(){

    }

    public Comment(String type, String content, long timestamp, String authorId, boolean isAuthor, String postId, String parentCommentId, String commentId, String postAuthorId) {
        this.type = type;
        this.content = content;
        this.timestamp = timestamp;
        this.authorId = authorId;
        this.isAuthor = isAuthor;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.commentId = commentId;
        this.postAuthorId = postAuthorId;
        this.replies = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public boolean isAuthor() {
        return isAuthor;
    }

    public void setAuthor(boolean author) {
        isAuthor = author;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getPostAuthorId() {
        return postAuthorId;
    }

    public void setPostAuthorId(String postAuthorId) {
        this.postAuthorId = postAuthorId;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }
}
