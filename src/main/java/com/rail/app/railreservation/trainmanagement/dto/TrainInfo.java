package com.rail.app.railreservation.trainmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainInfo {

    private Integer trainNo;
    private String trainName;
    private List<String> stns = new ArrayList<>();
    private String deptTime;
    private String arrvTime;
}
