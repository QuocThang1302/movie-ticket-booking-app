package com.example.moviebooking.moviepage;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviebooking.R;
import com.example.moviebooking.data.HardcodingData;
import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.UserInfo;
import com.example.moviebooking.BookingActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class MoviePageActivity extends AppCompatActivity {
    private static final String USER_INFO_INTENT_KEY = "userinfoIntent";
    private static final String MOVIE_INTENT_KEY = "movie";

    private Movie receivedMovie;
    private UserInfo userInfo;
    private List<DateTime> dates;
    private DateOfWeekAdapter dateOfWeekAdapter;
    private List<DateTime> hours1;
    private List<DateTime> hours2;
    private HoursAdapter hours1Adapter;
    private HoursAdapter hours2Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_page);

        extractIntentData();
        if (receivedMovie == null || userInfo == null) {
            return; // not found
        }

        initializeUI();
        setOnClickForFABButtonAndBackButton();
        bindDataToMovieInfo();
        bindDataToDateList();
        bindDataToHourList1List2();
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra(USER_INFO_INTENT_KEY);
        receivedMovie = (Movie) intent.getSerializableExtra(MOVIE_INTENT_KEY);
    }

    private void initializeUI() {
        ImageView backButton = findViewById(R.id.iv_back_btn);
        backButton.setOnClickListener(v -> finish());

        com.google.android.material.floatingactionbutton.FloatingActionButton fab =
                findViewById(R.id.fab);
        fab.setOnClickListener(this::onFabClick);
    }

    private void onFabClick(View v) {
        DateTime selectedDate = dateOfWeekAdapter.getSelectedDate();
        DateTime selectedHour1 = hours1Adapter.getSelectedHour();
        DateTime selectedHour2 = hours2Adapter.getSelectedHour();

        if (selectedDate == null) {
            showToast("Please select date");
            return;
        }

        if (selectedHour1 == null && selectedHour2 == null) {
            showToast("Please select hour");
            return;
        }

        if (selectedHour1 != null && selectedHour2 != null) {
            showToast("Only 1 cinema per ticket");
            return;
        }

        startBookingActivity(selectedDate, selectedHour1, selectedHour2);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void startBookingActivity(DateTime selectedDate, DateTime selectedHour1, DateTime selectedHour2) {
        Intent intent = new Intent(MoviePageActivity.this, BookingActivity.class);
        intent.putExtra(USER_INFO_INTENT_KEY, userInfo);
        intent.putExtra(MOVIE_INTENT_KEY, receivedMovie);

        DateTime selectedHour = (selectedHour1 != null) ? selectedHour1 : selectedHour2;
        String selectedCinema = (selectedHour1 != null) ? getCinema1Text() : getCinema2Text();

        intent.putExtra("cinema", selectedCinema);
        intent.putExtra("datetime", selectedDate.setHoursFromDateTime(selectedHour));

        startActivity(intent);
    }

    private String getCinema1Text() {
        return ((TextView) findViewById(R.id.tv_cinema_1)).getText().toString();
    }

    private String getCinema2Text() {
        return ((TextView) findViewById(R.id.tv_cinema_2)).getText().toString();
    }

    private void setOnClickForFABButtonAndBackButton() {
        findViewById(R.id.iv_back_btn).setOnClickListener(v -> finish());
        findViewById(R.id.fab).setOnClickListener(this::onFabClick);
    }

    private void bindDataToMovieInfo() {
        RoundedImageView movieImage = findViewById(R.id.riv_movie_image);
        TextView movieTitle = findViewById(R.id.tv_movie_title);
        TextView movieDescription = findViewById(R.id.tv_movie_description);
        TextView movieDuration = findViewById(R.id.tv_movie_duration);
        TextView movieRating = findViewById(R.id.tv_movie_rate);
        TextView movieGenre = findViewById(R.id.tv_movie_genre);

        Glide.with(this).load(receivedMovie.getThumbnail()).into(movieImage);
        movieTitle.setText(receivedMovie.getTitle());
        movieDescription.setText(receivedMovie.getDescription());
        movieDuration.setText(receivedMovie.getDuration() + " mins");
        movieRating.setText(receivedMovie.getRate());
        movieGenre.setText(receivedMovie.getMainGenre());
    }

    private void bindDataToDateList() {
        RecyclerView dateRCV = findViewById(R.id.rcv_dates);
        dates = HardcodingData.getNextDates();
        dateOfWeekAdapter = new DateOfWeekAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        dateRCV.setLayoutManager(linearLayoutManager);

        dateOfWeekAdapter.setData(dates);
        dateRCV.setAdapter(dateOfWeekAdapter);
    }

    private void bindDataToHourList1List2() {
        RecyclerView hourRCV1 = (RecyclerView) findViewById(R.id.rcv_hours1);
        hours1 = HardcodingData.getHours();

        hours1Adapter = new HoursAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        hourRCV1.setLayoutManager(linearLayoutManager);
        hours1Adapter.setData(hours1);
        Log.d("", ": " + hours1Adapter.getItemCount());
        hourRCV1.setAdapter(hours1Adapter);

        //

        RecyclerView hourRCV2 = (RecyclerView) findViewById(R.id.rcv_hours2);
        hours2 = HardcodingData.getHours();

        hours2Adapter = new HoursAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        hourRCV2.setLayoutManager(linearLayoutManager);
        hours2Adapter.setData(hours2);
        Log.d("", ": " + hours2Adapter.getItemCount());
        hourRCV2.setAdapter(hours2Adapter);
    }
}