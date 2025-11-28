package com.rail.app.railreservation.trainmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationTiming {

    private String stationName;
    private String arrivalTime;
    private String departureTime;

}
