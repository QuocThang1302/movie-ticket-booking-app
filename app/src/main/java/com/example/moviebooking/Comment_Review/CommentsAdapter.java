package com.example.moviebooking.Comment_Review;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviebooking.R;
import com.example.moviebooking.data.FireBaseManager;
import com.example.moviebooking.dto.Comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> comments;
    private FireBaseManager fireBaseManager = FireBaseManager.getInstance();

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment2, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);

        // Set username
        holder.tvUsername.setText(comment.getUsername() != null ? comment.getUsername() : "Anonymous");

        // Set comment content
        holder.tvCommentContent.setText(comment.getContent() != null ? comment.getContent() : "");

        // Set timestamp
        holder.tvTimestamp.setText(formatTimestamp(comment.getTimestamp()));

        // Set rating stars
        setRatingStars(holder, comment.getRating());
        // Load profile picture
        fireBaseManager.getUserProfilePicFromComment(comment, new FireBaseManager.OnUserProfilePicLoadedListener() {
            @Override
            public void onProfilePicLoaded(String profilePicUrl) {
                Glide.with(context)
                        .load(profilePicUrl)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .placeholder(R.drawable.ava)
                                .error(R.drawable.ava))
                        .into(holder.imgAvatar);
            }

            @Override
            public void onProfilePicError(String errorMessage) {
                holder.imgAvatar.setImageResource(R.drawable.ava);
            }

            @Override
            public void onProfilePicNotFound() {
                holder.imgAvatar.setImageResource(R.drawable.ava);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    private String formatTimestamp(long timestamp) {
        if (timestamp == 0) {
            return "Unknown time";
        }

        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - timestamp;

        // Convert to minutes, hours, days
        long minutes = timeDiff / (1000 * 60);
        long hours = timeDiff / (1000 * 60 * 60);
        long days = timeDiff / (1000 * 60 * 60 * 24);

        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (days < 7) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else {
            // For older comments, show actual date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }

    private void setRatingStars(CommentViewHolder holder, float rating) {
        ImageView[] stars = {
                holder.star1, holder.star2, holder.star3, holder.star4, holder.star5
        };

        // Reset all stars to empty
        for (ImageView star : stars) {
            star.setImageResource(R.drawable.ic_star_empty);
        }

        // Fill stars based on rating
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5f;

        // Fill full stars
        for (int i = 0; i < fullStars && i < 5; i++) {
            stars[i].setImageResource(R.drawable.ic_star_filled);
        }

        // Fill half star if needed
        if (hasHalfStar && fullStars < 5) {
            stars[fullStars].setImageResource(R.drawable.ic_star_half);
        }
    }

    public void updateComments(List<Comment> newComments) {
        this.comments = newComments;
        notifyDataSetChanged();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvTimestamp, tvCommentContent;
        ImageView star1, star2, star3, star4, star5;
        ImageView imgAvatar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvCommentContent = itemView.findViewById(R.id.tv_comment_content);
            star1 = itemView.findViewById(R.id.star1);
            star2 = itemView.findViewById(R.id.star2);
            star3 = itemView.findViewById(R.id.star3);
            star4 = itemView.findViewById(R.id.star4);
            star5 = itemView.findViewById(R.id.star5);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
