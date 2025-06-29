package com.example.moviebooking;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.*;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.example.moviebooking.dto.UserInfo;
import com.google.firebase.database.*;

import okhttp3.*;
import org.json.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class AIActivity extends AppCompatActivity {

    EditText etUserInput;
    Button btnSend;
    LinearLayout chatContainer;
    ScrollView scrollView;

    // Firebase
    private DatabaseReference databaseRef;
    private String firebaseData = ""; // Lưu dữ liệu từ Firebase

    // UserInfo từ Intent
    private UserInfo currentUser;
    private String currentUserContext = "";

    // 🔥 GROQ API - COMPLETELY FREE
    private static final String GROQ_API_KEY = "đổi key ở đây nha ấy đứa";
    private static final String TAG = "AIActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aiactivity);

        etUserInput = findViewById(R.id.etUserInput);
        btnSend = findViewById(R.id.btnSend);
        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.scrollView);

        // Nhận UserInfo từ Intent
        receiveUserInfo();

        // Khởi tạo Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Load dữ liệu từ Firebase
        loadFirebaseData();

        // Show initial welcome message với thông tin user
        String welcomeMessage = "🤖 Hello " + (currentUser != null ? currentUser.getName() : "Guest") + "!\n\n" +
                "🎬 Movie Booking CinesGPT is ready!\n\n" +
                "I can help you with:\n" +
                "• Movie information & recommendations\n" +
                "• Your ticket bookings & schedules\n" +
                "• Your snack orders & reviews\n" +
                "• Your account information\n\n" +
                "🔥 Loading your personalized cinema data...";

        addAIMessage(welcomeMessage);

        btnSend.setOnClickListener(v -> {
            String userInput = etUserInput.getText().toString().trim();
            if (!userInput.isEmpty()) {
                // Add user message
                addUserMessage(userInput);

                // Clear input
                etUserInput.setText("");

                // Add loading message
                TextView loadingMessage = addAIMessage("⏳ Thinking...");

                // Scroll to bottom
                scrollToBottom();

                // Call API with Firebase context and user info
                getAIResponseWithContext(userInput, loadingMessage);
            } else {
                Toast.makeText(this, "⚠️ Please enter a message!", Toast.LENGTH_SHORT).show();
            }
        });

        // Enter to send message
        etUserInput.setOnEditorActionListener((v, actionId, event) -> {
            btnSend.performClick();
            return true;
        });
    }

    private void receiveUserInfo() {
        // Nhận UserInfo từ Intent
        if (getIntent().hasExtra("userinfoIntent")) {
            currentUser = (UserInfo) getIntent().getSerializableExtra("userinfoIntent");

            if (currentUser != null) {
                // Tạo context cho user hiện tại
                currentUserContext = "🔑 CURRENT USER INFORMATION:\n" +
                        "- Name: " + currentUser.getName() + "\n" +
                        "- Username: " + currentUser.getUsername() + "\n" +
                        "- Role: " + (currentUser.isAdmin() ? "ADMIN" : "USER") + "\n" +
                        "- Profile Picture: " + (currentUser.getProfilePic() != null ? "Available" : "Not set") + "\n\n" +
                        "📋 IMPORTANT: When answering questions, prioritize information related to this user. " +
                        "Show their bookings, orders, comments first. If they ask about 'my' or 'I', refer to this user.\n\n";

                Log.d(TAG, "User logged in: " + currentUser.getName() + " (" + currentUser.getUsername() + ")");
                Log.d(TAG, "User role: " + (currentUser.isAdmin() ? "Admin" : "User"));
            }
        } else {
            Log.d(TAG, "No user info received from Intent");
            currentUserContext = "⚠️ GUEST USER - No specific user information available.\n\n";
        }
    }

    private void loadFirebaseData() {
        // Lấy dữ liệu từ các collection theo cấu trúc Firebase của bạn
        loadMovieData();
        loadUserData();
        loadTicketData();
        loadScheduleData();
        loadSnackOrderData();
        loadCommentData();
    }

    private void loadMovieData() {
        databaseRef.child("MOVIES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder movieData = new StringBuilder();
                movieData.append("🎬 MOVIES DATA:\n");

                for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
                    String movieId = movieSnapshot.getKey();
                    String title = movieSnapshot.child("title").getValue(String.class);
                    String description = movieSnapshot.child("description").getValue(String.class);
                    String duration = movieSnapshot.child("duration").getValue(String.class);
                    String rate = movieSnapshot.child("rate").getValue(String.class);
                    String year = movieSnapshot.child("year").getValue(String.class);

                    // Xử lý genres array
                    StringBuilder genreList = new StringBuilder();
                    if (movieSnapshot.child("genres").exists()) {
                        for (DataSnapshot genreSnapshot : movieSnapshot.child("genres").getChildren()) {
                            if (genreList.length() > 0) genreList.append(", ");
                            genreList.append(genreSnapshot.getValue(String.class));
                        }
                    }

                    movieData.append("- ").append(title).append(" (").append(year).append(")")
                            .append("\n  ID: ").append(movieId)
                            .append("\n  Genres: ").append(genreList.toString())
                            .append("\n  Duration: ").append(duration).append(" minutes")
                            .append("\n  Rating: ").append(rate).append("/5")
                            .append("\n  Description: ").append(description != null ? description.substring(0, Math.min(100, description.length())) + "..." : "N/A")
                            .append("\n\n");
                }

                updateFirebaseData("MOVIES", movieData.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error: " + databaseError.getMessage());
            }
        });
    }

    private void loadUserData() {
        databaseRef.child("USERS_INFO").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder userData = new StringBuilder();
                userData.append("👥 USERS DATA:\n");

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String name = userSnapshot.child("name").getValue(String.class);
                    String username = userSnapshot.child("username").getValue(String.class);

                    // Đánh dấu user hiện tại
                    String userMarker = "";
                    if (currentUser != null &&
                            (currentUser.getUsername().equals(username) || currentUser.getName().equals(name))) {
                        userMarker = " ⭐ [CURRENT USER]";
                    }

                    userData.append("- User ID: ").append(userId).append(userMarker)
                            .append("\n  Name: ").append(name)
                            .append("\n  Username: ").append(username)
                            .append("\n\n");
                }

                updateFirebaseData("USERS", userData.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error: " + databaseError.getMessage());
            }
        });
    }

    private void loadTicketData() {
        databaseRef.child("TICKETS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder ticketData = new StringBuilder();
                ticketData.append("🎫 TICKETS DATA:\n");

                // Separate user's tickets and others
                StringBuilder userTickets = new StringBuilder();
                StringBuilder otherTickets = new StringBuilder();

                for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                    String ticketKey = ticketSnapshot.getKey();
                    String ticketID = ticketSnapshot.child("ticketID").getValue(String.class);
                    String movieName = ticketSnapshot.child("movieName").getValue(String.class);
                    String cinemaId = ticketSnapshot.child("cinemaId").getValue(String.class);
                    String dateTime = ticketSnapshot.child("dateTime").getValue(String.class);
                    String seatId = ticketSnapshot.child("seatId").getValue(String.class);
                    String cost = ticketSnapshot.child("cost").getValue(String.class);
                    String userId = ticketSnapshot.child("userId").getValue(String.class);
                    String bookedTime = ticketSnapshot.child("bookedTime").getValue(String.class);
                    Boolean isBooked = ticketSnapshot.child("isBooked").getValue(Boolean.class);

                    String ticketInfo = "- Ticket ID: " + ticketID +
                            "\n  Movie: " + movieName +
                            "\n  Cinema: " + cinemaId +
                            "\n  Date & Time: " + dateTime +
                            "\n  Seat: " + seatId +
                            "\n  Cost: " + cost +
                            "\n  User ID: " + userId +
                            "\n  Booked Time: " + bookedTime +
                            "\n  Status: " + (isBooked ? "BOOKED" : "AVAILABLE") +
                            "\n\n";

                    // Check if this ticket belongs to current user
                    boolean isCurrentUserTicket = false;
                    if (currentUser != null && userId != null) {
                        // You might need to adjust this logic based on how userId relates to username
                        isCurrentUserTicket = userId.equals(currentUser.getUsername()) ||
                                userId.equals(currentUser.getName());
                    }

                    if (isCurrentUserTicket) {
                        userTickets.append(ticketInfo.replace("- Ticket ID:", "- Ticket ID: ⭐"));
                    } else {
                        otherTickets.append(ticketInfo);
                    }
                }

                // Prioritize current user's tickets
                if (userTickets.length() > 0) {
                    ticketData.append("\n🌟 CURRENT USER'S TICKETS:\n").append(userTickets);
                }
                if (otherTickets.length() > 0) {
                    ticketData.append("\n📋 OTHER TICKETS:\n").append(otherTickets);
                }

                updateFirebaseData("TICKETS", ticketData.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error: " + databaseError.getMessage());
            }
        });
    }

    private void loadScheduleData() {
        databaseRef.child("SCHEDULES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder scheduleData = new StringBuilder();
                scheduleData.append("📅 SCHEDULES DATA:\n");

                for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                    String scheduleKey = scheduleSnapshot.getKey();
                    String cinema = scheduleSnapshot.child("cinema").getValue(String.class);
                    String movieId = scheduleSnapshot.child("movieId").getValue(String.class);

                    StringBuilder showTimesList = new StringBuilder();
                    if (scheduleSnapshot.child("showTimes").exists()) {
                        for (DataSnapshot showTimeSnapshot : scheduleSnapshot.child("showTimes").getChildren()) {
                            if (showTimesList.length() > 0) showTimesList.append(", ");
                            showTimesList.append(showTimeSnapshot.getValue(String.class));
                        }
                    }

                    scheduleData.append("- Schedule ID: ").append(scheduleKey)
                            .append("\n  Cinema: ").append(cinema)
                            .append("\n  Movie ID: ").append(movieId)
                            .append("\n  Show Times: ").append(showTimesList.toString())
                            .append("\n\n");
                }

                updateFirebaseData("SCHEDULES", scheduleData.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error: " + databaseError.getMessage());
            }
        });
    }

    private void loadSnackOrderData() {
        databaseRef.child("SNACK_ORDERS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder snackData = new StringBuilder();
                snackData.append("🍿 SNACK ORDERS DATA:\n");

                // Separate user's orders and others
                StringBuilder userOrders = new StringBuilder();
                StringBuilder otherOrders = new StringBuilder();

                for (DataSnapshot snackSnapshot : dataSnapshot.getChildren()) {
                    String orderId = snackSnapshot.getKey();
                    String userName = snackSnapshot.child("userName").getValue(String.class);
                    String movieTitle = snackSnapshot.child("movieTitle").getValue(String.class);
                    String cinemaName = snackSnapshot.child("cinemaName").getValue(String.class);
                    String showDate = snackSnapshot.child("showDate").getValue(String.class);
                    String showTime = snackSnapshot.child("showTime").getValue(String.class);
                    String orderStatus = snackSnapshot.child("orderStatus").getValue(String.class);
                    Integer totalPrice = snackSnapshot.child("totalSnackPrice").getValue(Integer.class);

                    StringBuilder itemsList = new StringBuilder();
                    if (snackSnapshot.child("snackItems").exists()) {
                        for (DataSnapshot itemSnapshot : snackSnapshot.child("snackItems").getChildren()) {
                            String itemName = itemSnapshot.child("itemName").getValue(String.class);
                            Integer quantity = itemSnapshot.child("quantity").getValue(Integer.class);
                            Integer itemPrice = itemSnapshot.child("totalPrice").getValue(Integer.class);

                            if (itemsList.length() > 0) itemsList.append(", ");
                            itemsList.append(itemName).append(" x").append(quantity)
                                    .append(" (").append(itemPrice).append("đ)");
                        }
                    }

                    String orderInfo = "- Order ID: " + orderId +
                            "\n  Customer: " + userName +
                            "\n  Movie: " + movieTitle +
                            "\n  Cinema: " + cinemaName +
                            "\n  Show: " + showDate + " at " + showTime +
                            "\n  Status: " + orderStatus +
                            "\n  Items: " + itemsList.toString() +
                            "\n  Total: " + totalPrice + "đ" +
                            "\n\n";

                    // Check if this order belongs to current user
                    boolean isCurrentUserOrder = false;
                    if (currentUser != null && userName != null) {
                        isCurrentUserOrder = userName.equals(currentUser.getName()) ||
                                userName.equals(currentUser.getUsername());
                    }

                    if (isCurrentUserOrder) {
                        userOrders.append(orderInfo.replace("- Order ID:", "- Order ID: ⭐"));
                    } else {
                        otherOrders.append(orderInfo);
                    }
                }

                // Prioritize current user's orders
                if (userOrders.length() > 0) {
                    snackData.append("\n🌟 CURRENT USER'S ORDERS:\n").append(userOrders);
                }
                if (otherOrders.length() > 0) {
                    snackData.append("\n📋 OTHER ORDERS:\n").append(otherOrders);
                }

                updateFirebaseData("SNACK_ORDERS", snackData.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error: " + databaseError.getMessage());
            }
        });
    }

    private void loadCommentData() {
        databaseRef.child("COMMENTS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder commentData = new StringBuilder();
                commentData.append("💬 COMMENTS DATA:\n");

                // Separate user's comments and others
                StringBuilder userComments = new StringBuilder();
                StringBuilder otherComments = new StringBuilder();

                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    String commentId = commentSnapshot.getKey();
                    String content = commentSnapshot.child("content").getValue(String.class);
                    String movieTitle = commentSnapshot.child("movieTitle").getValue(String.class);
                    String username = commentSnapshot.child("username").getValue(String.class);
                    Integer rating = commentSnapshot.child("rating").getValue(Integer.class);
                    Long timestamp = commentSnapshot.child("timestamp").getValue(Long.class);

                    String commentInfo = "- Comment ID: " + commentId +
                            "\n  User: " + username +
                            "\n  Movie: " + movieTitle +
                            "\n  Rating: " + rating + "/5" +
                            "\n  Content: " + content +
                            "\n  Time: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date(timestamp)) +
                            "\n\n";

                    // Check if this comment belongs to current user
                    boolean isCurrentUserComment = false;
                    if (currentUser != null && username != null) {
                        isCurrentUserComment = username.equals(currentUser.getUsername()) ||
                                username.equals(currentUser.getName());
                    }

                    if (isCurrentUserComment) {
                        userComments.append(commentInfo.replace("- Comment ID:", "- Comment ID: ⭐"));
                    } else {
                        otherComments.append(commentInfo);
                    }
                }

                // Prioritize current user's comments
                if (userComments.length() > 0) {
                    commentData.append("\n🌟 CURRENT USER'S COMMENTS:\n").append(userComments);
                }
                if (otherComments.length() > 0) {
                    commentData.append("\n📋 OTHER COMMENTS:\n").append(otherComments);
                }

                updateFirebaseData("COMMENTS", commentData.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Firebase error: " + databaseError.getMessage());
            }
        });
    }

    private void updateFirebaseData(String dataType, String data) {
        // Cập nhật dữ liệu Firebase vào context
        if (firebaseData.contains(dataType.toUpperCase())) {
            // Replace existing data
            String[] parts = firebaseData.split(dataType.toUpperCase() + " DATA:");
            if (parts.length > 1) {
                String beforeData = parts[0];
                String afterData = "";
                String[] afterParts = parts[1].split("\n\n");
                if (afterParts.length > 1) {
                    for (int i = 1; i < afterParts.length; i++) {
                        afterData += "\n\n" + afterParts[i];
                    }
                }
                firebaseData = beforeData + data + afterData;
            }
        } else {
            // Add new data
            firebaseData += "\n\n" + data;
        }

        Log.d(TAG, "Firebase data updated: " + dataType);
    }

    private void addUserMessage(String message) {
        TextView messageView = createMessageBubble(message, true);
        chatContainer.addView(messageView);
    }

    private TextView addAIMessage(String message) {
        TextView messageView = createMessageBubble(message, false);
        chatContainer.addView(messageView);
        return messageView;
    }

    private TextView createMessageBubble(String message, boolean isUser) {
        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        messageView.setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));
        messageView.setTextIsSelectable(true);

        // Create bubble background
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(dpToPx(16));

        // Set layout parameters
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));

        if (isUser) {
            // User message - blue, right aligned
            drawable.setColor(0xFF8358B2);
            messageView.setTextColor(0xFFFFFFFF);
            params.gravity = Gravity.END;
            params.leftMargin = dpToPx(50);
        } else {
            // AI message - light gray, left aligned
            drawable.setColor(0xFFE0E0E0);
            messageView.setTextColor(0xFF333333);
            params.gravity = Gravity.START;
            params.rightMargin = dpToPx(50);
        }

        messageView.setBackground(drawable);
        messageView.setLayoutParams(params);

        return messageView;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void getAIResponseWithContext(String userInput, TextView loadingMessage) {
        // Create client with longer timeout
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        try {
            // Create request body in Groq format
            JSONObject jsonBody = new JSONObject();

            // Messages array
            JSONArray messages = new JSONArray();

            // System message với context kết hợp user info và Firebase data
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful AI assistant for a movie booking app. " +
                    "Use the following data to answer user questions accurately:\n\n" +
                    currentUserContext + firebaseData +
                    "\n\n🎯 PRIORITY RULES:\n" +
                    "1. When user asks about 'my bookings', 'my orders', 'my comments', show ONLY items marked with ⭐\n" +
                    "2. When user uses 'I', 'me', 'my', refer to the CURRENT USER information\n" +
                    "3. Always provide specific, helpful answers based on this data\n" +
                    "4. If user is an ADMIN, they can see all data. If regular USER, prioritize their personal data\n" +
                    "5. Be friendly and use the user's name when appropriate");
            messages.put(systemMessage);

            // User message
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", userInput);
            messages.put(userMessage);

            // Request parameters
            jsonBody.put("messages", messages);
            jsonBody.put("model", "llama3-8b-8192");
            jsonBody.put("max_tokens", 1000);
            jsonBody.put("temperature", 0.7);
            jsonBody.put("stream", false);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + GROQ_API_KEY)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Network error: " + e.getMessage());
                    runOnUiThread(() -> {
                        loadingMessage.setText("❌ Connection error: " + e.getMessage() +
                                "\n💡 Check internet and try again!");
                        scrollToBottom();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    Log.d(TAG, "Response Code: " + response.code());
                    Log.d(TAG, "Response: " + responseData);

                    runOnUiThread(() -> {
                        String aiResponse;

                        if (!response.isSuccessful()) {
                            // Handle errors
                            switch (response.code()) {
                                case 401:
                                    aiResponse = "❌ Invalid API Key!\n" +
                                            "🔑 Get new key at: https://console.groq.com/keys";
                                    break;
                                case 429:
                                    aiResponse = "⏳ Too many requests!\n" +
                                            "💤 Wait 1 minute and try again.";
                                    break;
                                case 500:
                                    aiResponse = "🔧 Temporary server error!\n" +
                                            "🔄 Try again in a few seconds.";
                                    break;
                                default:
                                    aiResponse = "❌ Error " + response.code() +
                                            "\n📞 Contact support if error persists.";
                            }
                        } else {
                            // Parse successful response
                            try {
                                JSONObject jsonResponse = new JSONObject(responseData);
                                JSONArray choices = jsonResponse.getJSONArray("choices");

                                if (choices.length() > 0) {
                                    JSONObject firstChoice = choices.getJSONObject(0);
                                    JSONObject message = firstChoice.getJSONObject("message");
                                    String content = message.getString("content").trim();

                                    if (!content.isEmpty()) {
                                        aiResponse = content;
                                    } else {
                                        aiResponse = "🤔 AI has no response. Try asking differently!";
                                    }
                                } else {
                                    aiResponse = "⚠️ No response received from AI.";
                                }

                            } catch (JSONException e) {
                                Log.e(TAG, "JSON parse error: " + e.getMessage());
                                aiResponse = "🔍 Response processing error: " + e.getMessage();
                            }
                        }

                        // Update loading message
                        loadingMessage.setText(aiResponse);
                        scrollToBottom();
                    });
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "JSON creation error: " + e.getMessage());
            runOnUiThread(() -> {
                loadingMessage.setText("❌ Request creation error: " + e.getMessage());
                scrollToBottom();
            });
        }
    }
}
