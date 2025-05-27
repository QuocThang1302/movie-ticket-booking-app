package com.example.moviebooking.data;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moviebooking.dto.Comment;
import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.Schedule;
import com.example.moviebooking.dto.Seat;
import com.example.moviebooking.dto.Ticket;
import com.example.moviebooking.dto.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FireBaseManager {
    private static FireBaseManager instance;
    private static FirebaseDatabase firebaseDatabase;
    private static List<Movie> nowShowing = null;
    private static List<Movie> comingSoon = null;
    private static List<Movie> allMovies = null;

    private static final String USERS_TABLE = "USERS_INFO";
    private static final String TICKETS_TABLE = "TICKETS";
    private static final String COMMENTS_TABLE = "COMMENTS";

    private FireBaseManager() {
        // Private constructor to prevent instantiation outside of this class
        firebaseDatabase = FirebaseDatabase.getInstance("https://moviebooking-59c69-default-rtdb.asia-southeast1.firebasedatabase.app/");
    }
    public static synchronized FireBaseManager getInstance() {
        if (instance == null) {
            instance = new FireBaseManager();
        }
        return instance;
    }

    public interface OnMoviesDataLoadedListener {
        void onMoviesDataLoaded(List<Movie> allMovieList);
        void onMoviesDataError(String errorMessage);
    }

    private void fetchMoviesData(String node, OnMoviesDataLoadedListener listener) {
        List<Movie> allMovieList = new ArrayList<>();

        DatabaseReference moviesReference = firebaseDatabase.getReference(node);
        Log.d("MovieInfo", "Connect to firebase");

        moviesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMovieList.clear();

                for (DataSnapshot movieSnapshot : snapshot.getChildren()) {
                    Movie movie = movieSnapshot.getValue(Movie.class);
                    if (movie != null) {
                        List<String> genres = new ArrayList<>();
                        movieSnapshot.child("genres").getChildren().forEach(genreSnapshot -> {
                            genres.add(genreSnapshot.getValue(String.class));
                        });
                        movie.setMovieID(movieSnapshot.getKey());
                        movie.setGenres(genres);

                        allMovieList.add(movie);
                    }
                }
                listener.onMoviesDataLoaded(allMovieList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onMoviesDataError("Lỗi: " + error.getMessage());
            }
        });
    }
    public void fetchAllMoviesData(OnMoviesDataLoadedListener listener) {
        if (allMovies != null && allMovies.size() > 0) {
            listener.onMoviesDataLoaded(allMovies);
        }
        fetchMoviesData("MOVIES", listener);
    }
    public void fetchNowShowingMoviesData(OnMoviesDataLoadedListener listener) {
        if (nowShowing != null && nowShowing.size() > 0) {
            listener.onMoviesDataLoaded(nowShowing);
        }
        fetchMoviesData("NOW_SHOWING", listener);
    }

    public static void registerUser(Context context, String name, String username, String password, String confirmPassword, RegistrationCallback callback) {
        DatabaseReference usersReference = firebaseDatabase.getReference(USERS_TABLE);

        // Database paths must not contain '.', '#', '$', '[', or ']'

        if (username.contains(".") || username.contains("#") || username.contains("$") || username.contains("[") || username.contains("]")) {
            Toast.makeText(context, "Username must not contain '.', '#', '$', '[', or ']'", Toast.LENGTH_SHORT).show();
            callback.onRegistrationResult(false, "Username must not contain '.', '#', '$', '[', or ']'", null);
            return;
        }

        usersReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(context, "Username already exists", Toast.LENGTH_SHORT).show();
                    callback.onRegistrationResult(false, "Username already exists", null);
                } else {
                    DatabaseReference userInfoReference = firebaseDatabase.getReference(USERS_TABLE);
                    userInfoReference.child(username).child("name").setValue(name);
                    userInfoReference.child(username).child("username").setValue(username);
                    userInfoReference.child(username).child("password").setValue(password);
                    // ✅ Thêm ảnh mặc định
                    userInfoReference.child(username).child("profilePic")
                            .setValue("https://res.cloudinary.com/deagejli9/image/upload/v1748143032/rvsejnd0o74qrsbtegbv.jpg");

                    callback.onRegistrationResult(true, "Register successfully", null);
                    Log.d("TAG", "Data added successfully.");

                    callback.onRegistrationResult(true, "Register successfully", null);
                    Log.d("TAG", "Data added successfully.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "Error: " + databaseError.getMessage());
                callback.onRegistrationResult(false, "Error: " + databaseError.getMessage(), null);
            }
        });
    }

    public interface RegistrationCallback {
        void onRegistrationResult(boolean isSuccess, String message, Object data);
    }

    public static void loginUser(Context context, String username, String password, RegistrationCallback callback) {
        DatabaseReference usersReference = firebaseDatabase.getReference(USERS_TABLE);

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            callback.onRegistrationResult(false, "Please fill in all fields", null);
            return;
        }

        usersReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String passwordFromDB = dataSnapshot.child("password").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String user = dataSnapshot.child("username").getValue(String.class);
                    String profilePicture = dataSnapshot.child("profilePic").getValue(String.class);

                    if (passwordFromDB.equals(password)) {
                        Log.d("TAG", "Login successfully");
                        Log.d("TAG", "Profile picture URL: " + profilePicture);
                        callback.onRegistrationResult(true, "Login successfully", new UserInfo(name, user, passwordFromDB, profilePicture));

                    } else {
                        Toast.makeText(context, "Wrong password", Toast.LENGTH_SHORT).show();
                        callback.onRegistrationResult(false, "Wrong password", null);
                    }
                } else {
                    Toast.makeText(context, "Username does not exist", Toast.LENGTH_SHORT).show();
                    callback.onRegistrationResult(false, "Username does not exist", null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "Error: " + databaseError.getMessage());
                callback.onRegistrationResult(false, "Error: " + databaseError.getMessage(), null);
            }
        });
    }

    public static void registerTicket(Context context, Ticket ticket, RegistrationCallback callback) {
        DatabaseReference ticketInfoReference = firebaseDatabase.getReference(TICKETS_TABLE);

        // go through all tickets of the user to check if the user has already booked the movie
        ticketInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // handle exception
                for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                    String id = ticketSnapshot.getKey();
                    String userId = ticketSnapshot.child("userId").getValue(String.class);
                    String movieId = ticketSnapshot.child("movieId").getValue(String.class);
                    String cinemaId = ticketSnapshot.child("cinemaId").getValue(String.class);
                    String dateTimeStr = ticketSnapshot.child("dateTime").getValue(String.class);
                    String seatId = ticketSnapshot.child("seatId").getValue(String.class);
                    Boolean isBooked = ticketSnapshot.child("isBooked").getValue(Boolean.class);

                    if (movieId.equals(ticket.getMovieId()) && cinemaId.equals(ticket.getCinemaId()) && dateTimeStr.equals(ticket.getDateTime()) && seatId.equals(ticket.getSeatId()) && isBooked) {
                        //Toast.makeText(context, "Someone has already booked this movie", Toast.LENGTH_SHORT).show();
                        callback.onRegistrationResult(false, "You have already booked this movie", null);
                        return;
                    }
                }

                Calendar calendar = Calendar.getInstance();
                Date currentTime = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                DatabaseReference ticketInfoReference = firebaseDatabase.getReference(TICKETS_TABLE);
                String ticketId = ticketInfoReference.push().getKey();
                ticketInfoReference.child(ticketId).child("ticketID").setValue(ticket.getId());
                ticketInfoReference.child(ticketId).child("userId").setValue(ticket.getUserId());
                ticketInfoReference.child(ticketId).child("movieId").setValue(ticket.getMovieId());
                ticketInfoReference.child(ticketId).child("movieName").setValue(ticket.getMovieName());
                ticketInfoReference.child(ticketId).child("movieThumbnail").setValue(ticket.getThumbnail());
                ticketInfoReference.child(ticketId).child("cinemaId").setValue(ticket.getCinemaId());
                ticketInfoReference.child(ticketId).child("dateTime").setValue(ticket.getDateTime());
                ticketInfoReference.child(ticketId).child("seatId").setValue(ticket.getSeatId());
                ticketInfoReference.child(ticketId).child("isBooked").setValue(ticket.isPaid());
                ticketInfoReference.child(ticketId).child("bookedTime").setValue(dateFormat.format(currentTime));
                ticketInfoReference.child(ticketId).child("cost").setValue(ticket.getPayment());

                callback.onRegistrationResult(true, "Register successfully", ticketId);
                Log.d("TAG", "Data added successfully.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "Error: " + databaseError.getMessage());
                callback.onRegistrationResult(false, "Error: " + databaseError.getMessage(), null);
            }
        });
    }

    public static void fetchBookedSeats(Context context, Movie movie, String cinema, DateTime dateTime, RegistrationCallback callback) {
        DatabaseReference ticketInfoReference = firebaseDatabase.getReference(TICKETS_TABLE);
        List<Seat> seats = new ArrayList<>();

        // go through all tickets of the user to check if the user has already booked the movie
        ticketInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // handle exception
                for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                    String id = ticketSnapshot.getKey();
                    String userId = ticketSnapshot.child("userId").getValue(String.class);
                    String movieId = ticketSnapshot.child("movieId").getValue(String.class);
                    String cinemaId = ticketSnapshot.child("cinemaId").getValue(String.class);
                    String dateTimeStr = ticketSnapshot.child("dateTime").getValue(String.class);
                    String seatId = ticketSnapshot.child("seatId").getValue(String.class);
                    Boolean isBooked = ticketSnapshot.child("isBooked").getValue(Boolean.class);

                    if (movieId.equals(movie.getMovieID()) && cinemaId.equals(cinema) && dateTimeStr.equals(dateTime.toString())) {
                        Seat seat = new Seat(seatId, isBooked, true);
                        seats.add(seat);
                        Log.d("TAG", "Loaded: " + seat.getSeatId());
                        if (!isBooked) {
                            ticketSnapshot.getRef().removeValue();
                        }
                    }
                }
                callback.onRegistrationResult(true, "Register successfully", seats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "Error: " + databaseError.getMessage());
                callback.onRegistrationResult(false, "Error: " + databaseError.getMessage(), null);
            }
        });
    }

    public static void fetchUserTickets(UserInfo userInfo, OnBookedSeatsLoadedListener listener) {
        DatabaseReference ticketInfoReference = firebaseDatabase.getReference(TICKETS_TABLE);
        List<Ticket> tickets = new ArrayList<>();
        String username = userInfo.getUsername();

        // go through all tickets of the user to check if the user has already booked the movie
        ticketInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // handle exception
                for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                    String id = ticketSnapshot.getKey();
                    String userId = ticketSnapshot.child("userId").getValue(String.class);
                    String movieId = ticketSnapshot.child("movieId").getValue(String.class);
                    String movieName = ticketSnapshot.child("movieName").getValue(String.class);
                    String movieThumbnail = ticketSnapshot.child("movieThumbnail").getValue(String.class);
                    String cinemaId = ticketSnapshot.child("cinemaId").getValue(String.class);
                    String dateTimeStr = ticketSnapshot.child("dateTime").getValue(String.class);
                    String seatId = ticketSnapshot.child("seatId").getValue(String.class);
                    Boolean isBooked = ticketSnapshot.child("isBooked").getValue(Boolean.class);
                    String bookedTime = ticketSnapshot.child("bookedTime").getValue(String.class);
                    String cost = ticketSnapshot.child("cost").getValue(String.class);

                    if (userId.equals(username) && isBooked) {
                        Ticket ticket = new Ticket(id, userId, movieId, cinemaId, dateTimeStr, seatId, isBooked, bookedTime, cost);
                        ticket.setMovieName(movieName);
                        ticket.setThumbnail(movieThumbnail);
                        tickets.add(ticket);
                        Log.d("TAG", "Loaded: " + ticket.getId());
                    }
                }
                listener.onBookedSeatsLoaded(tickets);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "Error: " + databaseError.getMessage());
                listener.onBookedSeatsError("Error: " + databaseError.getMessage());
            }
        });
    }

    public interface OnBookedSeatsLoadedListener {
        void onBookedSeatsLoaded(List<Ticket> tickets);
        void onBookedSeatsError(String errorMessage);
    }
    public static void verifyUserForPasswordReset(Context context, String username, String name, RegistrationCallback callback) {
        DatabaseReference usersReference = firebaseDatabase.getReference(USERS_TABLE);

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            callback.onRegistrationResult(false, "Please fill in all fields", null);
            return;
        }

        usersReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nameFromDB = dataSnapshot.child("name").getValue(String.class);

                    if (nameFromDB != null && nameFromDB.equals(name)) {
                        // Username and name match, allow password reset
                        callback.onRegistrationResult(true, "Verification successful", username);
                    } else {
                        Toast.makeText(context, "Name does not match for this username", Toast.LENGTH_SHORT).show();
                        callback.onRegistrationResult(false, "Name does not match for this username", null);
                    }
                } else {
                    Toast.makeText(context, "Username does not exist", Toast.LENGTH_SHORT).show();
                    callback.onRegistrationResult(false, "Username does not exist", null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "Error: " + databaseError.getMessage());
                callback.onRegistrationResult(false, "Error: " + databaseError.getMessage(), null);
            }
        });
    }

    public static void resetPassword(Context context, String username, String newPassword, String confirmPassword, RegistrationCallback callback) {
        DatabaseReference usersReference = firebaseDatabase.getReference(USERS_TABLE);

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            callback.onRegistrationResult(false, "Please fill in all fields", null);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(context, "Password and confirm password are not the same", Toast.LENGTH_SHORT).show();
            callback.onRegistrationResult(false, "Password and confirm password are not the same", null);
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            callback.onRegistrationResult(false, "Password must be at least 6 characters", null);
            return;
        }

        usersReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Update password
                    usersReference.child(username).child("password").setValue(newPassword)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Password reset successfully", Toast.LENGTH_SHORT).show();
                                callback.onRegistrationResult(true, "Password reset successfully", null);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to reset password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                callback.onRegistrationResult(false, "Failed to reset password: " + e.getMessage(), null);
                            });
                } else {
                    Toast.makeText(context, "Username does not exist", Toast.LENGTH_SHORT).show();
                    callback.onRegistrationResult(false, "Username does not exist", null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "Error: " + databaseError.getMessage());
                callback.onRegistrationResult(false, "Error: " + databaseError.getMessage(), null);
            }
        });
    }

    // Schedules Schedules Schedules Schedules Schedules Schedules Schedules Schedules Schedules

