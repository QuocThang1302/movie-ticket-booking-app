package com.example.moviebooking.data;

import android.util.Log;

import com.example.moviebooking.R;
import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Movie;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HardcodingData {
    private static List<Movie> moviesBarList = null;
    static public List<Movie> getFewMovies() {
        List<Movie> moviesBarList = new ArrayList<>();

        moviesBarList = getAllMovies().subList(0, 10);

        return moviesBarList;
    }

    static public List<Movie> getAllMovies() {
        if (moviesBarList != null) {
            return moviesBarList;
        }

        List<Movie> moviesBarList = new ArrayList<>();
        // public Movie(String movieID, int idResource, String title, String description, String imageUrl, String genre, ArrayList<String> type, int duration, double rate, String trailerYoutube)
        moviesBarList.add(new Movie("movieID0", "The Grudge", "The Grudge is a 2020 American psychological supernatural horror film written and directed by Nicolas Pesce. Originally announced as a reboot of the 2004 American remake and the original 2002 Japanese horror film Ju-On: The Grudge, the film ended up taking place before and during the events of the 2004 film and its two direct sequels, and is the fourth installment in the American The Grudge film series. The film stars Andrea Riseborough, Demi�n Bichir, John Cho, Betty Gilpin, Lin Shaye, and Jacki Weaver, and follows a police officer who investigates several murders that are seemingly connected to a single house.", "https://upload.wikimedia.org/wikipedia/en/3/34/The_Grudge_2020_Poster.jpeg", Arrays.asList("Horror","Supernatural"), "159", "3.3", ""));


        return moviesBarList;
    }

    static public List<DateTime> getNextDates() {
        List<DateTime> nextSevenDaysInfo = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM d");

        for (int i = 0; i < 7; i++) {
            LocalDate nextDay = currentDate.plusDays(i);

            int dayOfMonth = nextDay.getDayOfMonth();
            int monthValue = nextDay.getMonthValue();
            int yearValue = nextDay.getYear();
            DayOfWeek dayOfWeek = nextDay.getDayOfWeek();

            nextSevenDaysInfo.add(new DateTime(dayOfWeek.toString().substring(0, 3), dayOfMonth, monthValue, yearValue, 0, 0));
        }

        return nextSevenDaysInfo;
    }

    public static List<DateTime> getHours() {
        List<DateTime> hours = new ArrayList<>();

        hours.add(new DateTime("", 0, 0, 0, 9, 30));
        hours.add(new DateTime("", 0, 0, 0, 15, 00));
        hours.add(new DateTime("", 0, 0, 0, 18, 30));
        hours.add(new DateTime("", 0, 0, 0, 21, 00));

        return hours;
    }
}
