package com.example.moviebooking.moviepage;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebooking.R;
import com.example.moviebooking.dto.DateTime;

import java.util.List;

public class DateOfWeekAdapter extends RecyclerView.Adapter<DateOfWeekAdapter.DateTimeViewHolder> {
    private Context mContext;
    private List<DateTime> mListDateTime;
    public DateOfWeekAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void setData(List<DateTime> list) {
        this.mListDateTime = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DateTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false);
        return new DateTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateTimeViewHolder holder, final int position) {
        //holder.setIsRecyclable(false);
        DateTime dateTime = mListDateTime.get(position);

        if (dateTime == null) {
            return;
        }

        holder.bindData(dateTime);

        holder.relativeLayout.setOnClickListener(v -> {
            if (dateTime.isSelected()) {
                holder.relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                dateTime.setSelected(false);
                Log.d("DateOfWeekAdapter", "onBindViewHolder: " + mListDateTime.get(position).getDay() +
                        " " + holder.getBindingAdapterPosition() +
                        " " + position);
                notifyDataSetChanged();
            }
            else {
                holder.relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
                dateTime.setSelected(true);
                Log.d("DateOfWeekAdapter", "onBindViewHolder: " + mListDateTime.get(position).getDay() +
                        " " + holder.getBindingAdapterPosition() +
                        " " + position);
                for (int i = 0; i < mListDateTime.size(); i++) {
                    if (i != position) {
                        mListDateTime.get(i).setSelected(false);
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListDateTime != null) {
            return mListDateTime.size();
        }
        return 0;
    }

    public DateTime getSelectedDate() {
        for (DateTime dateTime : mListDateTime) {
            if (dateTime.isSelected()) {
                return dateTime;
            }
        }
        return null;
    }

    public class DateTimeViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout;
        private TextView day;
        private TextView dayOfWeek;
        public DateTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rl_calendar);
            day = itemView.findViewById(R.id.tv_day);
            dayOfWeek = itemView.findViewById(R.id.tv_dayOfWeek);
        }

        public void bindData(DateTime dateTime) {
            day.setText(dateTime.getDay());
            dayOfWeek.setText(dateTime.getDayOfWeek().toUpperCase().substring(0, 3));

            if (dateTime.isDisable()) {
                relativeLayout.setBackgroundResource(R.drawable.bg_disabled_date);
                day.setTextColor(mContext.getResources().getColor(R.color.yellow));
                dayOfWeek.setTextColor(mContext.getResources().getColor(R.color.white));
            } else
            if (dateTime.isSelected()) {
                relativeLayout.setBackgroundResource(R.drawable.bg_glow_orange);
                day.setTextColor(mContext.getResources().getColor(android.R.color.black));
                dayOfWeek.setTextColor(mContext.getResources().getColor(android.R.color.black));
            } else {
                relativeLayout.setBackgroundResource(R.drawable.bg_disabled_date);
                day.setTextColor(mContext.getResources().getColor(R.color.yellow));
                dayOfWeek.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        }
    }
}
