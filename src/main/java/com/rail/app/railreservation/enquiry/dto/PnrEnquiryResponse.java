package com.rail.app.railreservation.enquiry.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PnrEnquiryResponse {

    private int pnrNo;
    private String status;
    private int trainNo;
    private String trainName;
    private String classBooked;
    private String doj;
}
