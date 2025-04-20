package com.example.moviebooking.data;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moviebooking.dto.DateTime;
import com.example.moviebooking.dto.Movie;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FireBaseManager {
    private static FireBaseManager instance;
    private static FirebaseDatabase firebaseDatabase;
    private static List<Movie> nowShowing = null;
    private static List<Movie> comingSoon = null;
    private static List<Movie> allMovies = null;

    private static final String USERS_TABLE = "USERS_INFO";
    private static final String TICKETS_TABLE = "TICKETS";

    private FireBaseManager() {
        // Private constructor to prevent instantiation outside of this class
        firebaseDatabase = FirebaseDatabase.getInstance("https://moviebooking-65416-default-rtdb.asia-southeast1.firebasedatabase.app");
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

                    if (passwordFromDB.equals(password)) {
                        Log.d("TAG", "Login successfully");
                        callback.onRegistrationResult(true, "Login successfully", new UserInfo(name, user, passwordFromDB));
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
}