// Check if a specific showtime is available (not fully booked)
public static void checkShowtimeAvailability(String movieId, String cinemaId, DateTime dateTime, int maxSeats, RegistrationCallback callback) {
    DatabaseReference ticketsReference = firebaseDatabase.getReference(TICKETS_TABLE);
    String dateTimeStr = dateTime.toString();

    ticketsReference.orderByChild("movieId").equalTo(movieId).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int bookedSeats = 0;

            for (DataSnapshot ticketSnapshot : snapshot.getChildren()) {
                String ticketCinemaId = ticketSnapshot.child("cinemaId").getValue(String.class);
                String ticketDateTime = ticketSnapshot.child("dateTime").getValue(String.class);
                Boolean isBooked = ticketSnapshot.child("isBooked").getValue(Boolean.class);

                // So sánh đúng lịch chiếu và rạp
                if (cinemaId.equals(ticketCinemaId) &&
                        dateTimeStr.equals(ticketDateTime) &&
                        Boolean.TRUE.equals(isBooked)) {
                    bookedSeats++;
                }
            }

            if (bookedSeats < maxSeats) {
                callback.onRegistrationResult(true, "Còn chỗ", null);
            } else {
                callback.onRegistrationResult(false, "Hết chỗ", null);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            callback.onRegistrationResult(false, "Lỗi khi kiểm tra dữ liệu: " + error.getMessage(), null);
        }
    });
}




    //if (cinemaId.equals(ticketCinemaId) && dateTimeStr.)// Add these to your FireBaseManager class

                    private static final String SCHEDULES_TABLE = "SCHEDULES";

                    public interface OnSchedulesDataLoadedListener {
                        void onSchedulesDataLoaded(List<Schedule> schedulesList);
                        void onSchedulesDataError(String errorMessage);
                    }

