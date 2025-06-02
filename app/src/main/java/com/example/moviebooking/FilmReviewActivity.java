package com.example.moviebooking;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebooking.Comment_Review.CommentAdapter;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.Comment;
import com.example.moviebooking.dto.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class FilmReviewActivity extends AppCompatActivity {

    private static final String TAG = "FilmReviewActivity";

    private TextView tvTitle;
    private RecyclerView recyclerViewReviews;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    private FireBaseManager firebaseManager;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_review);

        // Khởi tạo views
        initViews();

        // Lấy thông tin user từ Intent
        getUserInfoFromIntent();

        // Khởi tạo Firebase Manager
        firebaseManager = FireBaseManager.getInstance();

        // Setup RecyclerView
        setupRecyclerView();

        // Load comments của user
        loadUserComments();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
    }

    private void getUserInfoFromIntent() {
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfoIntent");

        if (userInfo == null) {
            Log.e(TAG, "UserInfo is null");
            Toast.makeText(this, "Cannot get user information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Update title with username
        tvTitle.setText(userInfo.getUsername() + "'s Reviews");
        // Load avatar

    }

    private void setupRecyclerView() {
        commentList = new ArrayList<>();
        String profilePicUrl = userInfo.getProfilePic();
        commentAdapter = new CommentAdapter(this, commentList, profilePicUrl);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewReviews.setLayoutManager(layoutManager);
        recyclerViewReviews.setAdapter(commentAdapter);


    }

    private void loadUserComments() {
        if (userInfo == null || userInfo.getUsername() == null) {
            Toast.makeText(this, "Invalid user information", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị loading (có thể thêm ProgressBar)
        showLoading(true);

        firebaseManager.getCommentsByUsername(userInfo.getUsername(), new FireBaseManager.OnCommentsLoadedListener() {
            @Override
            public void onCommentsLoaded(List<Comment> comments) {
                showLoading(false);

                if (comments != null && !comments.isEmpty()) {
                    commentList.clear();
                    commentList.addAll(comments);
                    commentAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + comments.size() + " comments for user: " + userInfo.getUsername());
                } else {
                    // No comments found
                    commentList.clear();
                    commentAdapter.notifyDataSetChanged();
                    Toast.makeText(FilmReviewActivity.this, "No reviews found for this user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCommentsError(String error) {
                showLoading(false);
                Log.e(TAG, "Error loading comments: " + error);
                Toast.makeText(FilmReviewActivity.this, "Error loading reviews: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        // Có thể thêm ProgressBar để hiển thị loading
        // progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);

        if (isLoading) {
            Log.d(TAG, "Loading comments...");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup nếu cần
    }
}