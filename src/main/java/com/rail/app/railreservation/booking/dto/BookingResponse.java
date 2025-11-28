package com.rail.app.railreservation.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private int trainNo;
    private String trainName;
    private String from;
    private String to;
    private String journeyClass;
    private String doj;
    private Timestamp bookingDateTime;
    private List<BookedPassenger> passengerList = new ArrayList<>();

}