// Add a schedule to the database
                    public static void addSchedule(Context context, Schedule schedule, RegistrationCallback callback) {
                        DatabaseReference schedulesReference = firebaseDatabase.getReference(SCHEDULES_TABLE);

                        // First check if the movie exists
                        DatabaseReference moviesReference = firebaseDatabase.getReference("MOVIES");
                        moviesReference.child(schedule.getMovieId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    callback.onRegistrationResult(false, "Movie with ID " + schedule.getMovieId() + " does not exist", null);
                                    return;
                                }

                                // Generate a new key for the schedule
                                String scheduleId = schedulesReference.push().getKey();
                                if (scheduleId == null) {
                                    callback.onRegistrationResult(false, "Failed to generate schedule ID", null);
                                    return;
                                }

                                schedule.setScheduleId(scheduleId);

                                // Create a map with the schedule data
                                Map<String, Object> scheduleValues = new HashMap<>();
                                scheduleValues.put("scheduleId", schedule.getScheduleId());
                                scheduleValues.put("movieId", schedule.getMovieId());
                                scheduleValues.put("cinemaId", schedule.getCinemaId());
                                scheduleValues.put("active", schedule.isActive());

                                // Convert DateTime objects to strings for Firebase
                                List<String> showTimeStrings = schedule.getShowTimesAsStrings();
                                scheduleValues.put("showTimes", showTimeStrings);

                                // Add the schedule to the database
                                schedulesReference.child(scheduleId).setValue(scheduleValues)
                                        .addOnSuccessListener(aVoid -> {
                                            callback.onRegistrationResult(true, "Schedule added successfully", scheduleId);
                                        })
                                        .addOnFailureListener(e -> {
                                            callback.onRegistrationResult(false, "Failed to add schedule: " + e.getMessage(), null);
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("TAG", "Error checking movie: " + databaseError.getMessage());
                                callback.onRegistrationResult(false, "Error checking movie: " + databaseError.getMessage(), null);
                            }
                        });
                    }

