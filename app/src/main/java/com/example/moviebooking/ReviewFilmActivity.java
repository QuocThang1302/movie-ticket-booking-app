package com.example.moviebooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.moviebooking.Comment_Review.CommentsAdapter;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.Comment;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class ReviewFilmActivity extends AppCompatActivity implements FireBaseManager.OnCommentsLoadedListener {

    private static final String TAG = "ReviewFilmActivity";

    // UI Components
    private ImageView imgMoviePoster;
    private TextView tvMovieTitle, tvMovieYear, tvMovieDuration, tvMovieRating, tvMovieGenres, tvMovieDescription;
    private TextView tvCommentsCount, tvNoComments;
    private ProgressBar progressBar;
    private RecyclerView rvComments;

    // Data
    private Movie receivedMovie;
    private UserInfo userInfo;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentsList;
    private FireBaseManager fireBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_film);

        initViews();
        initData();
        setupRecyclerView();
        loadMovieData();
        loadComments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo UI được cập nhật khi activity resume
        if (receivedMovie != null) {
            loadMovieData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Hủy các task đang chạy để tránh memory leak
        if (fireBaseManager != null) {
            // Nếu FireBaseManager có method để cancel tasks
            // fireBaseManager.cancelPendingTasks();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
        if (commentsAdapter != null) {
            commentsAdapter = null;
        }
        if (commentsList != null) {
            commentsList.clear();
        }
    }

    @Override
    public void onBackPressed() {
        // Xử lý nút back một cách mượt mà
        super.onBackPressed();
        // Thêm animation transition nếu cần
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void initViews() {
        // Movie info views
        imgMoviePoster = findViewById(R.id.img_movie_poster);
        tvMovieTitle = findViewById(R.id.tv_movie_title);
        tvMovieYear = findViewById(R.id.tv_movie_year);
        tvMovieDuration = findViewById(R.id.tv_movie_duration);
        tvMovieRating = findViewById(R.id.tv_movie_rating);
        tvMovieGenres = findViewById(R.id.tv_movie_genres);
        tvMovieDescription = findViewById(R.id.tv_movie_description);

        // Comments views
        tvCommentsCount = findViewById(R.id.tv_comments_count);
        tvNoComments = findViewById(R.id.tv_no_comments);
        progressBar = findViewById(R.id.progress_bar);
        rvComments = findViewById(R.id.rv_comments);
    }

    private void initData() {
        // Get movie data from intent
        Intent intent = getIntent();
        receivedMovie = (Movie) intent.getSerializableExtra("movie");

        if (receivedMovie == null) {
            Toast.makeText(this, "Error: Movie data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase manager
        fireBaseManager = FireBaseManager.getInstance();
        commentsList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        commentsAdapter = new CommentsAdapter(this, commentsList);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentsAdapter);
        rvComments.setNestedScrollingEnabled(false);
    }

    private void loadMovieData() {
        if (receivedMovie == null) return;

        // Set movie title
        tvMovieTitle.setText(receivedMovie.getTitle() != null ? receivedMovie.getTitle() : "Unknown Title");

        // Set movie year
        tvMovieYear.setText("Year: " + (receivedMovie.getYear() != null ? receivedMovie.getYear() : "Unknown"));

        // Set movie duration
        tvMovieDuration.setText("Duration: " + (receivedMovie.getDuration() != null ? receivedMovie.getDuration() : "Unknown"));

        // Set movie rating
        tvMovieRating.setText("Rating: " + (receivedMovie.getRate() != null ? receivedMovie.getRate() + "/5" : "No rating"));

        // Set movie genres
        if (receivedMovie.getGenres() != null && !receivedMovie.getGenres().isEmpty()) {
            StringBuilder genresText = new StringBuilder("Genres: ");
            for (int i = 0; i < receivedMovie.getGenres().size(); i++) {
                genresText.append(receivedMovie.getGenres().get(i));
                if (i < receivedMovie.getGenres().size() - 1) {
                    genresText.append(", ");
                }
            }
            tvMovieGenres.setText(genresText.toString());
        } else {
            tvMovieGenres.setText("Genres: Unknown");
        }

        // Set movie description
        tvMovieDescription.setText(receivedMovie.getDescription() != null ? receivedMovie.getDescription() : "No description available.");

        // Load movie poster
        if (receivedMovie.getThumbnail() != null && !receivedMovie.getThumbnail().isEmpty()) {
            Glide.with(this)
                    .load(receivedMovie.getThumbnail())
                    .placeholder(receivedMovie.getThumbnailUri())
                    .error(receivedMovie.getThumbnailUri())
                    .into(imgMoviePoster);
        } else {
            imgMoviePoster.setImageResource(receivedMovie.getThumbnailUri());
        }
    }

    private void loadComments() {
        if (receivedMovie == null || receivedMovie.getMovieID() == null) {
            showNoComments();
            return;
        }

        showLoading();
        fireBaseManager.getCommentsByMovieId(receivedMovie.getMovieID(), this);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvComments.setVisibility(View.GONE);
        tvNoComments.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showComments() {
        hideLoading();
        rvComments.setVisibility(View.VISIBLE);
        tvNoComments.setVisibility(View.GONE);
    }

    private void showNoComments() {
        hideLoading();
        rvComments.setVisibility(View.GONE);
        tvNoComments.setVisibility(View.VISIBLE);
    }

    private void updateCommentsCount(int count) {
        tvCommentsCount.setText("(" + count + ")");
    }

    @Override
    public void onCommentsLoaded(List<Comment> comments) {
        Log.d(TAG, "Comments loaded successfully. Count: " + (comments != null ? comments.size() : 0));

        if (comments != null && !comments.isEmpty()) {
            commentsList.clear();
            commentsList.addAll(comments);
            commentsAdapter.updateComments(commentsList);
            updateCommentsCount(comments.size());
            showComments();
        } else {
            commentsList.clear();
            commentsAdapter.updateComments(commentsList);
            updateCommentsCount(0);
            showNoComments();
        }
    }

    @Override
    public void onCommentsError(String error) {
        Log.e(TAG, "Error loading comments: " + error);
        hideLoading();
        showNoComments();
        Toast.makeText(this, "Failed to load reviews: " + error, Toast.LENGTH_SHORT).show();
    }
}