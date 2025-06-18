package com.example.moviebooking.MovieManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.Movie;

import java.util.Arrays;

public class UpdateDeleteMovieActivity extends AppCompatActivity {
    private EditText titleInput, descriptionInput, durationInput, rateInput, yearInput, trailerInput, genresInput, thumbnailInput;
    private Button btnUpdate, btnDelete, btnRemoveNowShowing;
    private Movie currentMovie;
    private FireBaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_movie);

        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        durationInput = findViewById(R.id.durationInput);
        rateInput = findViewById(R.id.rateInput);
        yearInput = findViewById(R.id.yearInput);
        trailerInput = findViewById(R.id.trailerInput);
        genresInput = findViewById(R.id.genresInput);
        thumbnailInput = findViewById(R.id.thumbnailInput);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnRemoveNowShowing = findViewById(R.id.btnRemoveNowShowing);

        firebaseManager = FireBaseManager.getInstance();

        currentMovie = (Movie) getIntent().getSerializableExtra("movie");

        if (currentMovie != null) {
            titleInput.setText(currentMovie.getTitle());
            descriptionInput.setText(currentMovie.getDescription());
            durationInput.setText(currentMovie.getDuration());
            rateInput.setText(currentMovie.getRate());
            yearInput.setText(currentMovie.getYear());
            trailerInput.setText(currentMovie.getTrailerYoutube());
            thumbnailInput.setText(currentMovie.getThumbnail());
            genresInput.setText(TextUtils.join(",", currentMovie.getGenres()));
        }

        btnUpdate.setOnClickListener(v -> {
            currentMovie.setTitle(titleInput.getText().toString().trim());
            currentMovie.setDescription(descriptionInput.getText().toString().trim());
            currentMovie.setDuration(durationInput.getText().toString().trim());
            currentMovie.setRate(rateInput.getText().toString().trim());
            currentMovie.setYear(yearInput.getText().toString().trim());
            currentMovie.setTrailerYoutube(trailerInput.getText().toString().trim());
            currentMovie.setThumbnail(thumbnailInput.getText().toString().trim());

            String[] genresArr = genresInput.getText().toString().split(",");
            currentMovie.setGenres(Arrays.asList(genresArr));

            firebaseManager.updateMovie(currentMovie, new FireBaseManager.OnMovieOperationListener() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(UpdateDeleteMovieActivity.this, "Update successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(UpdateDeleteMovieActivity.this, "Error: " , Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(this, "Đã cập nhật phim", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnDelete.setOnClickListener(v -> {
            firebaseManager.deleteMovie(currentMovie.getMovieID(), new FireBaseManager.OnMovieOperationListener() {
                @Override
                public void onSuccess(String message) {
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(UpdateDeleteMovieActivity.this, "Error: " , Toast.LENGTH_SHORT).show();
                }
            });

        });

        btnRemoveNowShowing.setOnClickListener(v -> {
            firebaseManager.removeMovieFromNowShowing(currentMovie.getMovieID(), new FireBaseManager.OnMovieOperationListener() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(UpdateDeleteMovieActivity.this, "Add successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(UpdateDeleteMovieActivity.this, "Error: " , Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(this, "Delete successful", Toast.LENGTH_SHORT).show();
        });
    }
}
