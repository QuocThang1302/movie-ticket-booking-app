package com.example.moviebooking;

public class FilmReviewModel {
    private String username;
    private String time;
    private String comment;
    private float rating;
    private int imageResId;
    private String movieTitle;

    public FilmReviewModel(String username, String time, String comment, float rating, int imageResId,String movieTitle) {
        this.username = username;
        this.time = time;
        this.comment = comment;
        this.rating = rating;
        this.imageResId = imageResId;
        this.movieTitle = movieTitle;

    }
    public String getMovieTitle() {
        return movieTitle;
    }
    public String getUsername() { return username; }
    public String getTime() { return time; }
    public String getComment() { return comment; }
    public float getRating() { return rating; }
    public int getImageResId() { return imageResId; }
}
