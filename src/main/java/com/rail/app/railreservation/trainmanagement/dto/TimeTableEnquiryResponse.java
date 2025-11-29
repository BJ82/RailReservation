package com.rail.app.railreservation.trainmanagement.dto;

import com.rail.app.railreservation.commons.enums.Day;
import com.rail.app.railreservation.trainmanagement.entity.Timing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeTableEnquiryResponse {

    private int trainNo;
    private List<Day> runOnDays = new ArrayList<>();
    private List<Timing> trainTimings = new ArrayList<>();
}
