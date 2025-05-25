package com.example.moviebooking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.UserInfo;
import com.example.moviebooking.home.DrawerListAdapter;
import com.example.moviebooking.home.MovieScrollerAdapter;
import com.example.moviebooking.home.MovieSliderAdapter;
import com.example.moviebooking.home.OnLogoutClickListener;
import com.example.moviebooking.home.SliderTranformer;
import com.google.android.material.slider.BaseOnSliderTouchListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnLogoutClickListener {

    private static final int SLIDER_DALAY_MS = 3000;
    private UserInfo userInfo;
    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();
    private ActivityResultLauncher<Intent> editProfileLauncher;
    private FireBaseManager fireBaseManager = FireBaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupUserInfo();
        launcherRegister();
        setOnClickViewAll();
        setDataForDrawer();
        setDataForMoviesBar(this);
        setDataForMoviesSlider(this);
    }

    @Override
    public void onLogoutClick() {
        finish();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void launcherRegister(){
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isUpdate = result.getData().getBooleanExtra("UpdateStat", false);
                        if(isUpdate) {
                            UserInfo updatedUser = (UserInfo) result.getData().getSerializableExtra("UserInfoIntent");
                            if (updatedUser != null) {
                                this.userInfo = updatedUser;
                                setOnClickViewAll();
                                setDataForDrawer();
                                setDataForMoviesBar(this);
                                setDataForMoviesSlider(this);
                            }
                        }
                    }
                }
        );

    }

    private void setupUserInfo() {
        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra("userinfoIntent");
        if(userInfo == null) {
            return;
        }
        Log.d("HomeActivity", "OnCreate: " + userInfo.getUsername());
    }

    private void setOnClickViewAll() {
        TextView viewAll = findViewById(R.id.viewAll);
        viewAll.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AllMovieActivity.class);
            intent.putExtra("userinfoIntent", userInfo);
            startActivity(intent);
        });

        ImageView userAva = findViewById(R.id.imgUser);

        userAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });
    }

    private void setDataForDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        RecyclerView drawerList = findViewById(R.id.right_drawer);

        List<String> drawerItems = new ArrayList<>();
        drawerItems.add(userInfo.getUsername());
        drawerItems.add("Booking History");
        drawerItems.add("Logout");

        drawerList.setAdapter(new DrawerListAdapter(this, userInfo, this, editProfileLauncher));
        drawerList.setLayoutManager(new LinearLayoutManager(this));

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerList.setClickable(false);
        drawerList.setFocusable(false);
        drawerList.setFocusableInTouchMode(false);
    }

   private void setDataForMoviesBar(Context context) {
        fireBaseManager.fetchNowShowingMoviesData(new FireBaseManager.OnMoviesDataLoadedListener() {
            @Override
            public void onMoviesDataLoaded(List<Movie> nowShowingMoviesList) {
                initializeMoviesBar(context, nowShowingMoviesList);

            }

            @Override
            public void onMoviesDataError(String errorMessage) {
                Log.d("HomeActivity", "onMoviesDataError: " + errorMessage);
            }
        });

   }

   private void initializeMoviesBar(Context context, List<Movie> nowShowingMoviesList) {
        RecyclerView moviesBarView = findViewById(R.id.rcv_all_movies);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        moviesBarView.setLayoutManager(linearLayoutManager);

        moviesBarView.setAdapter(new MovieScrollerAdapter(context, userInfo, nowShowingMoviesList));



   }

    private void setDataForMoviesSlider(Context context) {
        fireBaseManager.fetchNowShowingMoviesData(new FireBaseManager.OnMoviesDataLoadedListener() {
            @Override
            public void onMoviesDataLoaded(List<Movie> nowShowingMoviesList) {
                initializeMoviesSlider(context, nowShowingMoviesList);

            }

            @Override
            public void onMoviesDataError(String errorMessage) {
                Log.d("HomeActivity", "onMoviesDataError: " + errorMessage);
            }
        });

    }

    private void initializeMoviesSlider(Context context, List<Movie> nowShowingMoviesList) {
        viewPager2 = findViewById(R.id.vp_images_slider);

        ImageView ivBackground = findViewById(R.id.blur_background_image);

        viewPager2.setOrientation(viewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setOffscreenPageLimit(4);
        viewPager2.setPageTransformer(new SliderTranformer(viewPager2));

        MovieSliderAdapter adapter = new MovieSliderAdapter(context, userInfo, nowShowingMoviesList, viewPager2);

        viewPager2.setAdapter(adapter);

        adapter.setOnMovieChangeListener(movie -> {
            if (!(isDestroyed() || isFinishing())){
                Glide.with(context)
                        .load(movie.getThumbnail())
                        .transform(new jp.wasabeef.glide.transformations.BlurTransformation(25, 3)) // làm mờ
                        .into(ivBackground);
            }
        });

        // Gọi lần đầu để hiển thị background cho phim đầu tiên
        if (!nowShowingMoviesList.isEmpty()) {
            adapter.movieChangeListener.onMovieChanged(nowShowingMoviesList.get(0));
        }

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, SLIDER_DALAY_MS);

                //
                if (position >= 0 && position < nowShowingMoviesList.size()) {
                    Movie currentMovie = nowShowingMoviesList.get(position);
                    updateBackgroundImage(currentMovie.getThumbnail());
                } else {
                    Log.e("onPageSelected", "Position out of bounds: " + position + " / Size: " + nowShowingMoviesList.size());
                }
                //
            }
        });



    }
    private void updateBackgroundImage(String imageUrl) {
        ImageView backgroundImage = findViewById(R.id.blur_background_image);
        if (!isFinishing() && !isDestroyed()) {
            Glide.with(this)
                    .load(imageUrl)
                    .transform(new jp.wasabeef.glide.transformations.BlurTransformation(25, 3))
                    .into(backgroundImage);
        }
    }

    private Runnable sliderRunnable = () -> viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);

}