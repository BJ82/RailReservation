package com.rail.app.railreservation.trainmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainTiming {

    private String station;

    private String arrvTime;

    private String deptTime;
}
