package com.rail.app.railreservation.trainmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainAddResponse {
    private int trainNo = -1;
    private String trainName;
    private String src;
    private String dest;
    private boolean isTrainAdded=false;

}
