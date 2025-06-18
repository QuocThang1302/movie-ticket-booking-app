package com.example.moviebooking.MovieManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<Movie> adapter;
    private List<Movie> movieList = new ArrayList<>();
    private FireBaseManager fireBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        listView = findViewById(R.id.movieListView);
        fireBaseManager = FireBaseManager.getInstance();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, movieList);
        listView.setAdapter(adapter);

        fireBaseManager.fetchAllMoviesData(new FireBaseManager.OnMoviesDataLoadedListener() {
            @Override
            public void onMoviesDataLoaded(List<Movie> allMovieList) {
                movieList.clear();
                movieList.addAll(allMovieList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onMoviesDataError(String errorMessage) {
                Toast.makeText(MovieListActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie selectedMovie = movieList.get(position);
            Intent intent = new Intent(MovieListActivity.this, UpdateDeleteMovieActivity.class);
            intent.putExtra("movie", selectedMovie);
            startActivity(intent);
        });

        findViewById(R.id.btnAddMovie).setOnClickListener(v -> {
            startActivity(new Intent(MovieListActivity.this, AddMovieActivity.class));
        });
    }
}
