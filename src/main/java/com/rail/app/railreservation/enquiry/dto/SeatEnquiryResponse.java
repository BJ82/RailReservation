package com.rail.app.railreservation.enquiry.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatEnquiryResponse {

    private int trainNo;
    private String trainName;
    private String doj;
    private String sleeper;
    private String AC1;
    private String AC2;
    private String AC3;
}
