package com.example.moviebooking.booking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingStatusActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE = 1;
    private String movieTitle, cinema, date, time, snackOrderId;
    private ArrayList<String> selectedCombos;
    private int snackPrice, snackCount;
    private HashMap<String, Integer> snackQuantities;
    private UserInfo userInfo;

    // Add new UI elements for snack information
    private RelativeLayout snackInfoLayout;
    private TextView tvSnackOrderId;
    private RecyclerView rvSnackList;

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.moviebooking.R.layout.activity_booking_status);
        extractIntentData();
        initUI();
        setOnClickListeners();
        setupSnackInfo();

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
        selectedCombos = getIntent().getStringArrayListExtra("selectedCombos");
        snackOrderId = getIntent().getStringExtra("snackOrderId");
        snackPrice = getIntent().getIntExtra("snackPrice", 0);
        snackCount = getIntent().getIntExtra("snackCount", 0);
        snackQuantities = (HashMap<String, Integer>) getIntent().getSerializableExtra("snackQuantities");
    }

    private void initUI() {
        TextView tvMovieTitle = findViewById(R.id.tv_movie_title);
        TextView tvCinemaName = findViewById(R.id.tv_cinema_name);
        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvHours = findViewById(R.id.tv_hours);

        // Initialize snack UI elements
        snackInfoLayout = findViewById(R.id.snack_info_layout);
        tvSnackOrderId = findViewById(R.id.tv_snack_order_id);
        rvSnackList = findViewById(R.id.rv_snack_list);

        tvMovieTitle.setText(movieTitle);
        tvCinemaName.setText(cinema);
        tvDate.setText(date);
        tvHours.setText(time);
    }

    private void setupSnackInfo() {
        // Show snack information only if there are snacks ordered
        if (selectedCombos != null && !selectedCombos.isEmpty() && snackCount > 0) {
            snackInfoLayout.setVisibility(View.VISIBLE);

            // Set snack order ID
            if (snackOrderId != null && !snackOrderId.isEmpty()) {
                tvSnackOrderId.setText("Order ID: " + snackOrderId);
            } else {
                tvSnackOrderId.setVisibility(View.GONE);
            }

            // Setup RecyclerView for snack list
            setupSnackRecyclerView();
        } else {
            snackInfoLayout.setVisibility(View.GONE);
        }
    }

    private void setupSnackRecyclerView() {
        if (snackQuantities != null && !snackQuantities.isEmpty()) {
            List<SnackItem> snackItems = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : snackQuantities.entrySet()) {
                snackItems.add(new SnackItem(entry.getKey(), entry.getValue()));
            }

            SnackListAdapter adapter = new SnackListAdapter(snackItems);
            rvSnackList.setLayoutManager(new LinearLayoutManager(this));
            rvSnackList.setAdapter(adapter);
        } else if (selectedCombos != null && !selectedCombos.isEmpty()) {
            // Fallback to selectedCombos if snackQuantities is null
            List<SnackItem> snackItems = new ArrayList<>();

            for (String combo : selectedCombos) {
                snackItems.add(new SnackItem(combo, 1)); // Assume quantity 1 if not specified
            }

            SnackListAdapter adapter = new SnackListAdapter(snackItems);
            rvSnackList.setLayoutManager(new LinearLayoutManager(this));
            rvSnackList.setAdapter(adapter);
        }
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
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

    // Inner class for snack item
    public static class SnackItem {
        private String name;
        private int quantity;

        public SnackItem(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
    }

    // RecyclerView Adapter for snack list
    public class SnackListAdapter extends RecyclerView.Adapter<SnackListAdapter.SnackViewHolder> {
        private List<SnackItem> snackItems;

        public SnackListAdapter(List<SnackItem> snackItems) {
            this.snackItems = snackItems;
        }

        @NonNull
        @Override
        public SnackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new SnackViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SnackViewHolder holder, int position) {
            SnackItem item = snackItems.get(position);
            holder.textView1.setText(item.getName());
            holder.textView2.setText("Quantity: " + item.getQuantity());
            holder.textView2.setTextSize(12);
            holder.textView2.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        @Override
        public int getItemCount() {
            return snackItems.size();
        }

        public class SnackViewHolder extends RecyclerView.ViewHolder {
            TextView textView1, textView2;

            public SnackViewHolder(@NonNull View itemView) {
                super(itemView);
                textView1 = itemView.findViewById(android.R.id.text1);
                textView2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}