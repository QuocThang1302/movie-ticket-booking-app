package com.example.moviebooking.MovieManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moviebooking.R;
import com.example.moviebooking.MovieManager.MovieCustomAdapter;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {
    private ListView listView;
    private MovieCustomAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();
    private FireBaseManager fireBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        initViews();
        setupAdapter();
        loadMovieData();
        setupClickListeners();
    }

    private void initViews() {
        listView = findViewById(R.id.movieListView);
        fireBaseManager = FireBaseManager.getInstance();
    }

    private void setupAdapter() {
        adapter = new MovieCustomAdapter(this, movieList);
        listView.setAdapter(adapter);
    }

    private void loadMovieData() {
        fireBaseManager.fetchAllMoviesData(new FireBaseManager.OnMoviesDataLoadedListener() {
            @Override
            public void onMoviesDataLoaded(List<Movie> allMovieList) {
                movieList.clear();
                movieList.addAll(allMovieList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onMoviesDataError(String errorMessage) {
                Toast.makeText(MovieListActivity.this,
                        "Error loading movies: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
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

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadMovieData();
    }
}