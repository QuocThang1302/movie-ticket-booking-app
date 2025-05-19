package com.example.moviebooking.booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.*;
import com.example.moviebooking.allmovies.MovieGridAdapter;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BookingActivity extends AppCompatActivity {
    private UserInfo userInfo = null;
    private Movie receivedMovie;
    private DateTime selectedDateTime;
    private String cinemaName;
    private GridLayout gridSeatsView;
    private static ImageView[][] seatsImageViews;
    private static Seat[][] seatStatus;
    private int selectedSeatCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        extractIntentData();
        initializeSeatsStatus();
        fetchBookedSeat();
        setOnClickListeners();
        setDataForFilmView();
        if (savedInstanceState == null) {
            setDataToSeatsGridFirstTime();
        }
        setDataToSeatsGrid();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchBookedSeat();
        setDataToSeatsGrid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchBookedSeat();
        setDataToSeatsGrid();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fetchBookedSeat();
        setDataToSeatsGrid();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchBookedSeat();
        setDataToSeatsGrid();
    }

    @Override
    protected void onStop() {
        super.onStop();

        List<Seat> selectedSeats = new ArrayList<>();
        for (int i = 0; i < seatStatus.length; i++) {
            for (int j = 0; j < seatStatus[0].length; j++) {
                if (seatStatus[i][j].isSelected() && !seatStatus[i][j].isBooked()) {
                    selectedSeats.add(seatStatus[i][j]);
                }
            }
        }
        if (selectedSeats.size() == 0) {
            return;
        }
        BookedTicketList bookedTicketList = new BookedTicketList();

        for (Seat seat : selectedSeats) {
            Ticket ticket = new Ticket(userInfo, receivedMovie, cinemaName, selectedDateTime, seat, false);
            bookedTicketList.addTicket(ticket);
        }

        for (Ticket ticket : bookedTicketList.getBookedTicketList()) {
            FireBaseManager.registerTicket(this, ticket, new FireBaseManager.RegistrationCallback() {
                @Override
                public void onRegistrationResult(boolean isSuccess, String message, Object data) {
                    if (isSuccess) {
                        Log.d("BookingStatusActivity", "onRegistrationResult: " + message);
                    }
                }
            });
        }
    }

    private void extractIntentData() {
        Intent moviePageIntent = getIntent();
        userInfo = (UserInfo) moviePageIntent.getSerializableExtra("userinfoIntent");
        receivedMovie = (Movie) moviePageIntent.getSerializableExtra("movie");
        selectedDateTime = (DateTime) moviePageIntent.getSerializableExtra("datetime");
        cinemaName = moviePageIntent.getStringExtra("cinema");

        if (receivedMovie == null || selectedDateTime == null || userInfo == null) {
            finish(); // Finish the activity if data is not found
        }

        Log.d("Booking", "onCreate: " + userInfo.getUsername());
    }

    private void initializeSeatsStatus() {
        seatStatus = new Seat[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                seatStatus[i][j] = new Seat((char) (i + 'A') + "" + j, false, false);
            }
        }
    }

    private void setOnClickListeners() {
        ImageView backButton = findViewById(R.id.iv_back_btn);
        backButton.setOnClickListener(v -> finish());

        ImageView fabButton = findViewById(R.id.fab);
        fabButton.setOnClickListener(v -> handleFabButtonClick());
    }

    private void handleFabButtonClick() {
        List<Seat> selectedSeats = getSelectedSeats();
        if (selectedSeats.size() == 0) {
            Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show();
            return;
        }

        BookedTicketList bookedTicketList = createBookedTicketList(selectedSeats);
        registerTickets(bookedTicketList.getBookedTicketList());

        Log.d("Booking", "handleFabButtonClick: " + bookedTicketList.getBookedTicketList().get(0).getId());
        navigateToBookingStatusActivity(bookedTicketList);
    }

    private List<Seat> getSelectedSeats() {
        List<Seat> selectedSeats = new ArrayList<>();
        for (int i = 0; i < seatStatus.length; i++) {
            for (int j = 0; j < seatStatus[0].length; j++) {
                if (seatStatus[i][j].isSelected() && !seatStatus[i][j].isBooked()) {
                    selectedSeats.add(seatStatus[i][j]);
                }
            }
        }
        return selectedSeats;
    }

    private BookedTicketList createBookedTicketList(List<Seat> selectedSeats) {
        BookedTicketList bookedTicketList = new BookedTicketList();
        String id = generateRandomString();
        for (int i = 0; i < selectedSeats.size(); i++) {
            String next_id = id.substring(0, id.length() - 1) + (char) (id.charAt(id.length() - 1) + i);
            Ticket ticket = new Ticket(next_id, userInfo, receivedMovie, cinemaName, selectedDateTime, selectedSeats.get(i), true);
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
                    Log.d("BookingStatusActivity", "onRegistrationResult: " + message);
                }
            });
        }
    }

    private void navigateToBookingStatusActivity(BookedTicketList bookedTicketList) {
        Intent intent = new Intent(this, com.example.moviebooking.ui.app.booking.SnackSelectionActivity.class); // Chuyển sang SnackSelectionActivity
        intent.putExtra("movie", receivedMovie);
        intent.putExtra("bookedTicketList", bookedTicketList);
        intent.putExtra("userinfoIntent", userInfo);
        intent.putExtra("datetime", selectedDateTime);
        intent.putExtra("cinema", cinemaName);
        startActivity(intent);
        finish();
    }

    private void fetchBookedSeat() {
        FireBaseManager.fetchBookedSeats(this, receivedMovie, cinemaName, selectedDateTime, (isSuccess, message, data) -> {
            if (isSuccess) {
                List<Seat> bookedSeats = (List<Seat>) data;
                selectedSeatCount = 0;
                for (Seat seat : bookedSeats) {
                    int row = seat.getSeatId().charAt(0) - 'A';
                    int col = seat.getSeatId().charAt(1) - '0';
                    seatStatus[row][col].setBooked(seat.isBooked());
                    seatStatus[row][col].setSelected(true);
                    if (!seat.isBooked()) {
                        selectedSeatCount++;
                    }
                }
                updatePaymentInfo();
            }
        });
    }

    private void updatePaymentInfo() {
        TextView seatCount = findViewById(R.id.tv_ticket_count);
        seatCount.setText("x" + selectedSeatCount);
        TextView totalPrice = findViewById(R.id.tv_total_price);
        totalPrice.setText("$" + selectedSeatCount * 10);
    }

    private void setDataToSeatsGridFirstTime() {
        gridSeatsView = findViewById(R.id.grid_seats);
        gridSeatsView.removeAllViews();

        int totalColumns = seatStatus[0].length;
        int totalRows = seatStatus.length;

        gridSeatsView.setColumnCount(totalColumns);
        gridSeatsView.setRowCount(totalRows);

        gridSeatsView.post(() -> {
            int imageViewSize = gridSeatsView.getWidth() / totalColumns;

            for (int i = 0; i < totalRows; i++) {
                for (int j = 0; j < totalColumns; j++) {
                    Log.d("Booking", "setDataToSeatsGrid: " + i + " " + j);
                    ImageView seatImageView = createSeatImageView(i, j, imageViewSize);
                    gridSeatsView.addView(seatImageView);
                }
            }
        });

        updatePaymentInfo();
    }

    private void setDataToSeatsGrid() {
        gridSeatsView = findViewById(R.id.grid_seats);
        gridSeatsView.removeAllViews();

        int totalColumns = seatStatus[0].length;
        int totalRows = seatStatus.length;

        gridSeatsView.setColumnCount(totalColumns);
        gridSeatsView.setRowCount(totalRows);

        int imageViewSize = gridSeatsView.getWidth() / totalColumns;

        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                Log.d("Booking", "setDataToSeatsGrid: " + i + " " + j);
                ImageView seatImageView = createSeatImageView(i, j, imageViewSize);
                gridSeatsView.addView(seatImageView);
            }
        }
        updatePaymentInfo();
    }

    private ImageView createSeatImageView(int row, int col, int imageViewSize) {
        ImageView seatImageView = new ImageView(BookingActivity.this);
        seatImageView.setPadding(10, 10, 10, 10);
        int seatBackgroundResource = getSeatBackgroundResource(row, col);
        Log.d("Booking", "c: " + seatBackgroundResource);
        seatImageView.setBackgroundResource(seatBackgroundResource);
        seatImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        seatImageView.setTag(seatStatus[row][col].getSeatId());

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = imageViewSize;
        params.height = imageViewSize;
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(col);

        seatImageView.setLayoutParams(params);

        seatImageView.setOnClickListener(v -> handleSeatClick((ImageView) v));
        seatImageView.invalidate();

        return seatImageView;
    }

    private int getSeatBackgroundResource(int row, int col) {
        if (seatStatus[row][col].isBooked()) {
            return R.drawable.button_seats_disabled;
        } else if (seatStatus[row][col].isSelected()) {
            return R.drawable.button_seats_selected;
        } else {
            return R.drawable.button_seats_unselected;
        }
    }

    private void handleSeatClick(ImageView seat) {
        String seatId = (String) seat.getTag();
        int row = seatId.charAt(0) - 'A';
        int col = seatId.charAt(1) - '0';

        if (seatStatus[row][col].isBooked()) {
            seat.setBackgroundResource(R.drawable.button_seats_disabled);
            return;
        }

        if (seatStatus[row][col].isSelected()) {
            seat.setBackgroundResource(R.drawable.button_seats_unselected);
            seatStatus[row][col].setSelected(false);
            selectedSeatCount--;
        } else {
            seat.setBackgroundResource(R.drawable.button_seats_selected);
            seatStatus[row][col].setSelected(true);
            selectedSeatCount++;
        }

        updatePaymentInfo();
    }

    private void setDataForFilmView() {
        com.makeramen.roundedimageview.RoundedImageView movieImage = findViewById(R.id.riv_movie_image);
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
}