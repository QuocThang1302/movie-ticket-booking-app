package com.example.moviebooking.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviebooking.FilmReviewActivity;
import com.example.moviebooking.booking.BookingHistoryActivity;
import com.example.moviebooking.R;
import com.example.moviebooking.dto.UserInfo;

import java.util.Arrays;
import java.util.List;
import com.example.moviebooking.Trailer.ReviewActivity;

public class DrawerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> drawerItems;
    private UserInfo userInfo;
    private Context context;
    private OnLogoutClickListener logoutClickListener;
    private int viewType;
    private ActivityResultLauncher<Intent> launcher;
    private static final int HEADER_VIEW = 0;
    private static final int SETTING_VIEW = 1;

    public DrawerListAdapter(Context context, UserInfo userInfo, OnLogoutClickListener listener, ActivityResultLauncher<Intent> launcher) {
        this.context = context;
        this.userInfo = userInfo;
        this.drawerItems = Arrays.asList("Username", "Booking History", "Write a Review", "Your Reviews", "Logout","Contact Us");

        this.logoutClickListener = listener;
        this.launcher = launcher;
    }
    @Override
    public int getItemViewType(int position){
        return position == 0 ? HEADER_VIEW : SETTING_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == HEADER_VIEW)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header_drawer, parent, false);
            return new HeaderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_drawer, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String item = drawerItems.get(position);

        if(getItemViewType(position) == HEADER_VIEW) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.textViewHeaderItem.setText(userInfo.getName());
            headerViewHolder.imageViewHeaderItem.setImageResource(R.drawable.icon_user_ava);

            if (userInfo.getProfilePic() != null && !userInfo.getProfilePic().isEmpty()) {
                Glide.with(context)
                        .load(userInfo.getProfilePic())
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop())
                        .into(headerViewHolder.imageViewHeaderItem);
            } else {
                headerViewHolder.imageViewHeaderItem.setImageResource(R.drawable.icon_user_ava);
            }

            headerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserProfile.class);
                    intent.putExtra("userinfoIntent", userInfo);
                    launcher.launch(intent);
                }
            });

        } else{
            ViewHolder viewHolder = (ViewHolder) holder;

            if(position == 1) {
                viewHolder.textViewItem.setText("Booking History");
                viewHolder.imageViewItem.setImageResource(R.drawable.icon_history);
                viewHolder.textViewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, BookingHistoryActivity.class);
                        intent.putExtra("userinfoIntent", userInfo);
                        context.startActivity(intent);
                    }
                });

            } else if (position == 2) {
                viewHolder.textViewItem.setText("Write a Review");
                viewHolder.imageViewItem.setImageResource(R.drawable.icon_review);

                viewHolder.textViewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ReviewActivity.class);
                        intent.putExtra("userinfoIntent", userInfo);
                        context.startActivity(intent);
                    }
                });

            }
            else if (position == 3) {
                viewHolder.textViewItem.setText("Your Reviews");
                viewHolder.imageViewItem.setImageResource(R.drawable.icon_film_review);

                viewHolder.textViewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FilmReviewActivity.class);
                        intent.putExtra("userinfoIntent", userInfo);
                        context.startActivity(intent);
                    }
                });
            }else if (position == 4) { // Contact Us
                viewHolder.textViewItem.setText("Contact Us");
                viewHolder.imageViewItem.setImageResource(R.drawable.icon_contact_us);

                viewHolder.textViewItem.setOnClickListener(v -> showContactDialog());
            }
            else if (position == 5) {
                viewHolder.textViewItem.setText("Logout");
                viewHolder.imageViewItem.setImageResource(R.drawable.icon_logout);
                viewHolder.textViewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(logoutClickListener != null) {
                            logoutClickListener.onLogoutClick();
                        }
                    }
                });
            } else if (position == 4) { // Contact Us
                viewHolder.textViewItem.setText("Contact Us");
                viewHolder.imageViewItem.setImageResource(R.drawable.icon_contact_us);

                viewHolder.textViewItem.setOnClickListener(v -> showContactDialog());
            }

        }

        //customizeTextViewItem(position, holder.textViewItem);
    }

    @Override
    public int getItemCount() {
        return drawerItems.size();
    }

    private void customizeTextViewItem(int position, TextView textViewItem) {

    }
    private void showContactDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_contact_us, null);

        ImageView fbIcon = dialogView.findViewById(R.id.img_fb);
        ImageView insIcon = dialogView.findViewById(R.id.img_ins);

        fbIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/curyhao123/"));
            context.startActivity(intent);
        });

        insIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/trananhhao133/"));
            context.startActivity(intent);
        });

        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Contact Us")
                .setView(dialogView)
                .setNegativeButton("Close", null)
                .show();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;
        ImageView imageViewItem;

        ViewHolder(View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.username);
            imageViewItem = itemView.findViewById(R.id.imgUserDrawer);
        }
    }
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewHeaderItem;
        ImageView imageViewHeaderItem;

        HeaderViewHolder(View itemView) {
            super(itemView);
            textViewHeaderItem = itemView.findViewById(R.id.usernameHeader);
            imageViewHeaderItem = itemView.findViewById(R.id.imgUserDrawerHeader);
        }
    }
}
