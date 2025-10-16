package com.rail.app.RailReservation.Enquiry.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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
