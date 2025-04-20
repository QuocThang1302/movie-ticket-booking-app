package com.example.moviebooking.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Seat implements Parcelable {
    private String seatId;
    private boolean isBooked = false;
    private boolean isSelected = false;

    public Seat(String seatId, boolean isBooked, boolean isChosen) {
        this.seatId = seatId;
        this.isBooked = isBooked;
        this.isSelected = isChosen;
    }

    public Seat() {
    }

    protected Seat(Parcel in) {
        seatId = in.readString();
        isBooked = in.readByte() != 0;
        isSelected = in.readByte() != 0;
    }

    public static final Creator<Seat> CREATOR = new Creator<Seat>() {
        @Override
        public Seat createFromParcel(Parcel in) {
            return new Seat(in);
        }

        @Override
        public Seat[] newArray(int size) {
            return new Seat[size];
        }
    };

    public String getSeatId() {
        return seatId;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(seatId);
        dest.writeByte((byte) (isBooked ? 1 : 0));
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
