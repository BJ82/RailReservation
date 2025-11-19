package com.rail.app.railreservation.booking.dto;

import com.rail.app.railreservation.booking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookedPassenger {

    private String name;
    private int age;
    private String sex;
    private int seatNo;
    private int pnr;
    private BookingStatus status;
}
