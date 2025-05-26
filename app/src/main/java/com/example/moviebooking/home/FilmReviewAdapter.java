package com.example.moviebooking.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebooking.R;
import com.example.moviebooking.FilmReviewModel;

import java.util.List;

public class FilmReviewAdapter extends RecyclerView.Adapter<FilmReviewAdapter.ReviewViewHolder> {

    private List<FilmReviewModel> reviewList;

    public FilmReviewAdapter(List<FilmReviewModel> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        FilmReviewModel review = reviewList.get(position);
        holder.tvUsername.setText(review.getUsername());
        holder.tvTime.setText(review.getTime());
        holder.editComment.setText(review.getComment());
        holder.ratingBar.setRating(review.getRating());
        holder.tvMovieTitle.setText(review.getMovieTitle());


        if (review.getImageResId() != 0) {
            holder.imgReview.setImageResource(review.getImageResId());
            holder.imgReview.setVisibility(View.VISIBLE);
        } else {
            holder.imgReview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvTime, editComment;
        RatingBar ratingBar;
        ImageView imgReview;
        TextView tvMovieTitle;


        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTime = itemView.findViewById(R.id.tvTime);
            editComment = itemView.findViewById(R.id.editComment);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imgReview = itemView.findViewById(R.id.imgReview);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);

        }
    }
}
