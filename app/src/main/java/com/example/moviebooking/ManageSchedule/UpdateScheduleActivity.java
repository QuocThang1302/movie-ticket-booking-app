package com.example.moviebooking.ManageSchedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateScheduleActivity extends AppCompatActivity {

    private EditText edtMovieId, edtCinemaId;
    private CheckBox chkActive;
    private ListView showTimeListView;
    private Button btnAddTime, btnSaveSchedule;

    private ArrayList<DateTime> showTimesList = new ArrayList<>();
    private ArrayAdapter<String> showTimeAdapter;
    private Schedule currentSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_schedule);

        edtMovieId = findViewById(R.id.edtMovieId);
        edtCinemaId = findViewById(R.id.edtCinemaId);
        chkActive = findViewById(R.id.chkActive);
        showTimeListView = findViewById(R.id.showTimeListView);
        btnAddTime = findViewById(R.id.btnAddTime);
        btnSaveSchedule = findViewById(R.id.btnSaveSchedule);

        // Lấy schedule từ Intent
        currentSchedule = (Schedule) getIntent().getSerializableExtra("schedule");

        if (currentSchedule != null) {
            edtMovieId.setText(currentSchedule.getMovieId());
            edtCinemaId.setText(currentSchedule.getCinemaId());
            chkActive.setChecked(currentSchedule.isActive());

            showTimesList.addAll(currentSchedule.getShowTimes());
        }

        showTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getFormattedShowTimes());
        showTimeListView.setAdapter(showTimeAdapter);

        // Thêm giờ chiếu mới
        btnAddTime.setOnClickListener(view -> showDateTimePicker());

        // Xoá giờ chiếu bằng long click
        showTimeListView.setOnItemLongClickListener((adapterView, view, position, id) -> {
            new AlertDialog.Builder(this)
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

        // Lưu lại lịch chiếu
        btnSaveSchedule.setOnClickListener(view -> saveSchedule());
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
    }

    private List<String> getFormattedShowTimes() {
        List<String> formatted = new ArrayList<>();
        for (DateTime dt : showTimesList) {
            formatted.add(dt.toString());
        }
        return formatted;
    }

    private void saveSchedule() {
        String movieId = edtMovieId.getText().toString().trim();
        String cinemaId = edtCinemaId.getText().toString().trim();
        boolean isActive = chkActive.isChecked();

        if (movieId.isEmpty() || cinemaId.isEmpty() || showTimesList.isEmpty()) {
            Toast.makeText(this, "Please fill in all the required information!", Toast.LENGTH_SHORT).show();
            return;
        }

        currentSchedule.setMovieId(movieId);
        currentSchedule.setCinemaId(cinemaId);
        currentSchedule.setActive(isActive);
        currentSchedule.setShowTimes(showTimesList);

        FireBaseManager.updateSchedule(this, currentSchedule, (success, message, id) -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if (success) {
                finish(); // Quay lại màn hình trước
            }
        });
    }
}

