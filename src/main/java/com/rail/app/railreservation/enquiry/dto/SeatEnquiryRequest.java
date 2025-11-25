package com.rail.app.railreservation.enquiry.dto;

import com.rail.app.railreservation.common.enums.JourneyClass;

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
