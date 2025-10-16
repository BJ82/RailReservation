package com.rail.app.RailReservation.Enquiry.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TrainEnquiryResponse {

    private int trainNo;
    private String trainName;
    private String[] daysWhenRunning;
    private String[] availableClasses;
    private String src;
    private String dest;
    private String departTime;
    private String arvlTime;
}
