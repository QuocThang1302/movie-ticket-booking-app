package com.example.moviebooking.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebooking.booking.BookingHistoryActivity;
import com.example.moviebooking.R;
import com.example.moviebooking.dto.UserInfo;

import java.util.Arrays;
import java.util.List;

public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.ViewHolder> {
    private List<String> drawerItems;
    private UserInfo userInfo;
    private Context context;
    private OnLogoutClickListener logoutClickListener;

    public DrawerListAdapter(Context context, UserInfo userInfo, OnLogoutClickListener listener) {
        this.context = context;
        this.userInfo = userInfo;
        this.drawerItems = Arrays.asList("Username", "Booking History", "Logout");
        this.logoutClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_drawer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = drawerItems.get(position);

        if(position == 0) {
            holder.textViewItem.setText(userInfo.getUsername());
            holder.imageViewItem.setImageResource(R.drawable.icon_user_ava);
        } else if(position == 1) {
            holder.textViewItem.setText("Booking History");
            holder.imageViewItem.setImageResource(R.drawable.icon_history);

            holder.textViewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BookingHistoryActivity.class);
                    intent.putExtra("userinfoIntent", userInfo);
                    context.startActivity(intent);
                }
            });

        } else if (position == 2) {
            holder.textViewItem.setText("Logout");
            holder.imageViewItem.setImageResource(R.drawable.icon_logout);

            holder.textViewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(logoutClickListener != null) {
                        logoutClickListener.onLogoutClick();
                    }
                }
            });
        }

        customizeTextViewItem(position, holder.textViewItem);
    }

    @Override
    public int getItemCount() {
        return drawerItems.size();
    }

    private void customizeTextViewItem(int position, TextView textViewItem) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;
        ImageView imageViewItem;

        ViewHolder(View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.username);
            imageViewItem = itemView.findViewById(R.id.imgUserDrawer);
        }
    }
}
