package com.example.moviebooking.ManageSchedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Updated AddScheduleActivity.java
public class AddScheduleActivity extends AppCompatActivity {

    private Spinner movieSpinner, cinemaSpinner;
    private Button btnPickDate, btnPickTime, btnAddSchedule, btnAddTimeSlot;
    private TextView txtDateTime;
    private ListView listViewTimes;

    private final List<Movie> movieList = new ArrayList<>();
    private final List<String> cinemaList = Arrays.asList("CINEMA_1", "CINEMA_2", "CINEMA_3");
    private final List<DateTime> showTimes = new ArrayList<>();

    private ArrayAdapter<String> movieAdapter, cinemaAdapter;
    private ArrayAdapter<String> timesAdapter;

    private String selectedMovieId = null;
    private String selectedCinemaId = null;

    private LocalDate selectedDate;
    private LocalTime selectedTime;

    private final FireBaseManager firebaseManager = FireBaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        movieSpinner = findViewById(R.id.spinnerMovie);
        cinemaSpinner = findViewById(R.id.spinnerCinema);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        txtDateTime = findViewById(R.id.txtDateTime);
        btnAddTimeSlot = findViewById(R.id.btnAddTimeSlot);
        btnAddSchedule = findViewById(R.id.btnAddSchedule);
        listViewTimes = findViewById(R.id.listViewTimes);

        setupMovieSpinner();
        setupCinemaSpinner();
        setupDateTimePickers();

        timesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listViewTimes.setAdapter(timesAdapter);

        btnAddTimeSlot.setOnClickListener(v -> addTimeSlot());

        btnAddSchedule.setOnClickListener(v -> addSchedule());
    }

    private void setupMovieSpinner() {
        movieAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        movieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        movieSpinner.setAdapter(movieAdapter);

        firebaseManager.fetchAllMoviesData(new FireBaseManager.OnMoviesDataLoadedListener() {
            @Override
            public void onMoviesDataLoaded(List<Movie> movies) {
                movieList.clear();
                movieList.addAll(movies);
                List<String> movieTitles = new ArrayList<>();
                for (Movie m : movies) {
                    movieTitles.add(m.getTitle());
                }
                movieAdapter.clear();
                movieAdapter.addAll(movieTitles);
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMoviesDataError(String error) {
                Toast.makeText(AddScheduleActivity.this, "Failed to load movies: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        movieSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMovieId = movieList.get(position).getMovieID();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                selectedMovieId = null;
            }
        });
    }

    private void setupCinemaSpinner() {
        cinemaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cinemaList);
        cinemaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cinemaSpinner.setAdapter(cinemaAdapter);

        cinemaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCinemaId = cinemaList.get(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                selectedCinemaId = null;
            }
        });
    }

    private void setupDateTimePickers() {
        btnPickDate.setOnClickListener(v -> {
            LocalDate now = LocalDate.now();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                        updateDateTimeDisplay();
                    }, now.getYear(), now.getMonthValue() - 1, now.getDayOfMonth());
            datePickerDialog.show();
        });

        btnPickTime.setOnClickListener(v -> {
            LocalTime now = LocalTime.now();
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        selectedTime = LocalTime.of(hourOfDay, minute);
                        updateDateTimeDisplay();
                    }, now.getHour(), now.getMinute(), true);
            timePickerDialog.show();
        });
    }

    private void updateDateTimeDisplay() {
        if (selectedDate != null && selectedTime != null) {
            txtDateTime.setText("Selected: " + selectedDate.toString() + " " + selectedTime.toString());
        }
    }

    private void addTimeSlot() {
        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Please select both date and time", Toast.LENGTH_SHORT).show();
            return;
        }
        DateTime dateTime = new DateTime(selectedDate);
        dateTime.setHoursFromDateTime(new DateTime(selectedDate.getDayOfWeek().toString(),
                selectedDate.getDayOfMonth(),
                selectedDate.getMonthValue(),
                selectedDate.getYear(),
                selectedTime.getHour(),
                selectedTime.getMinute()));

        showTimes.add(dateTime);
        timesAdapter.add(dateTime.toString());
        timesAdapter.notifyDataSetChanged();
    }

    private void addSchedule() {
        if (selectedMovieId == null || selectedCinemaId == null || showTimes.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Schedule schedule = new Schedule(selectedMovieId, selectedCinemaId, showTimes);

        FireBaseManager.addSchedule(this, schedule, (success, message, id) -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if (success) {
                finish();
            }
        });
    }
}
