package com.rail.app.railreservation.trainmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.rail.app.railreservation.common.enums.Day;
import com.rail.app.railreservation.common.enums.JourneyClass;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainAddRequest {

    //private int trainNo;
    private List<String> stations;
    private List<Day> runOnDays;
    private LocalTime deptTime;
    private LocalTime arrvTime;
    private List<JourneyClass> avblJournyClass;
    private String trainName;
}
