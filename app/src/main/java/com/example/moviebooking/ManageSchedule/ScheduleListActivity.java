package com.example.moviebooking.ManageSchedule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.Schedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<Schedule> adapter;
    private List<Schedule> scheduleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        listView = findViewById(R.id.scheduleListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleList);
        listView.setAdapter(adapter);

        loadSchedules();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Schedule selected = scheduleList.get(position);
            Intent intent = new Intent(this, UpdateScheduleActivity.class);
            intent.putExtra("schedule", selected);
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Schedule selected = scheduleList.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Xoá lịch chiếu")
                    .setMessage("Bạn có chắc muốn xoá?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        FireBaseManager.deleteSchedule(this, selected.getScheduleId(), (success, msg, data) -> {
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                            if (success) loadSchedules();
                        });
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
            return true;
        });
    }

    private void loadSchedules() {
        FireBaseManager.fetchAllSchedules(new FireBaseManager.OnSchedulesDataLoadedListener() {
            @Override
            public void onSchedulesDataLoaded(List<Schedule> schedules) {
                scheduleList.clear();
                scheduleList.addAll(schedules);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onSchedulesDataError(String error) {
                Toast.makeText(ScheduleListActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
