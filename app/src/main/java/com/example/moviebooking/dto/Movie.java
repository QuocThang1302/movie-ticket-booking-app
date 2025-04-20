package com.example.moviebooking.dto;

import androidx.annotation.NonNull;

import com.example.moviebooking.R;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Movie implements Serializable {
    private String movieID;
    @PropertyName("title")
    private String title;
    @PropertyName("description")
    private String description;
    @PropertyName("thumbnail")
    private String thumbnail;
    private int thumbnailUri = R.drawable.ic_launcher_background;
    @PropertyName("duration")
    private String duration;
    @PropertyName("genres")
    private List<String> genres;
    @PropertyName("rate")
    private String rate;
    @PropertyName("year")
    private String year;
    private String trailerYoutube = "https://www.youtube.com/watch?v=0";

    public Movie(String movieID, String movieInfo) {
        this.movieID = movieID;

        String[] infoParts = movieInfo.split(", ");

        for (String part : infoParts) {
            if (part.startsWith("title=")) {
                this.title = part.substring("title='".length(), part.length() - 1);
            } else if (part.startsWith("description=")) {
                this.description = part.substring("description='".length(), part.length() - 1);
            } else if (part.startsWith("thumbnail=")) {
                this.thumbnail = part.substring("thumbnail='".length(), part.length() - 1);
            } else if (part.startsWith("genre={")) {
                String genreString = part.substring("genre={".length(), part.length() - 1);
                String[] genres = genreString.split(", ");
                this.genres = new ArrayList<>(Arrays.asList(genres));
            } else if (part.startsWith("duration=")) {
                this.duration = part.substring("duration=".length());
            } else if (part.startsWith("rate=")) {
                this.rate = part.substring("rate=".length());
            } else if (part.startsWith("trailerYoutube='")) {
                this.trailerYoutube = part.substring("trailerYoutube='".length(), part.length() - 1);
            }
        }
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMainGenre() {
        return genres.get(0);
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTrailerYoutube() {
        return trailerYoutube;
    }

    public void setTrailerYoutube(String trailerYoutube) {
        this.trailerYoutube = trailerYoutube;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getMovieID() {
        return movieID;
    }
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMovieInfo() {
        String data =
                "title='" + title + '\'' +
                        ", description='" + description + '\'' +
                        ", thumbnail='" + thumbnail + '\'' +
                        ", genre={";
        for (String s : genres) {
            if (s.equals(genres.get(genres.size() - 1))) {
                data = data + "'" + s + "'";
                break;
            }
            data = data + "'" + s + "'" + ", ";
        }
        data = data +
                "}, duration=" + duration +
                ", rate=" + rate +
                ", trailerYoutube='" + trailerYoutube + '\'' +
                '}';
        return data;
    }

    @NonNull
    @Override
    public String toString() {
        String data =  "Movie{" +
                ", movieId='" + movieID + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", genre={";
        for (String s : genres) {
            if (s.equals(genres.get(genres.size() - 1))) {
                data = data + "'" + s + "'";
                break;
            }
            data = data + "'" + s + "'" + ", ";
        }
        data = data +
                "}, duration=" + duration +
                ", rate=" + rate +
                ", trailerYoutube='" + trailerYoutube + '\'' +
                '}';
        return data;
    }

    public Movie(String movieID, String title, String description, String imageUrl, List<String> genres, String duration, String rate, String trailerYoutube) {
        this.movieID = movieID;
        this.title = title;
        this.description = description;
        this.thumbnail = imageUrl;
        this.genres = genres;
        this.duration = duration;
        this.rate = rate;
        this.trailerYoutube = trailerYoutube;
    }

    public Movie() {
        this.title = "title";
        this.description = "description";
    }

    public int getThumbnailUri() {
        return thumbnailUri;
    }

    public String getDetailDuration() {
        String hour_min;
        int hour = Integer.parseInt(duration) / 60;
        int minute = Integer.parseInt(duration) % 60;
        if (hour == 0) {
            hour_min = minute + "m";
        } else {
            hour_min = hour + "h " + minute + "min";
        }
        return hour_min;
    }
}
