package com.rail.app.RailReservation.Booking.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookedResponse {

    private int bookingID;
    private String bookingTime;
    private String bookingDate
    private int trainNo;
    private String trainName;
    private String from;
    private String to;
    private String journeyClass;
    private String doj;
    private List<BookedPassenger> passengerList = new ArrayList<>();

}
