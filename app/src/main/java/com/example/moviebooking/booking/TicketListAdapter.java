package com.example.moviebooking.booking;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviebooking.R;
import com.example.moviebooking.dto.Ticket;
import com.example.moviebooking.dto.UserInfo;

import java.util.List;

public class TicketListAdapter extends RecyclerView.Adapter<TicketListAdapter.TicketViewHolder> {
    private Context mContext;
    private UserInfo userInfo;
    private List<Ticket> mListTicket;
    public TicketListAdapter(Context mContext, UserInfo userInfo, List<Ticket> mListTicket) {
        this.mContext = mContext;
        this.mListTicket = mListTicket;
        this.userInfo = userInfo;
    }

    public void setData(List<Ticket> list) {
        this.mListTicket = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = mListTicket.get(position);
        if (ticket == null) {
            return;
        }

        // set image resource by picasso and resize image to 50x50
        Glide.with(mContext).load(ticket.getThumbnail()).centerCrop().override(50, 50).into(holder.imgTicket);
        holder.tvMovieName.setText(ticket.getMovieName());
        holder.tvDateTime.setText(ticket.getTicketDateTime());
        holder.tvCinema.setText(ticket.getCinemaId());
        holder.tvSeat.setText("Seat: " + ticket.getSeatId());
        holder.tvCost.setText(ticket.getCost());
        holder.tvBookingTime.setText(ticket.getBookedTime());
    }

    @Override
    public int getItemCount() {
        if (mListTicket != null) {
            return mListTicket.size();
        }
        return 0;
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgTicket;
        private TextView tvMovieName;
        private TextView tvDateTime;
        private TextView tvCinema;
        private TextView tvSeat;
        private TextView tvCost;
        private TextView tvBookingTime;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);

            imgTicket = itemView.findViewById(R.id.imgUser);
            tvMovieName = itemView.findViewById(R.id.tv_movie_name);
            tvDateTime = itemView.findViewById(R.id.tv_datetime);
            tvCinema = itemView.findViewById(R.id.tv_cinema);
            tvSeat = itemView.findViewById(R.id.tv_seats);
            tvCost = itemView.findViewById(R.id.tv_price);
            tvBookingTime = itemView.findViewById(R.id.tv_booking_time);
        }
    }
}