// Update an existing schedule
                    public static void updateSchedule(Context context, Schedule schedule, RegistrationCallback callback) {
                        if (schedule.getScheduleId() == null || schedule.getScheduleId().isEmpty()) {
                            callback.onRegistrationResult(false, "Schedule ID is required for updates", null);
                            return;
                        }

                        DatabaseReference scheduleRef = firebaseDatabase.getReference(SCHEDULES_TABLE).child(schedule.getScheduleId());

                        // Check if the schedule exists
                        scheduleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    callback.onRegistrationResult(false, "Schedule with ID " + schedule.getScheduleId() + " does not exist", null);
                                    return;
                                }

                                // Update the schedule
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("movieId", schedule.getMovieId());
                                updates.put("cinemaId", schedule.getCinemaId());
                                updates.put("active", schedule.isActive());

                                // Convert DateTime objects to strings for Firebase
                                updates.put("showTimes", schedule.getShowTimesAsStrings());

                                scheduleRef.updateChildren(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            callback.onRegistrationResult(true, "Schedule updated successfully", schedule.getScheduleId());
                                        })
                                        .addOnFailureListener(e -> {
                                            callback.onRegistrationResult(false, "Failed to update schedule: " + e.getMessage(), null);
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("TAG", "Error checking schedule: " + databaseError.getMessage());
                                callback.onRegistrationResult(false, "Error checking schedule: " + databaseError.getMessage(), null);
                            }
                        });
                    }

