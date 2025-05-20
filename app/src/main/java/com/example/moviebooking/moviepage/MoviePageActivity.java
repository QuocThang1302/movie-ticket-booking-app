package com.example.moviebooking.moviepage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.data.HardcodingData;
import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.Schedule;
import com.example.moviebooking.dto.UserInfo;
import com.example.moviebooking.booking.BookingActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MoviePageActivity extends AppCompatActivity {
    private static final String USER_INFO_INTENT_KEY = "userinfoIntent";
    private static final String MOVIE_INTENT_KEY = "movie";
    private List<Schedule> schedules = new ArrayList<>();
    private Map<String, List<String>> dateToHoursMap = new LinkedHashMap<>(); // Map ngày → danh sách giờ
    private String selectedDate = null;
    private Movie receivedMovie;
    private UserInfo userInfo;
    private List<DateTime> dates;
    private DateOfWeekAdapter dateOfWeekAdapter;
    private List<DateTime> hours1;
    private List<DateTime> hours2;
    private HoursAdapter hours1Adapter;
    private HoursAdapter hours2Adapter;

    private boolean isExpanded = false;
    private TextView movieDescription;
    private TextView trailerTextView;
    private ImageView expandButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_page);

        extractIntentData();
        if (receivedMovie == null || userInfo == null) {
            return;
        }
        String movieId = receivedMovie.getMovieID();
        trailerTextView = findViewById(R.id.tv_trailer);
        trailerTextView.setOnClickListener(v -> openYoutubeTrailer());
        initializeUI();
        setOnClickForFABButtonAndBackButton();
        bindDataToMovieInfo();
        bindDataToDateList(movieId);
        //bindDataToHourList1List2();
    }
    private void openYoutubeTrailer() {
        if (receivedMovie == null || receivedMovie.getTrailerYoutube() == null) {
            showToast("Trailer is not available");
            return;
        }

        String trailerUrl = receivedMovie.getTrailerYoutube();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
        intent.setPackage("com.google.android.youtube");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

            intent.setPackage(null);
            startActivity(intent);
        }
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

        movieDescription = findViewById(R.id.tv_movie_description);
        expandButton = findViewById(R.id.iv_expand);

        expandButton.setOnClickListener(v -> {
            Log.d("MoviePageActivity", "iv_expand clicked, isExpanded: " + isExpanded); // Debug
            toggleDescription();
        });
    }

    private void toggleDescription() {
        if (isExpanded) {
            movieDescription.setMaxHeight((int) (130 * getResources().getDisplayMetrics().density));
            expandButton.setBackgroundResource(R.drawable.button_expand);
            isExpanded = false;
        } else {
            movieDescription.setMaxHeight(Integer.MAX_VALUE);
            expandButton.setBackgroundResource(R.drawable.button_collapse); // Cần drawable này
            isExpanded = true;
        }
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
        movieDescription = findViewById(R.id.tv_movie_description);
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

    private void bindDataToDateList(String movieId) {
        RecyclerView dateRCV = findViewById(R.id.rcv_dates);
        dateOfWeekAdapter = new DateOfWeekAdapter(this);
        dateRCV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        dateRCV.setAdapter(dateOfWeekAdapter);

        FireBaseManager.fetchSchedulesByMovie(movieId, new FireBaseManager.OnSchedulesDataLoadedListener() {
            @Override
            public void onSchedulesDataLoaded(List<Schedule> schedules) {
                Set<LocalDate> uniqueDates = new TreeSet<>();
                for (Schedule schedule : schedules) {
                    for (DateTime dateTime : schedule.getShowTimes()) {
                        uniqueDates.add(dateTime.toLocalDate());
                    }
                }

                List<DateTime> dateList = new ArrayList<>();
                for (LocalDate date : uniqueDates) {
                    dateList.add(new DateTime(date));
                }

                dateOfWeekAdapter.setData(dateList);

                // Auto-select the first date and bind hours
                if (!dateList.isEmpty()) {
                    dateList.get(0).setSelected(true);
                    dateOfWeekAdapter.notifyDataSetChanged();
                    bindDataToHourList1List2(schedules, dateList.get(0));
                }

                // Lắng nghe khi chọn ngày mới
                dateOfWeekAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        DateTime selectedDate = dateOfWeekAdapter.getSelectedDate();
                        if (selectedDate != null) {
                            bindDataToHourList1List2(schedules, selectedDate);
                        }
                    }
                });
            }

            @Override
            public void onSchedulesDataError(String errorMessage) {
                Log.e("Firebase", "Error loading schedules: " + errorMessage);
            }
        });
    }



    private void bindDataToHourList1List2(List<Schedule> schedules, DateTime selectedDate) {
        RecyclerView hourRCV1 = findViewById(R.id.rcv_hours1);
        RecyclerView hourRCV2 = findViewById(R.id.rcv_hours2);

        hours1Adapter = new HoursAdapter(this);
        hours2Adapter = new HoursAdapter(this);

        hourRCV1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        hourRCV2.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        hourRCV1.setAdapter(hours1Adapter);
        hourRCV2.setAdapter(hours2Adapter);

        List<DateTime> matchingTimes = new ArrayList<>();
        for (Schedule schedule : schedules) {
            for (DateTime dateTime : schedule.getShowTimes()) {
                if (dateTime.toLocalDate().equals(selectedDate.toLocalDate())) {
                    matchingTimes.add(dateTime);
                }
            }
        }

        // Chia đều danh sách giờ chiếu giữa hai RecyclerView
        int mid = matchingTimes.size() / 2;
        List<DateTime> part1 = matchingTimes.subList(0, mid);
        List<DateTime> part2 = matchingTimes.subList(mid, matchingTimes.size());

        hours1Adapter.setData(part1);
        hours2Adapter.setData(part2);
    }

    //Thử nghiệm

    // Thử nghiệm
}