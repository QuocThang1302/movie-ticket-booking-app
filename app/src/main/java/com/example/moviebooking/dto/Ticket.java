package com.example.moviebooking.dto;

import java.io.Serializable;

public class Ticket implements Serializable {
    private String bookedTime;
    private String thumbnail;
    private String id;
    private String userId;
    private String movieId;
    private String movieName;
    private String cinemaId;
    private String dateTime;
    private String seatId;
    private Boolean isPaid = false;
    private String cost = "10$";

    public Ticket() {
    }

    public Ticket(String id, String userId, String movieId, String cinemaId, String dateTime, String seatId, Boolean isPaid) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.cinemaId = cinemaId;
        this.dateTime = dateTime;
        this.seatId = seatId;
        this.isPaid = isPaid;
    }

    public Ticket(String id, String userId, String movieId, String cinemaId, String dateTime, String seatId, Boolean isPaid, String bookedTime, String cost) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.cinemaId = cinemaId;
        this.dateTime = dateTime;
        this.seatId = seatId;
        this.isPaid = isPaid;
        this.cost = cost;
        this.bookedTime = bookedTime;
    }

    public Ticket(UserInfo userInfo, Movie movie, String cinema, DateTime dateTime, Seat seat, Boolean isPaid) {
        this.userId = userInfo.getUsername();
        this.movieId = movie.getMovieID();
        this.movieName = movie.getTitle();
        this.thumbnail = movie.getThumbnail();
        this.cinemaId = cinema;
        this.dateTime = dateTime.toString();
        this.seatId = seat.getSeatId();
        this.id = "";
        this.isPaid = isPaid;
    }

    public Ticket(String id, UserInfo userInfo, Movie movie, String cinema, DateTime dateTime, Seat seat, Boolean isPaid) {
        this.userId = userInfo.getUsername();
        this.movieId = movie.getMovieID();
        this.movieName = movie.getTitle();
        this.thumbnail = movie.getThumbnail();
        this.cinemaId = cinema;
        this.dateTime = dateTime.toString();
        this.seatId = seat.getSeatId();
        this.id = id;
        this.isPaid = isPaid;
    }

    public String getBookedTime() {
        return bookedTime;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getCost() {
        return cost;
    }

    public void setBookedTime(String bookedTime) {
        this.bookedTime = bookedTime;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean isPaid() {return isPaid;}

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", movieId='" + movieId + '\'' +
                ", cinemaId='" + cinemaId + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", seatId='" + seatId + '\'' +
                ", isPaid=" + isPaid +
                '}';
    }

    public String getPayment() {
        return "10$";
    }

    public String getTicketDateTime() {
        String[] part = dateTime.split("-");
        return part[0].substring(0, 3)+ "-" + part[1] + "-" + part[2];
    }
}
