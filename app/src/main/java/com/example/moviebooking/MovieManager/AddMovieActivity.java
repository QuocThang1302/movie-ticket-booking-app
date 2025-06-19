package com.example.moviebooking.MovieManager;

import android.os.Bundle;
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
import java.util.List;
import java.util.UUID;

public class AddMovieActivity extends AppCompatActivity {
    private EditText titleInput, descriptionInput, durationInput, rateInput, yearInput, trailerInput, genresInput, thumbnailInput;
    private Button btnAddMovie;
    private FireBaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        durationInput = findViewById(R.id.durationInput);
        rateInput = findViewById(R.id.rateInput);
        yearInput = findViewById(R.id.yearInput);
        trailerInput = findViewById(R.id.trailerInput);
        genresInput = findViewById(R.id.genresInput);
        thumbnailInput = findViewById(R.id.thumbnailInput);
        btnAddMovie = findViewById(R.id.btnAddMovie);

        firebaseManager = FireBaseManager.getInstance();

        btnAddMovie.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String duration = durationInput.getText().toString().trim();
            String rate = rateInput.getText().toString().trim();
            String year = yearInput.getText().toString().trim();
            String trailer = trailerInput.getText().toString().trim();
            String thumbnail = thumbnailInput.getText().toString().trim();
            String[] genresArr = genresInput.getText().toString().split(",");
            List<String> genres = Arrays.asList(genresArr);

            Movie movie = new Movie(id, title, description, thumbnail, genres, duration, rate, trailer);
            movie.setYear(year);

            firebaseManager.addMovie(movie, true, new FireBaseManager.OnMovieOperationListener() {
                @Override
                public void onSuccess(String msg) {
                    Toast.makeText(AddMovieActivity.this, "Add successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String msg) {
                    Toast.makeText(AddMovieActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}
