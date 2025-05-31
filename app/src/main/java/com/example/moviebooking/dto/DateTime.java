package com.example.moviebooking.dto;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Objects;

public class DateTime implements Serializable {
    String dayOfWeek;
    int day;
    int month;
    int year;
    int hour;
    int minute;

    boolean isSelected = false;
    boolean isDisable = false;

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
    }

    public boolean isDisable() {
        return isDisable;
    }

    public String getDay() {
        if (day < 10) {
            return "0" + day;
        }
        return "" + day;
    }

    public String getMonth() {
        String[] months = new DateFormatSymbols().getShortMonths();
        if (month >= 1 && month <= 12) {
            return months[month - 1].toUpperCase();
        }
        return "";
    }

    public String getYear() {
        return "" + year;
    }

    public String getHour() {
        if (hour < 10) {
            return "0" + hour;
        }
        return "" + hour;
    }

    public String getMinute() {
        if (minute < 10) {
            return "0" + minute;
        }
        return "" + minute;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getTimeAMPM() {
        if (hour < 12) {
            return getHour() + ":" + getMinute() + " AM";
        }
        return String.valueOf(hour - 12) + ":" + getMinute() + " PM";
    }

    public String getShortDate() {
        return getDayOfWeek() + ", " + getDay();
    }

    public DateTime(String dayOfWeek, int day, int month, int year, int hour, int minute) {
        this.dayOfWeek = dayOfWeek;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        String[] daysOfWeek = new String[]{"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
        this.dayOfWeek = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public String toString() {
        return dayOfWeek + "-" + day + "/" + month + "/" + year + "-" + hour + ":" + minute;
    }

    public DateTime setHoursFromDateTime(DateTime dateTime) {
        this.hour = dateTime.hour;
        this.minute = dateTime.minute;

        return this;
    }
    // input: "MONDAY-13/5/2025-14:30"
    public DateTime(String dateTimeString) {
        try {
            String[] parts = dateTimeString.split("-");
            this.dayOfWeek = parts[0];

            String[] dateParts = parts[1].split("/");
            this.day = Integer.parseInt(dateParts[0]);
            this.month = Integer.parseInt(dateParts[1]);
            this.year = Integer.parseInt(dateParts[2]);

            String[] timeParts = parts[2].split(":");
            this.hour = Integer.parseInt(timeParts[0]);
            this.minute = Integer.parseInt(timeParts[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public LocalDate toLocalDate() {
        return LocalDate.of(year, month, day);
    }
    public DateTime(LocalDate date) {
        this.day = date.getDayOfMonth();
        this.month = date.getMonthValue();
        this.year = date.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        String[] daysOfWeek = new String[]{"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
        this.dayOfWeek = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];

        // Giờ và phút mặc định là 0
        this.hour = 0;
        this.minute = 0;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DateTime that = (DateTime) obj;
        return this.day == that.day &&
                this.month == that.month &&
                this.year == that.year &&
                this.hour == that.hour &&
                this.minute == that.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, year, hour, minute);
    }
    public LocalTime toLocalTime() {
        return LocalTime.of(hour, minute);
    }

}