// Delete a schedule
                    public static void deleteSchedule(Context context, String scheduleId, RegistrationCallback callback) {
                        DatabaseReference scheduleRef = firebaseDatabase.getReference(SCHEDULES_TABLE).child(scheduleId);

                        scheduleRef.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    callback.onRegistrationResult(true, "Schedule deleted successfully", null);
                                })
                                .addOnFailureListener(e -> {
                                    callback.onRegistrationResult(false, "Failed to delete schedule: " + e.getMessage(), null);
                                });
                    }

// Fetch all schedules
                    public static void fetchAllSchedules(OnSchedulesDataLoadedListener listener) {
                        DatabaseReference schedulesReference = firebaseDatabase.getReference(SCHEDULES_TABLE);

                        schedulesReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Schedule> schedules = new ArrayList<>();

                                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                                    try {
                                        String scheduleId = scheduleSnapshot.getKey();
                                        String movieId = scheduleSnapshot.child("movieId").getValue(String.class);
                                        String cinemaId = scheduleSnapshot.child("cinemaId").getValue(String.class);
                                        Boolean isActive = scheduleSnapshot.child("active").getValue(Boolean.class);

                                        if (isActive == null) {
                                            isActive = true; // Default value if not set
                                        }

                                        // Create a schedule object
                                        Schedule schedule = new Schedule();
                                        schedule.setScheduleId(scheduleId);
                                        schedule.setMovieId(movieId);
                                        schedule.setCinemaId(cinemaId);
                                        schedule.setActive(isActive);

                                        // Parse showTimes from strings to DateTime objects
                                        List<String> showTimeStrings = new ArrayList<>();
                                        for (DataSnapshot timeSnapshot : scheduleSnapshot.child("showTimes").getChildren()) {
                                            String time = timeSnapshot.getValue(String.class);
                                            if (time != null) {
                                                showTimeStrings.add(time);
                                            }
                                        }
                                        schedule.setShowTimesFromStrings(showTimeStrings);

                                        schedules.add(schedule);
                                    } catch (Exception e) {
                                        Log.e("TAG", "Error parsing schedule: " + e.getMessage());
                                    }
                                }

                                listener.onSchedulesDataLoaded(schedules);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                listener.onSchedulesDataError("Error: " + error.getMessage());
                            }
                        });
                    }

