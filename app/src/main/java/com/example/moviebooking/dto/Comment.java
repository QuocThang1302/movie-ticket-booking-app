package com.example.moviebooking.dto;

public class Comment {
    private String commentId;
    private String movieId;
    private String content;
    private float rating;
    private long timestamp;
    private String username;
    private String movieTitle;


    public Comment() {
    }


    public Comment(String movieId, String movieTitle, String content, float rating, long timestamp, String username) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.content = content;
        this.rating = rating;
        this.timestamp = timestamp;
        this.username = username;
    }

    public Comment(String commentId, String movieId, String movieTitle, String content, float rating, long timestamp, String username) {
        this.commentId = commentId;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.content = content;
        this.rating = rating;
        this.timestamp = timestamp;
        this.username = username;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
}