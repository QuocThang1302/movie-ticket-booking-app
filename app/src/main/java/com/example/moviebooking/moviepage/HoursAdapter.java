package com.example.moviebooking.moviepage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebooking.R;
import com.example.moviebooking.dto.DateTime;

import java.util.ArrayList;
import java.util.List;

public class HoursAdapter extends RecyclerView.Adapter<HoursAdapter.HoursViewHolder> {
    private Context mContext;
    private List<DateTime> mHoursTime;

    public HoursAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void setData(List<DateTime> list) {
        mHoursTime = new ArrayList<>(list);
        notifyDataSetChanged();
    }

//    public void setData(List<DateTime> list) {
//        mHoursTime = new ArrayList<>();
//
//        mHoursTime.add(new DateTime("", 0, 0, 0, 9, 30));
//        mHoursTime.add(new DateTime("", 0, 0, 0, 12, 30));
//        mHoursTime.add(new DateTime("", 0, 0, 0, 15, 00));
//        mHoursTime.add(new DateTime("", 0, 0, 0, 18, 30));
//        mHoursTime.add(new DateTime("", 0, 0, 0, 21, 00));
//
//        for (DateTime hours : mHoursTime) {
//            Boolean isExist = false;
//            for (DateTime item : list) {
//                if (hours.toString().equals(item.toString())) {
//                    isExist = true;
//                    break;
//                }
//            }
//            if (!isExist) {
//                hours.setDisable(true);
//            } else {
//                hours.setDisable(false);
//            }
//        }
//
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public HoursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hours_button, parent, false);
        return new HoursViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HoursViewHolder holder, int position) {
        DateTime dateTime = mHoursTime.get(position);

        if (dateTime == null) {
            return;
        }

        holder.bindData(dateTime);

        holder.btnHours.setOnClickListener(v -> {
            if (dateTime.isDisable()) {
                return;
            }
            if (dateTime.isSelected()) {
                holder.btnHours.setBackground(mContext.getResources().getDrawable(R.drawable.bg_glow_orange));
                //holder.btnHours.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
                mHoursTime.get(position).setSelected(false);
                notifyDataSetChanged();
            }
            else {
                holder.btnHours.setBackground(mContext.getResources().getDrawable(R.drawable.bg_disabled_date));
                //holder.btnHours.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                mHoursTime.get(position).setSelected(true);
                for (int i = 0; i < mHoursTime.size(); i++) {
                    if (i != position) {
                        mHoursTime.get(i).setSelected(false);
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mHoursTime != null) {
            return mHoursTime.size();
        }
        return 0;
    }

    public DateTime getSelectedHour() {
        for (DateTime dateTime : mHoursTime) {
            if (dateTime.isSelected()) {
                return dateTime;
            }
        }
        return null;
    }

    public class HoursViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout btnHours;
        private TextView tvHours;
        public HoursViewHolder(@NonNull View itemView) {
            super(itemView);
            btnHours = itemView.findViewById(R.id.rl_hours);
            tvHours = itemView.findViewById(R.id.tv_hours);
        }

        public void bindData(DateTime dateTime) {
            tvHours.setText(dateTime.getTimeAMPM());

            if (dateTime.isDisable()) {
                btnHours.setClickable(false);
                btnHours.setBackground(mContext.getResources().getDrawable(R.drawable.button_hours_style_disabled));
                tvHours.setTextColor(mContext.getResources().getColor(R.color.disable_text));
            } else
            if (dateTime.isSelected()) {
                btnHours.setBackground(mContext.getResources().getDrawable(R.drawable.bg_glow_orange));
                tvHours.setTextColor(mContext.getResources().getColor(R.color.black));
                //btnHours.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            } else {
                btnHours.setBackground(mContext.getResources().getDrawable(R.drawable.bg_disabled_hours));
                tvHours.setTextColor(mContext.getResources().getColor(R.color.white));
                //btnHours.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
        }
    }
}
