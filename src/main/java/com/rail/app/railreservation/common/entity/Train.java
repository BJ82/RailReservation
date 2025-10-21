package com.rail.app.railreservation.common.entity;


import com.rail.app.railreservation.common.enums.Day;
import com.rail.app.railreservation.common.enums.JourneyClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="TrainAddRequest")

@Getter
@Setter
@NoArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int trainNo;

    private String trainName;

    private int routeId;


    @ElementCollection
    @CollectionTable(name = "run_on_days" , joinColumns = @JoinColumn(name = "train_run_day"))
    @Column(name = "day")
    private List<Day> runOnDays = new ArrayList<>();

    private String deptTime;

    private String arrvTime;

    @ElementCollection
    @CollectionTable(name = "journy_class_types" , joinColumns = @JoinColumn(name = "journy_class"))
    @Column(name = "class")
    private List<JourneyClass> avblJournyClass = new ArrayList<>();

}
