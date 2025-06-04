package com.example.moviebooking.booking;

import java.io.Serializable;

public class SnackItem implements Serializable {
    private final String name;
    private final double price;
    private final int imageResId;

    public SnackItem(String name, double price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
} 