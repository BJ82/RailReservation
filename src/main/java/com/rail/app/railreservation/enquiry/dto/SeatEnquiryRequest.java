package com.rail.app.railreservation.enquiry.dto;

import com.rail.app.railreservation.commons.enums.JourneyClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatEnquiryRequest {

    private int trainNo;
    private String trainName;
    private String startDt;
    private String endDt;
    private String from;
    private String to;
    private JourneyClass journeyClass;
    private String doj;

}
