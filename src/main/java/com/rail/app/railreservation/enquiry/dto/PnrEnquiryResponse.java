package com.rail.app.railreservation.enquiry.dto;


import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.common.enums.JourneyClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PnrEnquiryResponse {

    private int trainNo;
    private LocalDate dtOfJourny;
    private String name;
    private BookingStatus bookingStatus;
    private JourneyClass journeyClass;

}
