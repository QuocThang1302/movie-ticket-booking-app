package com.example.moviebooking.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Schedule implements Serializable {
    private String scheduleId;
    private String movieId;
    private String cinemaId;
    private List<DateTime> showTimes; // List of DateTime objects
    private boolean isActive;

    // Default constructor required for Firebase
    public Schedule() {
    }

    public Schedule(String movieId, String cinemaId, List<DateTime> showTimes) {
        this.movieId = movieId;
        this.cinemaId = cinemaId;
        this.showTimes = showTimes;
        this.isActive = true;
    }

    // Constructor with scheduleId
    public Schedule(String scheduleId, String movieId, String cinemaId, List<DateTime> showTimes, boolean isActive) {
        this.scheduleId = scheduleId;
        this.movieId = movieId;
        this.cinemaId = cinemaId;
        this.showTimes = showTimes;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public List<DateTime> getShowTimes() {
        return showTimes;
    }

    public void setShowTimes(List<DateTime> showTimes) {
        this.showTimes = showTimes;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Helper method to convert DateTime objects to Strings for Firebase
    public List<String> getShowTimesAsStrings() {
        List<String> showTimeStrings = new ArrayList<>();
        if (showTimes != null) {
            for (DateTime dateTime : showTimes) {
                showTimeStrings.add(dateTime.toString());
            }
        }
        return showTimeStrings;
    }

    // Helper method to convert String representations back to DateTime objects
    public void setShowTimesFromStrings(List<String> showTimeStrings) {
        List<DateTime> dateTimes = new ArrayList<>();
        if (showTimeStrings != null) {
            for (String timeStr : showTimeStrings) {
                dateTimes.add(new DateTime(timeStr));
            }
        }
        this.showTimes = dateTimes;
    }
}
