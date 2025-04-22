package com.example.moviebooking.home;

import android.adservices.adselection.UpdateAdCounterHistogramRequest;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviebooking.R;
import com.example.moviebooking.dto.Movie;
import com.example.moviebooking.dto.UserInfo;
import com.example.moviebooking.moviepage.MoviePageActivity;

import java.util.List;

public class MovieScrollerAdapter extends RecyclerView.Adapter<MovieScrollerAdapter.MovieViewHolder> {


    private Context mContext;
    private UserInfo userInfo;
    private List<Movie> mListMovie;
    public MovieScrollerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public MovieScrollerAdapter(Context mContext, UserInfo userInfo, List<Movie> mListMovie) {
        this.mContext = mContext;
        this.mListMovie = mListMovie;
        this.userInfo = userInfo;
    }

    public void setData(List<Movie> list) {
        this.mListMovie = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_movies, parent);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = mListMovie.get(position);
        if(movie == null) {
            return;
        }

        Glide.with(mContext).load(movie.getThumbnail()).override(270,410).into(holder.imgMovie);

        //

        holder.tvTitle.setText(movie.getTitle());
        holder.tvNote.setText(movie.getDetailDuration());

        holder.imgMovie.setOnClickListener(v -> {
            Intent intent = new Intent(this.mContext, MoviePageActivity.class);
            intent.putExtra("userinfoIntent", userInfo);
            intent.putExtra("movie", movie);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if(mListMovie != null) {
            return mListMovie.size();
        }
        return 0;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgMovie;
        private TextView tvTitle;
        private TextView tvNote;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMovie = itemView.findViewById(R.id.image_movie);
            tvTitle = itemView.findViewById(R.id.title);
            tvNote = itemView.findViewById(R.id.note_text);
        }
    }
}
