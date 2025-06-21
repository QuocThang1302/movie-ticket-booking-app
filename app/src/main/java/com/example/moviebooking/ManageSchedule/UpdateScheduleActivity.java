package com.example.moviebooking.ManageSchedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class UpdateScheduleActivity extends AppCompatActivity {

    private CheckBox chkActive;
    private RecyclerView showTimeListView;
    private Button btnAddTime, btnSaveSchedule;

    private ArrayList<DateTime> showTimesList = new ArrayList<>();
    private ArrayAdapter<String> showTimeAdapter;
    private Schedule currentSchedule;
    private Spinner spinnerMovie, spinnerCinema;
    private final List<Movie> movieList = new ArrayList<>();
    private final List<String> cinemaList = Arrays.asList("CINEMA_1", "CINEMA_2", "CINEMA_3");

    private ArrayAdapter<String> movieAdapter, cinemaAdapter;
    private String selectedMovieId = null;
    private String selectedCinemaId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_schedule);

        spinnerMovie = findViewById(R.id.spinnerMovie);
        spinnerCinema = findViewById(R.id.spinnerCinema);

        chkActive = findViewById(R.id.chkActive);
        showTimeListView = findViewById(R.id.showTimeListView);
        btnAddTime = findViewById(R.id.btnAddTime);
        btnSaveSchedule = findViewById(R.id.btnSaveSchedule);

        // Lấy schedule từ Intent
        currentSchedule = (Schedule) getIntent().getSerializableExtra("schedule");

        if (currentSchedule != null) {
            chkActive.setChecked(currentSchedule.isActive());
            showTimesList.addAll(currentSchedule.getShowTimes());
            setupMovieSpinner();
            setupCinemaSpinner();
        }

        showTimeListView.setLayoutManager(new LinearLayoutManager(this));
        showTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getFormattedShowTimes());
        RecyclerView.Adapter recyclerAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView textView = new TextView(parent.getContext());
                textView.setPadding(20, 20, 20, 20);
                return new RecyclerView.ViewHolder(textView) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView).setText(showTimeAdapter.getItem(position));
                holder.itemView.setOnLongClickListener(v -> {
                    new AlertDialog.Builder(UpdateScheduleActivity.this)
                            .setTitle("Remove the showtime")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Remove", (dialog, which) -> {
                                showTimesList.remove(position);
                                refreshShowTimeList();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                });
            }

            @Override
            public int getItemCount() {
                return showTimeAdapter.getCount();
            }
        };
        showTimeListView.setAdapter(recyclerAdapter);

        btnAddTime.setOnClickListener(view -> showDateTimePicker());

        btnSaveSchedule.setOnClickListener(view -> saveSchedule());
    }

    private void setupMovieSpinner() {
        movieAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        movieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMovie.setAdapter(movieAdapter);

        FireBaseManager.getInstance().fetchAllMoviesData(new FireBaseManager.OnMoviesDataLoadedListener() {
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

                for (int i = 0; i < movieList.size(); i++) {
                    if (movieList.get(i).getMovieID().equals(currentSchedule.getMovieId())) {
                        spinnerMovie.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onMoviesDataError(String error) {
                Toast.makeText(UpdateScheduleActivity.this, "Failed to load movies: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        spinnerMovie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        spinnerCinema.setAdapter(cinemaAdapter);

        int selectedIndex = cinemaList.indexOf(currentSchedule.getCinemaId());
        if (selectedIndex >= 0) {
            spinnerCinema.setSelection(selectedIndex);
        }

        spinnerCinema.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCinemaId = cinemaList.get(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                selectedCinemaId = null;
            }
        });
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                DateTime newDateTime = new DateTime("", dayOfMonth, month + 1, year, hourOfDay, minute);
                                showTimesList.add(newDateTime);
                                refreshShowTimeList();
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void refreshShowTimeList() {
        showTimeAdapter.clear();
        showTimeAdapter.addAll(getFormattedShowTimes());
        showTimeAdapter.notifyDataSetChanged();
        showTimeListView.getAdapter().notifyDataSetChanged();
    }

    private List<String> getFormattedShowTimes() {
        List<String> formatted = new ArrayList<>();
        for (DateTime dt : showTimesList) {
            formatted.add(dt.toString());
        }
        return formatted;
    }

    private void saveSchedule() {
        boolean isActive = chkActive.isChecked();

        if (selectedMovieId == null || selectedCinemaId == null || showTimesList.isEmpty()) {
            Toast.makeText(this, "Please fill in all the required information!", Toast.LENGTH_SHORT).show();
            return;
        }

        currentSchedule.setMovieId(selectedMovieId);
        currentSchedule.setCinemaId(selectedCinemaId);
        currentSchedule.setActive(isActive);
        currentSchedule.setShowTimes(showTimesList);

        FireBaseManager.updateSchedule(this, currentSchedule, (success, message, id) -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if (success) {
                finish();
            }
        });
    }
}

