package com.rail.app.railreservation.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingOpenInfo {

    private int trainNo;
    private List<BookingOpenDate> dates;
}
