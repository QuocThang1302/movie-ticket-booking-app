package com.example.moviebooking.booking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.BookedTicketList;
import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.Seat;
import com.example.moviebooking.dto.Ticket;
import com.example.moviebooking.dto.UserInfo;
import com.example.moviebooking.HomeActivity;
import com.example.moviebooking.home.SaveViewAsImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BookingStatusActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE = 1;
    private String movieTitle, cinema, date, time;
    private UserInfo userInfo;
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.moviebooking.R.layout.activity_booking_status);
        extractIntentData();
        initUI();
        setOnClickListeners();
        Intent intent = getIntent();
        BookedTicketList bookedTicketList = (BookedTicketList) intent.getSerializableExtra("bookedTicketList");
        if (bookedTicketList == null) {
            return;
        }
        Movie movie = intent.getSerializableExtra("movie") != null ? (Movie) intent.getSerializableExtra("movie") : null;

        createTicketsCard(bookedTicketList, movie);
    }
    private void extractIntentData() {
        Intent intent = getIntent();
        movieTitle = intent.getStringExtra("movieTitle");
        cinema = intent.getStringExtra("cinema");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        userInfo = (UserInfo) intent.getSerializableExtra("userinfoIntent");
    }

    private void initUI() {
        TextView tvMovieTitle = findViewById(R.id.tv_movie_title);
        TextView tvCinemaName = findViewById(R.id.tv_cinema_name);
        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvHours = findViewById(R.id.tv_hours);

        tvMovieTitle.setText(movieTitle);
        tvCinemaName.setText(cinema);
        tvDate.setText(date);
        tvHours.setText(time);



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("userinfoIntent", (UserInfo) getIntent().getSerializableExtra("userinfoIntent"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void setOnClickListeners() {
        findViewById(com.example.moviebooking.R.id.icon_home).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("userinfoIntent", (UserInfo) getIntent().getSerializableExtra("userinfoIntent"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        });


        findViewById(com.example.moviebooking.R.id.tv_history).setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingHistoryActivity.class);
            intent.putExtra("userinfoIntent", (UserInfo) getIntent().getSerializableExtra("userinfoIntent"));
            startActivity(intent);

        });

        findViewById(com.example.moviebooking.R.id.btn_save_to_gallery).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkWriteStoragePermission();
            }
            SaveViewAsImage.saveViewAsImage(this, findViewById(com.example.moviebooking.R.id.card_view));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }

    private void createTicketsCard(BookedTicketList bookedTicketList, Movie movie) {
        List<Ticket> ticketList = bookedTicketList.getBookedTicketList();

        ImageView movieImg = findViewById(com.example.moviebooking.R.id.movie_img);
        TextView movie_name = findViewById(R.id.tv_movie_title);
        TextView ticket_cinema = findViewById(R.id.tv_cinema_name);
        TextView ticket_date_value = findViewById(R.id.tv_date);
        TextView ticket_hour_value = findViewById(R.id.tv_hours);
        TextView ticket_seat_value = findViewById(R.id.tv_seats);
        TextView booking_code_value = findViewById(com.example.moviebooking.R.id.booking_code_value);
        ImageView qr_code = findViewById(com.example.moviebooking.R.id.qr_code);

        Glide.with(this).load(movie.getThumbnail()).into(movieImg);
        movie_name.setText(movie.getTitle());
        ticket_cinema.setText(bookedTicketList.getCinemaName());
        ticket_date_value.setText(bookedTicketList.getDate());
        if (bookedTicketList.countTicket() > 3) {
            ticket_date_value.setTextSize(12);
        }
        ticket_hour_value.setText(bookedTicketList.getHour());

        String seatList = "";
        for (Ticket ticket : ticketList) {
            seatList += ticket.getSeatId();
            if (ticketList.indexOf(ticket) != ticketList.size() - 1) {
                seatList += ", ";
            }
        }
        ticket_seat_value.setText(seatList);
        booking_code_value.setText(bookedTicketList.getTicketID());
        qr_code.setImageBitmap(bookedTicketList.getQrCode(bookedTicketList.getTicketID()));
    }
}