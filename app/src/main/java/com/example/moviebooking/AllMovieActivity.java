package com.example.moviebooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviebooking.allmovies.MovieGridAdapter;
import com.example.moviebooking.allmovies.MovieGridAdapter2;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class AllMovieActivity extends AppCompatActivity {

    private UserInfo userInfo = null;
    private List<Movie> moviesList = null;
    private RecyclerView allMoviesView;
    private SearchView searchView;
    private MovieGridAdapter2 movieGridAdapter;
    private FireBaseManager fireBaseManager = FireBaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_movie);
        initViews();
        getIntentData();
        setSearchViewListener();
        fetchDataForMoviesSlider();
    }

    private void initViews() {
        allMoviesView = findViewById(R.id.rcv_search_all_movies);
        searchView = findViewById(R.id.search_bar);
        searchView.clearFocus();

    }

    private void getIntentData() {
        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra("userinfoIntent");
        ImageView avaImg = findViewById(R.id.all_movie_ava);
        if (userInfo.getProfilePic() != null && !userInfo.getProfilePic().isEmpty()) {
            Glide.with(this)
                    .load(userInfo.getProfilePic())
                    .apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop())
                    .into(avaImg);
        } else {
            avaImg.setImageResource(R.drawable.icon_user_ava);
        }
    }

    private void setSearchViewListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMovies(newText);
                return true;
            }
        });
    }

    private void filterMovies(String newText) {
        // Kiểm tra null và empty cho moviesList
        if (moviesList == null || moviesList.isEmpty()) {
            Log.w("AllMovieActivity", "Movies list is null or empty, cannot filter");
            // Có thể hiển thị empty state hoặc reload data
            if (movieGridAdapter != null) {
                movieGridAdapter.setFilterList(new ArrayList<>());
            }
            return;
        }

        // Kiểm tra null cho adapter
        if (movieGridAdapter == null) {
            Log.e("AllMovieActivity", "Movie adapter is null");
            return;
        }

        List<Movie> filteredList = new ArrayList<>();

        // Kiểm tra nếu search text là null hoặc empty
        if (newText == null || newText.trim().isEmpty()) {
            // Nếu không có text tìm kiếm, hiển thị tất cả phim
            filteredList.addAll(moviesList);
        } else {
            // Lọc phim theo từ khóa
            String searchText = newText.toLowerCase().trim();
            for (Movie movie : moviesList) {
                // Kiểm tra null cho movie và title
                if (movie != null && movie.getTitle() != null) {
                    if (movie.getTitle().toLowerCase().contains(searchText)) {
                        filteredList.add(movie);
                    }
                }
            }
        }

        // Cập nhật adapter với danh sách đã lọc
        movieGridAdapter.setFilterList(filteredList);
    }

    private void fetchDataForMoviesSlider() {
        fireBaseManager.fetchAllMoviesData(new FireBaseManager.OnMoviesDataLoadedListener() {
            @Override
            public void onMoviesDataLoaded(List<Movie> movies) {
                moviesList = movies;
                Log.d("TAG", "allMovieList: " + moviesList.size());
                setupMoviesRecyclerView();
            }

            @Override
            public void onMoviesDataError(String errorMessage) {
                Log.d("TAG", errorMessage);
            }
        });
    }

    private  void setupMoviesRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        allMoviesView.setLayoutManager(linearLayoutManager);


        movieGridAdapter = new MovieGridAdapter2(this, userInfo, moviesList);
        allMoviesView.setAdapter(movieGridAdapter);
    }
}