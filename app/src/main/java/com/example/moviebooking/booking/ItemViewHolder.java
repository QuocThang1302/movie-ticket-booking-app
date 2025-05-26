package com.example.moviebooking.booking;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebooking.R;

import java.util.Map;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvSnackName;
    private final TextView tvSnackPrice;
    private final ImageView ivSnackImage;
    private final TextView tvQuantity;
    private final ImageView ivDecrease;
    private final ImageView ivIncrease;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        tvSnackName = itemView.findViewById(R.id.tv_snack_name);
        tvSnackPrice = itemView.findViewById(R.id.tv_snack_price);
        ivSnackImage = itemView.findViewById(R.id.iv_snack_image);
        tvQuantity = itemView.findViewById(R.id.tv_quantity);
        ivDecrease = itemView.findViewById(R.id.iv_decrease);
        ivIncrease = itemView.findViewById(R.id.iv_increase);
    }

    public void bind(SnackItem item, Map<String, Integer> snackQuantities, SnackSelectionActivity activity) {
        tvSnackName.setText(item.getName());
        tvSnackPrice.setText("$" + String.format("%.2f", item.getPrice()));
        ivSnackImage.setImageResource(item.getImageResId());
        tvQuantity.setText(String.valueOf(snackQuantities.getOrDefault(item.getName(), 0)));

        ivDecrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString());
            if (quantity > 0) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                activity.updateSnackPrice(-item.getPrice());
                snackQuantities.put(item.getName(), quantity);
                if (quantity == 0) {
                    activity.removeSelectedCombo(item.getName());
                    snackQuantities.remove(item.getName());
                }
                activity.updateSnackCount();
                activity.updatePaymentInfo();
            }
        });

        ivIncrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString());
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            activity.updateSnackPrice(item.getPrice());
            snackQuantities.put(item.getName(), quantity);
            if (quantity == 1) {
                activity.addSelectedCombo(item.getName());
            }
            activity.updateSnackCount();
            activity.updatePaymentInfo();
        });
    }
} 