// Fetch schedules for a specific movie
                    public static void fetchSchedulesByMovie(String movieId, OnSchedulesDataLoadedListener listener) {
                        DatabaseReference schedulesReference = firebaseDatabase.getReference(SCHEDULES_TABLE);

                        schedulesReference.orderByChild("movieId").equalTo(movieId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Schedule> schedules = new ArrayList<>();

                                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                                    try {
                                        String scheduleId = scheduleSnapshot.getKey();
                                        String cinemaId = scheduleSnapshot.child("cinemaId").getValue(String.class);
                                        Boolean isActive = scheduleSnapshot.child("active").getValue(Boolean.class);

                                        if (isActive == null) {
                                            isActive = true; // Default value if not set
                                        }

                                        // Create a schedule object
                                        Schedule schedule = new Schedule();
                                        schedule.setScheduleId(scheduleId);
                                        schedule.setMovieId(movieId);
                                        schedule.setCinemaId(cinemaId);
                                        schedule.setActive(isActive);

                                        // Parse showTimes from strings to DateTime objects
                                        List<String> showTimeStrings = new ArrayList<>();
                                        for (DataSnapshot timeSnapshot : scheduleSnapshot.child("showTimes").getChildren()) {
                                            String time = timeSnapshot.getValue(String.class);
                                            if (time != null) {
                                                showTimeStrings.add(time);
                                            }
                                        }
                                        schedule.setShowTimesFromStrings(showTimeStrings);

                                        schedules.add(schedule);
                                    } catch (Exception e) {
                                        Log.e("TAG", "Error parsing schedule: " + e.getMessage());
                                    }
                                }

                                listener.onSchedulesDataLoaded(schedules);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                listener.onSchedulesDataError("Error: " + error.getMessage());
                            }
                        });
                    }

