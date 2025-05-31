package com.example.moviebooking.booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SnackOrder {
    private String orderId;
    private String userId;
    private String userName;
    private String movieTitle;
    private String cinemaName;
    private String showDate;
    private String showTime;
    private List<SnackOrderItem> snackItems;
    private double totalSnackPrice;
    private long orderTimestamp;
    private String orderStatus; // "pending", "confirmed", "completed"

    public SnackOrder() {
        // Constructor rỗng cho Firebase
        this.snackItems = new ArrayList<>();
        this.orderTimestamp = System.currentTimeMillis();
        this.orderStatus = "pending";
    }

    public SnackOrder(String userId, String userName, String movieTitle, String cinemaName,
                      String showDate, String showTime, Map<String, Integer> snackQuantities,
                      List<SnackItem> allSnackItems) {
        this();
        this.orderId = generateOrderId();
        this.userId = userId;
        this.userName = userName;
        this.movieTitle = movieTitle;
        this.cinemaName = cinemaName;
        this.showDate = showDate;
        this.showTime = showTime;

        // Chuyển đổi từ Map sang List SnackOrderItem
        this.snackItems = new ArrayList<>();
        this.totalSnackPrice = 0;

        for (SnackItem item : allSnackItems) {
            Integer quantity = snackQuantities.get(item.getName());
            if (quantity != null && quantity > 0) {
                SnackOrderItem orderItem = new SnackOrderItem(
                        item.getName(),
                        item.getPrice(),
                        quantity,
                        item.getPrice() * quantity
                );
                this.snackItems.add(orderItem);
                this.totalSnackPrice += orderItem.getTotalPrice();
            }
        }
    }

    private String generateOrderId() {
        return "SNACK_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getCinemaName() { return cinemaName; }
    public void setCinemaName(String cinemaName) { this.cinemaName = cinemaName; }

    public String getShowDate() { return showDate; }
    public void setShowDate(String showDate) { this.showDate = showDate; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public List<SnackOrderItem> getSnackItems() { return snackItems; }
    public void setSnackItems(List<SnackOrderItem> snackItems) { this.snackItems = snackItems; }

    public double getTotalSnackPrice() { return totalSnackPrice; }
    public void setTotalSnackPrice(double totalSnackPrice) { this.totalSnackPrice = totalSnackPrice; }

    public long getOrderTimestamp() { return orderTimestamp; }
    public void setOrderTimestamp(long orderTimestamp) { this.orderTimestamp = orderTimestamp; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
}

// Model class cho từng item trong đơn hàng
class SnackOrderItem {
    private String itemName;
    private double unitPrice;
    private int quantity;
    private double totalPrice;

    public SnackOrderItem() {
        // Constructor rỗng cho Firebase
    }

    public SnackOrderItem(String itemName, double unitPrice, int quantity, double totalPrice) {
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
