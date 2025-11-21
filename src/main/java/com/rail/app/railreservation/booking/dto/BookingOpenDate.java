package com.rail.app.railreservation.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingOpenDate {

    private LocalDate startDt;
    private LocalDate endDt;
}
