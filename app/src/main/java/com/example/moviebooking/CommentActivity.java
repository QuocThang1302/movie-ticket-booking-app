package com.example.moviebooking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.Comment;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.UserInfo;

public class CommentActivity extends AppCompatActivity {
    private Movie movie;
    private FireBaseManager fireBaseManager;
    private String currentUsername;
    private UserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        fireBaseManager = FireBaseManager.getInstance();

        movie = (Movie) getIntent().getSerializableExtra("movie");
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfoIntent");
        if (userInfo == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        else
        {
            currentUsername = userInfo.getUsername();
        }
        ImageView imageView = findViewById(R.id.image_movie);
        String movieTitle = null;
        if (movie != null) {
            String movieImageUrl = movie.getThumbnail();
            movieTitle = movie.getTitle();
            Glide.with(this).load(movieImageUrl).into(imageView);
        }

        TextView tvMovieTitle = findViewById(R.id.title);
        EditText editComment = findViewById(R.id.editComment);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        if (movieTitle != null) {
            tvMovieTitle.setText("Write a comment for: " + movieTitle);
        } else {
            tvMovieTitle.setText("Write a comment");
        }


        btnSubmit.setOnClickListener(v -> {
            String commentContent = editComment.getText().toString().trim();
            float rating = ratingBar.getRating();

            // Kiểm tra validation
            if (commentContent.isEmpty()) {
                Toast.makeText(this, "Please enter a comment.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rating == 0) {
                Toast.makeText(this, "Please select a rating.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (movie == null || movie.getMovieID() == null) {
                Toast.makeText(this, "Movie information is missing.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo comment mới
            long currentTimestamp = System.currentTimeMillis();
            Comment newComment = new Comment(
                    movie.getMovieID(),
                    movie.getTitle(),
                    commentContent,
                    rating,
                    currentTimestamp,
                    currentUsername
            );

            // Lưu comment lên Firebase
            saveCommentToFirebase(newComment, editComment, ratingBar);
        });
    }

    private void saveCommentToFirebase(Comment comment, EditText editComment, RatingBar ratingBar) {

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Submitting...");

        fireBaseManager.saveComment(comment, new FireBaseManager.OnCommentOperationListener() {
            @Override
            public void onCommentSaved(boolean success, String message) {
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(CommentActivity.this,
                                "Comment submitted successfully!",
                                Toast.LENGTH_SHORT).show();

                        // Clear form sau khi submit thành công
                        editComment.setText("");
                        ratingBar.setRating(0);

                        finish();
                    } else {
                        Toast.makeText(CommentActivity.this,
                                "Failed to submit comment: " + message,
                                Toast.LENGTH_SHORT).show();
                    }

                    // Enable lại button
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit");
                });
            }
        });
    }
}
