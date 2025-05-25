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

public class CommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        String movieTitle = getIntent().getStringExtra("title");
        String movieImageUrl = getIntent().getStringExtra("image_movie"); // nếu có

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
            String comment = editComment.getText().toString().trim();
            float rating = ratingBar.getRating();

            if (comment.isEmpty()) {
                Toast.makeText(this, "Please enter a comment.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this,
                    "Submitted:\nRating: " + rating + " stars\n",
                    Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
