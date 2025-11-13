package com.rail.app.railreservation.trainmanagement.dto;


import com.rail.app.railreservation.common.enums.Day;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeTableAddRequest {

    private String trainName;

    private int trainNo;

    private List<Day> runOnDays = new ArrayList<>();

    private List<TrainTiming> trainTimings = new ArrayList<>();
}
