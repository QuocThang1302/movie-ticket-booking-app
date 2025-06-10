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
import com.example.moviebooking.ReviewFilmActivity;
import com.example.moviebooking.Trailer.TrailerDialogFragment;
import com.example.moviebooking.data.FireBaseManager;
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
    private List<Schedule> currentSchedules = new ArrayList<>();
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
    private TextView reviewTextView;

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
        reviewTextView = findViewById(R.id.tv_review);
        reviewTextView.setOnClickListener(v -> {
            try {
                // Kiểm tra movie object trước khi chuyển
                if (receivedMovie != null) {
                    Intent intent = new Intent(MoviePageActivity.this, ReviewFilmActivity.class);
                    intent.putExtra("movie", receivedMovie);

                    // Thêm flags để tối ưu performance
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);

                    // Thêm animation transition mượt mà
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } else {
                    Toast.makeText(MoviePageActivity.this, "Movie data not available", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("MoviePageActivity", "Error starting ReviewFilmActivity: " + e.getMessage());
                Toast.makeText(MoviePageActivity.this, "Error opening reviews", Toast.LENGTH_SHORT).show();
            }
        });

        trailerTextView.setOnClickListener(v -> openYoutubeTrailer());
        initializeUI();
        setOnClickForFABButtonAndBackButton();
        bindDataToMovieInfo();
        bindDataToDateList(movieId);
        //bindDataToHourList1List2();
    }
    private void openYoutubeTrailer() {
        new TrailerDialogFragment(receivedMovie.getTrailerYoutube()).show(getSupportFragmentManager(), "TrailerDialog");

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


        if (selectedDate == null) {
            showToast("Please select date");
            return;
        }

        if (selectedHour1 == null ) {
            showToast("Please select hour");
            return;
        }

        startBookingActivity(selectedDate, selectedHour1);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void startBookingActivity(DateTime selectedDate, DateTime selectedHour1) {
        Intent intent = new Intent(MoviePageActivity.this, BookingActivity.class);
        intent.putExtra(USER_INFO_INTENT_KEY, userInfo);
        intent.putExtra(MOVIE_INTENT_KEY, receivedMovie);

        DateTime selectedHour = selectedHour1;
        String selectedCinema = findCinemaIdForSelectedDateTime(selectedDate, selectedHour1);

        intent.putExtra("cinema", selectedCinema);
        intent.putExtra("datetime", selectedDate.setHoursFromDateTime(selectedHour));

        startActivity(intent);
    }
    private String findCinemaIdForSelectedDateTime(DateTime selectedDate, DateTime selectedHour) {
        for (Schedule schedule : currentSchedules) {
            for (DateTime showTime : schedule.getShowTimes()) {
                if (showTime.toLocalDate().equals(selectedDate.toLocalDate()) &&
                        showTime.getHour() == selectedHour.getHour() &&
                        showTime.getMinute() == selectedHour.getMinute()) {
                    return schedule.getCinemaId();
                }
            }
        }
        // Fallback nếu không tìm thấy
        return currentSchedules.isEmpty() ? "" : currentSchedules.get(0).getCinemaId();
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
                // Get current date and next 7 days
                currentSchedules = schedules;
                LocalDate today = LocalDate.now();
                LocalDate endDate = today.plusDays(6); // Next 7 days (including today)

                Set<LocalDate> uniqueDates = new TreeSet<>();
                for (Schedule schedule : schedules) {
                    for (DateTime dateTime : schedule.getShowTimes()) {
                        LocalDate scheduleDate = dateTime.toLocalDate();

                        // Only add dates that fall within the next 7 days
                        if (!scheduleDate.isBefore(today) && !scheduleDate.isAfter(endDate)) {
                            uniqueDates.add(scheduleDate);
                        }
                    }
                }

                // If no schedules available in the next 7 days, don't display anything
                if (uniqueDates.isEmpty()) {
                    Log.d("DateList", "No schedules available in the next 7 days");
                    dateOfWeekAdapter.setData(new ArrayList<>());
                    return;
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

                // Listen for date selection changes
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
        hours1Adapter = new HoursAdapter(this);
        hourRCV1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        hourRCV1.setAdapter(hours1Adapter);

        List<DateTime> matchingTimes = new ArrayList<>();
        for (Schedule schedule : schedules) {
            for (DateTime dateTime : schedule.getShowTimes()) {
                if (dateTime.toLocalDate().equals(selectedDate.toLocalDate())) {
                    matchingTimes.add(dateTime);
                }
            }
        }

        // Gán toàn bộ danh sách giờ chiếu vào RecyclerView 1
        hours1Adapter.setData(matchingTimes);
    }

    //Thử nghiệm

    // Thử nghiệm
}