// Fetch schedules for a specific cinema
                    public static void fetchSchedulesByCinema(String cinemaId, OnSchedulesDataLoadedListener listener) {
                        DatabaseReference schedulesReference = firebaseDatabase.getReference(SCHEDULES_TABLE);

                        schedulesReference.orderByChild("cinemaId").equalTo(cinemaId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Schedule> schedules = new ArrayList<>();

                                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                                    try {
                                        String scheduleId = scheduleSnapshot.getKey();
                                        String movieId = scheduleSnapshot.child("movieId").getValue(String.class);
                                        Boolean isActive = scheduleSnapshot.child("active").getValue(Boolean.class);

                                        if (isActive == null) {
                                            isActive = true; // Default value if not set
                                        }

                                        // Create a schedule object
                                        Schedule schedule = new Schedule();
                                        schedule.setScheduleId(scheduleId);
                                        schedule.setMovieId(movieId);
                                        schedule.setCinemaId(cinemaId);
                                        schedule.setActive(isActive);

                                        // Parse showTimes from strings to DateTime objects
                                        List<String> showTimeStrings = new ArrayList<>();
                                        for (DataSnapshot timeSnapshot : scheduleSnapshot.child("showTimes").getChildren()) {
                                            String time = timeSnapshot.getValue(String.class);
                                            if (time != null) {
                                                showTimeStrings.add(time);
                                            }
                                        }
                                        schedule.setShowTimesFromStrings(showTimeStrings);

                                        schedules.add(schedule);
                                    } catch (Exception e) {
                                        Log.e("TAG", "Error parsing schedule: " + e.getMessage());
                                    }
                                }

                                listener.onSchedulesDataLoaded(schedules);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                listener.onSchedulesDataError("Error: " + error.getMessage());
                            }
                        });
                    }

// Fetch schedules for a specific movie and cinema
                    public static void fetchSchedulesByMovieAndCinema(String movieId, String cinemaId, OnSchedulesDataLoadedListener listener) {
                        DatabaseReference schedulesReference = firebaseDatabase.getReference(SCHEDULES_TABLE);

                        schedulesReference.orderByChild("movieId").equalTo(movieId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Schedule> schedules = new ArrayList<>();

                                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                                    try {
                                        String scheduleId = scheduleSnapshot.getKey();
                                        String scheduleCinemaId = scheduleSnapshot.child("cinemaId").getValue(String.class);
                                        Boolean isActive = scheduleSnapshot.child("active").getValue(Boolean.class);

                                        // Only include schedules for the specified cinema
                                        if (cinemaId.equals(scheduleCinemaId)) {
                                            if (isActive == null) {
                                                isActive = true; // Default value if not set
                                            }

                                            // Create a schedule object
                                            Schedule schedule = new Schedule();
                                            schedule.setScheduleId(scheduleId);
                                            schedule.setMovieId(movieId);
                                            schedule.setCinemaId(cinemaId);
                                            schedule.setActive(isActive);

                                            // Parse showTimes from strings to DateTime objects
                                            List<String> showTimeStrings = new ArrayList<>();
                                            for (DataSnapshot timeSnapshot : scheduleSnapshot.child("showTimes").getChildren()) {
                                                String time = timeSnapshot.getValue(String.class);
                                                if (time != null) {
                                                    showTimeStrings.add(time);
                                                }
                                            }
                                            schedule.setShowTimesFromStrings(showTimeStrings);

                                            schedules.add(schedule);
                                        }
                                    } catch (Exception e) {
                                        Log.e("TAG", "Error parsing schedule: " + e.getMessage());
                                    }
                                }

                                listener.onSchedulesDataLoaded(schedules);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                listener.onSchedulesDataError("Error: " + error.getMessage());
                            }
                        });
                    }

