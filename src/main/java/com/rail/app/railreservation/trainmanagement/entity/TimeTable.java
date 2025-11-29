package com.rail.app.railreservation.trainmanagement.entity;

import com.rail.app.railreservation.commons.enums.Day;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="TimeTable")

@Getter
@Setter
@NoArgsConstructor
public class TimeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Integer timeTableID;

    private String trainName;

    private int trainNo;

    @ElementCollection
    @CollectionTable(name = "TrainRunDays" , joinColumns = @JoinColumn(name = "TIME_TABLE_ID"))
    @Column(name = "DAY")
    private List<Day> runOnDays = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "TrainStationTimings" , joinColumns = @JoinColumn(name = "TIME_TABLE_ID"))
    private List<Timing> trainTimings = new ArrayList<>();
}
