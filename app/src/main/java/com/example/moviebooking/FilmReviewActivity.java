package com.example.moviebooking;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebooking.home.FilmReviewAdapter;
import com.example.moviebooking.FilmReviewModel;

import java.util.ArrayList;
import java.util.List;

public class FilmReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FilmReviewAdapter reviewAdapter;
    private List<FilmReviewModel> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_review);

        recyclerView = findViewById(R.id.recyclerViewReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        reviewList = new ArrayList<>();
        reviewList.add(new FilmReviewModel("Do Mixue", "2 months ago", "Oi doi oi, oi doi oi, trinh la gi ma la rinh ai tam", 4f, R.drawable.ava,"Captain Marvels"));
        reviewList.add(new FilmReviewModel("Lionel Messi", "1 week ago", "I wanna meet this main character.", 1f, R.drawable.ava,"The Boys"));

        reviewAdapter = new FilmReviewAdapter(reviewList);
        recyclerView.setAdapter(reviewAdapter);
    }
}
