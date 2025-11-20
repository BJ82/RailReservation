package com.rail.app.railreservation.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenBookingRequest {

    private int trainNo;
    private String startDt;
    private String endDt;


}
