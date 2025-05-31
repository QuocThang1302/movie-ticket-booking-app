package com.example.moviebooking.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moviebooking.R;
import com.example.moviebooking.dto.UserInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class PaymentActivity extends AppCompatActivity {

    private String movieTitle, cinema, date, time, snackOrderId;
    private double totalPrice;
    private UserInfo userInfo;
    private ImageView qrCodeImage;
    private ArrayList<String> selectedCombos;
    private int snackPrice, snackCount;
    private HashMap<String, Integer> snackQuantities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        extractIntentData();
        initUI();
        generateQRCode();
    }

    private void extractIntentData() {
        Intent intent = getIntent();
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
        Intent intent = new Intent(this, com.example.moviebooking.booking.BookingStatusActivity.class);
        intent.putExtra("movieTitle", movieTitle);
        intent.putExtra("cinema", cinema);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("userinfoIntent", userInfo);
        intent.putExtra("movie", getIntent().getSerializableExtra("movie"));
        intent.putExtra("bookedTicketList", getIntent().getSerializableExtra("bookedTicketList"));
        intent.putExtra("selectedCombos", selectedCombos);
        intent.putExtra("snackOrderId", snackOrderId);
        intent.putExtra("snackPrice", snackPrice);
        intent.putExtra("snackCount", snackCount);
        intent.putExtra("snackQuantities", snackQuantities);
        startActivity(intent);
        finish();
    }
}
