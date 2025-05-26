package com.example.moviebooking.booking;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.UserInfo;
import com.example.moviebooking.dto.Ticket;

import java.util.List;
import com.example.moviebooking.home.TicketListAdapter;


public class BookingHistoryActivity extends AppCompatActivity {
    private UserInfo userInfo = null;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.moviebooking.R.layout.activity_booking_history);
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfoIntent");
        TextView username = findViewById(R.id.tv_user_name);
        username.setText(userInfo.getName());
        ImageView avaImg = findViewById(R.id.booking_history_ava);
        if (userInfo.getProfilePic() != null && !userInfo.getProfilePic().isEmpty()) {
            Glide.with(this)
                    .load(userInfo.getProfilePic())
                    .apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop())
                    .into(avaImg);
        } else {
            avaImg.setImageResource(R.drawable.icon_user_ava);
        }

        Log.d("BookingHistoryActivity", "onCreate: " + userInfo.getUsername());

        fetchDataForTicketHistory(userInfo);
    }

    private void fetchDataForTicketHistory(UserInfo userInfo) {
        FireBaseManager firebaseManager = FireBaseManager.getInstance();
        firebaseManager.fetchUserTickets(userInfo, new FireBaseManager.OnBookedSeatsLoadedListener() {
            @Override
            public void onBookedSeatsLoaded(List<Ticket> tickets) {
                if (tickets != null) {
                    tickets.sort((o1, o2) -> o2.getBookedTime().compareTo(o1.getBookedTime()));
                    Log.d("BookingHistoryActivity", "onBookedSeatsLoaded: " + tickets.size());
                    initRecyclerView(tickets);
                }
            }
            @Override
            public void onBookedSeatsError(String errorMessage) {
            }
        });
    }

    private void initRecyclerView(List<Ticket> tickets) {
        RecyclerView historyTicketView = findViewById(R.id.rcv_ticket_history);
        TicketListAdapter adapter = new TicketListAdapter(this, userInfo, tickets);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        historyTicketView.setLayoutManager(linearLayoutManager);

        historyTicketView.setAdapter(adapter);
    }
}
