package com.example.moviebooking.dto;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookedTicketList implements Serializable {
    List<Ticket> bookedTicketList;

    public BookedTicketList(List<Ticket> bookedTicketList) {
        this.bookedTicketList = bookedTicketList;
    }

    public BookedTicketList() {
        bookedTicketList = new ArrayList<>();
    }

    public List<Ticket> getBookedTicketList() {
        return bookedTicketList;
    }

    public void setBookedTicketList(List<Ticket> bookedTicketList) {
        this.bookedTicketList = bookedTicketList;
    }

    public void addTicket(Ticket ticket) {
        bookedTicketList.add(ticket);
    }

    public String getCinemaName() {
        if (bookedTicketList.size() == 0) {
            return "";
        }
        return bookedTicketList.get(0).getCinemaId();
    }

    public String getHour() {
        if (bookedTicketList.size() == 0) {
            return "";
        }
        String dateTime = bookedTicketList.get(0).getDateTime();
        String[] dateTimeArr = dateTime.split("-");
        return dateTimeArr[dateTimeArr.length - 1];
    }

    public String getDate() {
        if (bookedTicketList.size() == 0) {
            return "";
        }
        String dateTime = bookedTicketList.get(0).getDateTime();
        String[] dateTimeArr = dateTime.split("-");
        String[] ddmmyyyy =  dateTimeArr[1].split("/");
        String[] mmName = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct","Nov","Dec"};
        return ddmmyyyy[0] + " " + mmName[Integer.parseInt(ddmmyyyy[1]) - 1];
    }

    public int countTicket() {
        return bookedTicketList.size();
    }

    public String getTicketID() {
        if (bookedTicketList.size() == 0) {
            return "";
        }
        return bookedTicketList.get(0).getId();
    }

    public Bitmap getQrCode(String data) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            return barcodeEncoder.encodeBitmap(data, BarcodeFormat.CODE_128, 1000, 300);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
