package com.example.moviebooking.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.moviebooking.dto.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SharedReferenceController {
    private static final String TABLE_ACCOUNT = "AccountsPref";
    private static final String TABLE_MOVIE = "MoviesPref";
    private static List<Movie> listMovies = null;
    private static final String PREF_SEATS = "SeatsPref";
    private static final String PREF_TICKETS = "TicketsPref";

    public static void saveMoviesFromList(Context context, List<Movie> movies) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TABLE_MOVIE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (Movie movie : movies) {
            editor.putString(movie.getMovieID(), movie.getMovieInfo());
            Log.d("saveMovies", movie.getMovieID());
        }

        editor.apply();
        getListMovies(context);
    }

    public static List<Movie> getListMovies(Context context) {
        if (listMovies != null)
            return listMovies;

        listMovies = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(TABLE_MOVIE, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            listMovies.add(new Movie(key, value.toString()));
        }

        return listMovies;
    }

    public static Boolean registerUser(Context context, String name, String username, String password, String confirmPassword) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TABLE_ACCOUNT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (username.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sharedPreferences.contains(username)) {
            Toast.makeText(context, "Username already exists. Choose another one.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            editor.putString(username, password);
            editor.apply();
            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public static Boolean loginUser(Context context, String username, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TABLE_ACCOUNT, Context.MODE_PRIVATE);

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!sharedPreferences.contains(username)) {
            Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!sharedPreferences.getString(username, "").equals(password)) {
            Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
