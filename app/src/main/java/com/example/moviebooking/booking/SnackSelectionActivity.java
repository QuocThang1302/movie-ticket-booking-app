package com.example.moviebooking.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.moviebooking.R;
import com.example.moviebooking.dto.BookedTicketList;
import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.UserInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnackSelectionActivity extends AppCompatActivity {

    private UserInfo userInfo;
    private Movie receivedMovie;
    private DateTime selectedDateTime;
    private String cinemaName;
    private BookedTicketList bookedTicketList;

    private int selectedSnackCount = 0;
    private double ticketPrice;
    private double snackPrice = 0;
    private TextView tvSnackCount;
    private TextView tvTotalPrice;
    private List<String> selectedCombos = new ArrayList<>();
    private Map<String, Integer> snackQuantities = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snack_selection);

        extractIntentData();
        initializeUI();
        setDataForFilmView();
        setupTabs();
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

        ticketPrice = bookedTicketList.getBookedTicketList().size() * 10.0; // Giả sử mỗi vé $10
    }

    private void initializeUI() {
        ImageView backButton = findViewById(R.id.iv_back_btn);
        backButton.setOnClickListener(v -> finish());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::handleFabButtonClick);

        tvSnackCount = findViewById(R.id.tv_ticket_count);
        tvTotalPrice = findViewById(R.id.tv_total_price);
    }

    private void setDataForFilmView() {
        RoundedImageView movieImage = findViewById(R.id.riv_movie_image);
        TextView movieTitle = findViewById(R.id.tv_movie_title);
        TextView ticketDate = findViewById(R.id.tv_date);
        TextView ticketTime = findViewById(R.id.tv_hours);
        TextView ticketCinema = findViewById(R.id.tv_cinema_name);

        Glide.with(this).load(receivedMovie.getThumbnail()).into(movieImage);
        movieTitle.setText(receivedMovie.getTitle());
        ticketDate.setText(selectedDateTime.getShortDate());
        ticketTime.setText(selectedDateTime.getTimeAMPM());
        ticketCinema.setText(cinemaName);
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        SnackPagerAdapter pagerAdapter = new SnackPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Combo");
                            break;
                        case 1:
                            tab.setText("Drink");
                            break;
                        case 2:
                            tab.setText("Popcorn");
                            break;
                        case 3:
                            tab.setText("Snack");
                            break;
                    }
                }).attach();
    }

    void updatePaymentInfo() {
        tvSnackCount.setText("x" + selectedSnackCount);
        double totalPrice = ticketPrice + snackPrice;
        tvTotalPrice.setText("$" + String.format("%.2f", totalPrice));
    }

    private void handleFabButtonClick(View v) {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("movie", receivedMovie);
        intent.putExtra("bookedTicketList", bookedTicketList);
        intent.putExtra("userinfoIntent", userInfo);
        intent.putExtra("totalPrice", ticketPrice + snackPrice);
        intent.putStringArrayListExtra("selectedCombos", (ArrayList<String>) selectedCombos);
        intent.putExtra("bookingTime", "10:28 PM, Sun, 25 May 2025"); // Cập nhật thời gian hiện tại
        startActivity(intent);
        finish();
    }

    public void updateSnackPrice(double price) {
        snackPrice += price;
    }

    public void updateSnackCount() {
        selectedSnackCount = 0;
        for (int quantity : snackQuantities.values()) {
            selectedSnackCount += quantity;
        }
    }

    public void addSelectedCombo(String combo) {
        selectedCombos.add(combo);
    }

    public void removeSelectedCombo(String combo) {
        selectedCombos.remove(combo);
    }

    // Adapter for ViewPager
    private class SnackPagerAdapter extends RecyclerView.Adapter<SnackViewHolder> {
        private final List<List<SnackItem>> categoryItems;
        private final LayoutInflater inflater;

        SnackPagerAdapter(AppCompatActivity activity) {
            inflater = activity.getLayoutInflater();
            categoryItems = new ArrayList<>();
            categoryItems.add(getComboItems());
            categoryItems.add(getDrinkItems());
            categoryItems.add(getPopcornItems());
            categoryItems.add(getSnackItems());
        }

        @NonNull
        @Override
        public SnackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_snack, parent, false);
            return new SnackViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SnackViewHolder holder, int position) {
            List<SnackItem> items = categoryItems.get(position);
            SnackItemAdapter adapter = new SnackItemAdapter(items, snackQuantities, SnackSelectionActivity.this);
            holder.recyclerView.setAdapter(adapter);
        }

        @Override
        public int getItemCount() {
            return 4; // Số lượng tab
        }

        private List<SnackItem> getComboItems() {
            List<SnackItem> items = new ArrayList<>();
            items.add(new SnackItem("Pepsi Lớn + Bắp Ngọt", 70.0, R.drawable.combo2));
            items.add(new SnackItem("Pepsi Lớn + Bắp phô mai", 77.0, R.drawable.combo2));
            items.add(new SnackItem("Pepsi Không Calo Lớn + Bắp Ngọt", 70.0, R.drawable.combo2));
            items.add(new SnackItem("Pepsi Không Calo Lớn + Bắp phô mai", 77.0, R.drawable.combo2));
            return items;
        }

        private List<SnackItem> getDrinkItems() {
            List<SnackItem> items = new ArrayList<>();
            items.add(new SnackItem("Pepsi", 27.0, R.drawable.pepsi2));
            items.add(new SnackItem("Pepsi Lớn", 30.0, R.drawable.pepsi2));
            items.add(new SnackItem("Pepsi Không Calo", 27.0, R.drawable.pepsi2));
            items.add(new SnackItem("Pepsi Không Calo Lớn", 30.0, R.drawable.pepsi2));
            return items;
        }

        private List<SnackItem> getPopcornItems() {
            List<SnackItem> items = new ArrayList<>();
            items.add(new SnackItem("Bắp Ngọt", 45.0, R.drawable.popcorn));
            items.add(new SnackItem("Bắp Phô Mai", 52.0, R.drawable.popcorn));
            items.add(new SnackItem("Bắp Caramel", 52.0, R.drawable.popcorn));
            return items;
        }

        private List<SnackItem> getSnackItems() {
            List<SnackItem> items = new ArrayList<>();
            items.add(new SnackItem("Snack Quế Thái Lan Cam", 27.0, R.drawable.snack));
            items.add(new SnackItem("Oishi Pillows Socola", 27.0, R.drawable.snack));
            items.add(new SnackItem("Mực Bento Xanh (ít Cay)", 35.0, R.drawable.snack));
            return items;
        }
    }
}