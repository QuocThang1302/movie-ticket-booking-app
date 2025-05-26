package com.example.moviebooking.booking;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebooking.R;

public class SnackViewHolder extends RecyclerView.ViewHolder {
    public final RecyclerView recyclerView;

    public SnackViewHolder(@NonNull View itemView) {
        super(itemView);
        recyclerView = itemView.findViewById(R.id.recycler_snack_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
    }
} 