// Check if a specific showtime is available (not fully booked)
                    public static void checkShowtimeAvailability(String movieId, String cinemaId, String dateTime, int maxSeats, RegistrationCallback callback) {
                        DatabaseReference ticketsReference = firebaseDatabase.getReference(TICKETS_TABLE);

                        ticketsReference.orderByChild("movieId").equalTo(movieId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int bookedSeats = 0;

                                for (DataSnapshot ticketSnapshot : snapshot.getChildren()) {
                                    String ticketCinemaId = ticketSnapshot.child("cinemaId").getValue(String.class);
                                    String ticketDateTime = ticketSnapshot.child("dateTime").getValue(String.class);
                                    Boolean isBooked = ticketSnapshot.child("isBooked").getValue(Boolean.class);

                                    if (cinemaId.equals(ticketCinemaId) && dateTime.equals(ticketDateTime) && isBooked != null && isBooked) {
                                        bookedSeats++;
                                    }
                                }

                                boolean isAvailable = bookedSeats < maxSeats;
                                int remainingSeats = maxSeats - bookedSeats;

                                Map<String, Object> data = new HashMap<>();
                                data.put("isAvailable", isAvailable);
                                data.put("bookedSeats", bookedSeats);
                                data.put("remainingSeats", remainingSeats);
                                data.put("maxSeats", maxSeats);

                                callback.onRegistrationResult(true, isAvailable ?
                                        "Showtime is available with " + remainingSeats + " seats remaining" :
                                        "Showtime is fully booked", data);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                callback.onRegistrationResult(false, "Error checking availability: " + error.getMessage(), null);
                            }
                        });
                    }
    // Schedules Schedules Schedules Schedules Schedules Schedules Schedules Schedules Schedules

    //Add profile picture Uri in Database
    public void updateProfilePicture(String username, String imageUrl, UpdateCallback callback) {
        DatabaseReference userRef = firebaseDatabase.getReference("USERS_INFO").child(username);
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("profilePic", imageUrl);

        userRef.updateChildren(updateData)
                .addOnSuccessListener(aVoid -> callback.onUpdateResult(true))
                .addOnFailureListener(e -> callback.onUpdateResult(false));
    }

    public interface UpdateCallback {
        void onUpdateResult(boolean success);
    }

    public static DatabaseReference getDatabaseReference() {
        return firebaseDatabase.getReference();
    }



    //Comment
    public interface OnCommentOperationListener {
        void onCommentSaved(boolean success, String message);
    }

    public interface OnCommentsLoadedListener {
        void onCommentsLoaded(List<Comment> comments);
        void onCommentsError(String errorMessage);
    }

    // Method để lưu comment lên Firebase
    public void saveComment(Comment comment, OnCommentOperationListener listener) {
        DatabaseReference commentsReference = firebaseDatabase.getReference(COMMENTS_TABLE);


        String commentKey = commentsReference.push().getKey();

        if (commentKey != null) {
            comment.setCommentId(commentKey);

            commentsReference.child(commentKey).setValue(comment)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FireBaseManager", "Comment saved successfully");
                        listener.onCommentSaved(true, "Comment saved successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FireBaseManager", "Failed to save comment: " + e.getMessage());
                        listener.onCommentSaved(false, "Failed to save comment: " + e.getMessage());
                    });
        } else {
            listener.onCommentSaved(false, "Failed to generate comment ID");
        }
    }

    // Method để lấy tất cả comments của một bộ phim
    public void getCommentsByMovieId(String movieId, OnCommentsLoadedListener listener) {
        DatabaseReference commentsReference = firebaseDatabase.getReference(COMMENTS_TABLE);

        commentsReference.orderByChild("movieId").equalTo(movieId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Comment> comments = new ArrayList<>();

                        for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                            Comment comment = commentSnapshot.getValue(Comment.class);
                            if (comment != null) {
                                comment.setCommentId(commentSnapshot.getKey());
                                comments.add(comment);
                            }
                        }

                        // Sắp xếp theo thời gian (mới nhất trước)
                        Collections.sort(comments, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));

                        listener.onCommentsLoaded(comments);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FireBaseManager", "Error loading comments: " + error.getMessage());
                        listener.onCommentsError("Error loading comments: " + error.getMessage());
                    }
                });
    }

    // Method để lấy comment theo username
    public void getCommentsByUsername(String username, OnCommentsLoadedListener listener) {
        DatabaseReference commentsReference = firebaseDatabase.getReference(COMMENTS_TABLE);

        commentsReference.orderByChild("username").equalTo(username)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Comment> comments = new ArrayList<>();

                        for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                            Comment comment = commentSnapshot.getValue(Comment.class);
                            if (comment != null) {
                                comment.setCommentId(commentSnapshot.getKey());
                                comments.add(comment);
                            }
                        }

                        // Sắp xếp theo thời gian (mới nhất trước)
                        Collections.sort(comments, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));

                        listener.onCommentsLoaded(comments);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FireBaseManager", "Error loading comments by username: " + error.getMessage());
                        listener.onCommentsError("Error loading comments by username: " + error.getMessage());
                    }
                });
    }

    //

}