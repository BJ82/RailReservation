package com.rail.app.railreservation.enquiry.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TrainEnquiryResponse {

    private int trainNo;
    private String trainName;
    private String[] runOnDays;
    private String[] avblJournyClass;
    private String src;
    private String dest;
    private String deptTime;
    private String arrvTime;
}
