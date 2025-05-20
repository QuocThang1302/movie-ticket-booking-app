package com.example.moviebooking.ui.app.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.moviebooking.R;
import com.example.moviebooking.booking.BookingStatusActivity;
import com.example.moviebooking.dto.BookedTicketList;
import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.UserInfo;
import com.makeramen.roundedimageview.RoundedImageView;

public class SnackSelectionActivity extends AppCompatActivity {

    private UserInfo userInfo;
    private Movie receivedMovie;
    private DateTime selectedDateTime;
    private String cinemaName;
    private BookedTicketList bookedTicketList;

    private int selectedSnackCount = 0;
    private double ticketPrice; // Tổng giá vé từ BookingActivity
    private double snackPrice = 0; // Tổng giá combo snack
    private TextView tvSnackCount;
    private TextView tvTotalPrice;

    // Giá của các combo
    private static final double PRICE_COMBO1 = 5.0;
    private static final double PRICE_COMBO2 = 7.0;
    private static final double PRICE_COMBO3 = 9.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snack_selection);

        extractIntentData();
        initializeUI();
        setDataForFilmView();
        updatePaymentInfo();
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra("userinfoIntent");
        receivedMovie = (Movie) intent.getSerializableExtra("movie");
        selectedDateTime = (DateTime) intent.getSerializableExtra("datetime");
        cinemaName = intent.getStringExtra("cinema");
        bookedTicketList = (BookedTicketList) intent.getSerializableExtra("bookedTicketList");

        if (receivedMovie == null || selectedDateTime == null || userInfo == null || bookedTicketList == null) {
            finish();
            return;
        }

        // Tính tổng giá vé từ danh sách vé
        ticketPrice = bookedTicketList.getBookedTicketList().size() * 10.0; // Giả sử mỗi vé $10
    }

    private void initializeUI() {
        ImageView backButton = findViewById(R.id.iv_back_btn);
        backButton.setOnClickListener(v -> finish());

        com.google.android.material.floatingactionbutton.FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::handleFabButtonClick);

        tvSnackCount = findViewById(R.id.tv_ticket_count); // Thay thế tv_drink_count
        tvTotalPrice = findViewById(R.id.tv_total_price);

        // Sự kiện click cho các combo snack
        findViewById(R.id.rl_combo1).setOnClickListener(v -> selectCombo("Combo 1: Small Popcorn + Coke", PRICE_COMBO1)); // Thay thế rl_drink1
        findViewById(R.id.rl_combo2).setOnClickListener(v -> selectCombo("Combo 2: Medium Popcorn + Pepsi", PRICE_COMBO2)); // Thay thế rl_drink2
        findViewById(R.id.rl_combo3).setOnClickListener(v -> selectCombo("Combo 3: Large Popcorn + Water", PRICE_COMBO3)); // Thay thế rl_drink3
    }

    private void selectCombo(String comboName, double price) {
        Toast.makeText(this, "Selected: " + comboName, Toast.LENGTH_SHORT).show();
        selectedSnackCount++;
        snackPrice += price;
        updatePaymentInfo();
    }

    private void updatePaymentInfo() {
        tvSnackCount.setText("x" + selectedSnackCount);
        double totalPrice = ticketPrice + snackPrice;
        tvTotalPrice.setText("$" + String.format("%.2f", totalPrice));
    }

    private void setDataForFilmView() {
        RoundedImageView movieImage = findViewById(R.id.riv_movie_image);
        TextView movieTitle = findViewById(R.id.tv_movie_title); // Thay thế tv_drink_title
        TextView ticketDate = findViewById(R.id.tv_date);
        TextView ticketTime = findViewById(R.id.tv_hours);
        TextView ticketCinema = findViewById(R.id.tv_cinema_name);

        Glide.with(this).load(receivedMovie.getThumbnail()).into(movieImage);
        movieTitle.setText(receivedMovie.getTitle());
        ticketDate.setText(selectedDateTime.getShortDate());
        ticketTime.setText(selectedDateTime.getTimeAMPM());
        ticketCinema.setText(cinemaName);
    }

    private void handleFabButtonClick(View v) {
        // Chuyển hướng sang BookingStatusActivity
        Intent intent = new Intent(this, BookingStatusActivity.class);
        intent.putExtra("movie", receivedMovie);
        intent.putExtra("bookedTicketList", bookedTicketList);
        intent.putExtra("userinfoIntent", userInfo);
        intent.putExtra("totalPrice", ticketPrice + snackPrice); // Truyền tổng giá
        startActivity(intent);
        finish();
    }
}