package com.example.moviebooking.ManageSchedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moviebooking.R;
import com.example.moviebooking.dto.Schedule;
import com.example.moviebooking.dto.DateTime;

import java.util.List;

public class ScheduleAdapter extends ArrayAdapter<Schedule> {
    private Context context;
    private List<Schedule> scheduleList;

    public ScheduleAdapter(@NonNull Context context, @NonNull List<Schedule> scheduleList) {
        super(context, 0, scheduleList);
        this.context = context;
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false);
            holder = new ViewHolder();
            holder.tvScheduleId = convertView.findViewById(R.id.tvScheduleId);
            holder.tvMovieId = convertView.findViewById(R.id.tvMovieId);
            holder.tvCinemaId = convertView.findViewById(R.id.tvCinemaId);
            holder.tvShowTimes = convertView.findViewById(R.id.tvShowTimes);
            holder.tvStatus = convertView.findViewById(R.id.tvStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Schedule schedule = scheduleList.get(position);

        // Set data
        holder.tvScheduleId.setText("ID: " + (schedule.getScheduleId() != null ? schedule.getScheduleId() : "N/A"));
        holder.tvMovieId.setText("Movie: " + (schedule.getMovieId() != null ? schedule.getMovieId() : "N/A"));
        holder.tvCinemaId.setText("Cinema: " + (schedule.getCinemaId() != null ? schedule.getCinemaId() : "N/A"));

        // Format show times
        String showTimesText = formatShowTimes(schedule.getShowTimes());
        holder.tvShowTimes.setText("Show Times: " + showTimesText);

        // Set status with color
        if (schedule.isActive()) {
            holder.tvStatus.setText("Active");
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvStatus.setText("Inactive");
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        return convertView;
    }

    private String formatShowTimes(List<DateTime> showTimes) {
        if (showTimes == null || showTimes.isEmpty()) {
            return "No show times";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < showTimes.size(); i++) {
            DateTime dateTime = showTimes.get(i);
            if (dateTime != null) {
                // Sử dụng format đẹp hơn với AM/PM
                String formatted = String.format("%s %s/%s/%s %s",
                        dateTime.getDayOfWeek(),
                        dateTime.getDay(),
                        dateTime.getMonth(),
                        dateTime.getYear(),
                        dateTime.getTimeAMPM());
                sb.append(formatted);

                if (i < showTimes.size() - 1) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    static class ViewHolder {
        TextView tvScheduleId;
        TextView tvMovieId;
        TextView tvCinemaId;
        TextView tvShowTimes;
        TextView tvStatus;
    }
}