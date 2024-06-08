package com.example.amap_teamproject.TeamPage;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    private String id;
    private String content;
    private long timestamp;
    private String authorId;
    private boolean isSecret;
    private boolean isAuthor;
    private String postId;
    private String parentCommentId;
    private List<Comment> replies;

    public Comment(String content, long timestamp, String authorId, boolean isSecret, boolean isAuthor, String postId, String parentCommentId) {
        this.content = content;
        this.timestamp = timestamp;
        this.authorId = authorId;
        this.isSecret = isSecret;
        this.isAuthor = isAuthor;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.replies = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isSecret() {
        return isSecret;
    }

    public void setSecret(boolean secret) {
        isSecret = secret;
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

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }
}
