package com.example.moviebooking.booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.BookedTicketList;
import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.Seat;
import com.example.moviebooking.dto.Ticket;
import com.example.moviebooking.dto.UserInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class PaymentActivity extends AppCompatActivity {

    private String movieTitle, cinema, date, time, snackOrderId;
    private double totalPrice;
    private UserInfo userInfo;
    private ImageView qrCodeImage;
    private ArrayList<String> selectedCombos;
    private int snackPrice, snackCount;
    private HashMap<String, Integer> snackQuantities;
    private ArrayList<SnackItem> allSnackItems;
    private Movie receivedMovie;
    private DateTime selectedDateTime;
    private ArrayList<Seat> selectedSeats;
    private FireBaseManager fireBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        fireBaseManager = FireBaseManager.getInstance();
        extractIntentData();
        initUI();
        generateQRCode();
    }

    private void extractIntentData() {
        Intent intent = getIntent();

        allSnackItems = (ArrayList<SnackItem>) getIntent().getSerializableExtra("allSnackItems");
        selectedSeats = (ArrayList<Seat>) getIntent().getSerializableExtra("selectedSeats");
        selectedDateTime = (DateTime) getIntent().getSerializableExtra("datetime");
        receivedMovie = (Movie) getIntent().getSerializableExtra("receivedMovie");
        movieTitle = intent.getStringExtra("movieTitle");
        cinema = intent.getStringExtra("cinema");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        userInfo = (UserInfo) intent.getSerializableExtra("userinfoIntent");
        totalPrice = intent.getDoubleExtra("totalPrice", 0.0);
        selectedCombos = getIntent().getStringArrayListExtra("selectedCombos");
        snackOrderId = getIntent().getStringExtra("snackOrderId");
        snackPrice = getIntent().getIntExtra("snackPrice", 0);
        snackCount = getIntent().getIntExtra("snackCount", 0);
        snackQuantities = (HashMap<String, Integer>) getIntent().getSerializableExtra("snackQuantities");
        if (movieTitle == null || cinema == null || date == null || time == null || userInfo == null) {
            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initUI() {
        TextView tvMovieTitle = findViewById(R.id.tv_movie_title);
        TextView tvCinemaName = findViewById(R.id.tv_cinema_name);
        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvHours = findViewById(R.id.tv_hours);
        TextView tvTotalPrice = findViewById(R.id.tv_total_price);
        qrCodeImage = findViewById(R.id.iv_qr_code);

        tvMovieTitle.setText(movieTitle != null ? movieTitle : "Không có tiêu đề");
        tvCinemaName.setText(cinema != null ? cinema : "Không có rạp");
        tvDate.setText(date != null ? date : "Không có ngày");
        tvHours.setText(time != null ? time : "Không có giờ");
        tvTotalPrice.setText("Total: $" + String.format("%.2f", totalPrice));

        ImageView backBtn = findViewById(R.id.iv_back_btn);
        backBtn.setOnClickListener(v -> finish());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::handleNextStep);
    }

    private void generateQRCode() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            String content = movieTitle + "|" + cinema + "|" + date + "|" + time + "|" + totalPrice;
            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, com.google.zxing.BarcodeFormat.QR_CODE, 400, 400);
            qrCodeImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNextStep(View view) {
        BookedTicketList bookedTicketList = createBookedTicketList(selectedSeats);
        registerTickets(bookedTicketList.getBookedTicketList());
        if (hasSnackOrders()) {
            saveSnackOrderToFirebase();
        }
        // Sau khi đẩy dữ liệu lên Firebase, chuyển tiếp sang BookingStatusActivity
        Intent intent = new Intent(this, com.example.moviebooking.booking.BookingStatusActivity.class);
        intent.putExtra("movieTitle", movieTitle);
        intent.putExtra("cinema", cinema);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("userinfoIntent", userInfo);
        intent.putExtra("movie", getIntent().getSerializableExtra("movie"));
        intent.putExtra("bookedTicketList", bookedTicketList);
        intent.putExtra("selectedCombos", selectedCombos);
        intent.putExtra("snackOrderId", snackOrderId);
        intent.putExtra("snackPrice", snackPrice);
        intent.putExtra("snackCount", snackCount);
        intent.putExtra("snackQuantities", snackQuantities);
        startActivity(intent);
        finish();
    }
    private boolean hasSnackOrders() {
        return snackCount > 0;
    }
    private void saveSnackOrderToFirebase() {
        SnackOrder snackOrder = new SnackOrder(
                userInfo.getUsername(),
                userInfo.getUsername(),
                receivedMovie.getTitle(),
                cinema,
                selectedDateTime.getShortDate(),
                selectedDateTime.getTimeAMPM(),
                snackQuantities,
                allSnackItems
        );
        fireBaseManager.saveSnackOrder(snackOrder, new FireBaseManager.OnSnackOrderSavedListener() {
            @Override
            public void onSnackOrderSaved(String orderId, boolean success) {
                if (success) {
                    Log.d("SnackOrder", "Snack order saved successfully: " + orderId);
                    Toast.makeText(PaymentActivity.this, "Snack order saved successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "Failed to save snack order!", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onSnackOrderError(String errorMessage) {
                Log.e("SnackOrder", "Failed to save snack order!" + errorMessage);
                Toast.makeText(PaymentActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private BookedTicketList createBookedTicketList(List<Seat> selectedSeats) {
        BookedTicketList bookedTicketList = new BookedTicketList();
        String id = generateRandomString();
        for (int i = 0; i < selectedSeats.size(); i++) {
            String next_id = id.substring(0, id.length() - 1) + (char) (id.charAt(id.length() - 1) + i);
            Ticket ticket = new Ticket(next_id, userInfo, receivedMovie, cinema, selectedDateTime, selectedSeats.get(i), true);
            Log.d("Booking", "createBookedTicketList: " + ticket.getId());
            bookedTicketList.addTicket(ticket);
        }
        return bookedTicketList;
    }
    private static String generateRandomString() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder randomString = new StringBuilder();

        randomString.append((char) (secureRandom.nextInt(26) + 'A'));
        randomString.append((char) (secureRandom.nextInt(26) + 'A'));

        for (int i = 0; i < 13; i++) {
            randomString.append((char) (secureRandom.nextInt(10) + '0'));
        }

        return randomString.toString();
    }
    private void registerTickets(List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            FireBaseManager.registerTicket(this, ticket, (isSuccess, message, data) -> {
                if (isSuccess) {
                    Log.d("PaymentActivity", "onRegistrationResult: " + message);
                }
            });
        }
    }


}