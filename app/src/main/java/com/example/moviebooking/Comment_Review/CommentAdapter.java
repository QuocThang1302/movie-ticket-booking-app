package com.example.moviebooking.Comment_Review;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebooking.R;
import com.example.moviebooking.dto.Comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private SimpleDateFormat dateFormat;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Set dữ liệu cho ViewHolder
        holder.tvUsername.setText(comment.getUsername());
        holder.tvCommentText.setText(comment.getContent());
        holder.tvMovieTitle.setText("Movie: " + comment.getMovieTitle());

        // Format và hiển thì thời gian
        if (comment.getTimestamp() > 0) {
            Date date = new Date(comment.getTimestamp());
            holder.tvTimestamp.setText(dateFormat.format(date));
        } else {
            holder.tvTimestamp.setText("Unknown time");
        }

        // Hiển thị rating nếu có
        if (comment.getRating() > 0) {
            holder.tvRating.setText("★ " + comment.getRating() + "/5");
            holder.tvRating.setVisibility(View.VISIBLE);
        } else {
            holder.tvRating.setVisibility(View.GONE);
        }

        // Set background cho item (tuỳ chọn)
        holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    // ViewHolder class
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvCommentText, tvMovieTitle, tvTimestamp, tvRating;
        ImageView ivUserAvatar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvRating = itemView.findViewById(R.id.tvRating);
            //ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);

        }
    }

    // Method để cập nhật dữ liệu
    public void updateComments(List<Comment> newComments) {
        this.commentList.clear();
        this.commentList.addAll(newComments);
        notifyDataSetChanged();
    }

    // Method để thêm comment mới
    public void addComment(Comment comment) {
        this.commentList.add(0, comment); // Thêm vào đầu danh sách
        notifyItemInserted(0);
    }

    // Method để xóa comment
    public void removeComment(int position) {
        if (position >= 0 && position < commentList.size()) {
            commentList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
