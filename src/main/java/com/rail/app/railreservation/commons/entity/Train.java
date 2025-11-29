package com.rail.app.railreservation.commons.entity;


import com.rail.app.railreservation.commons.enums.Day;
import com.rail.app.railreservation.commons.enums.JourneyClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Train")

@Getter
@Setter
@NoArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer trainNo;

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
