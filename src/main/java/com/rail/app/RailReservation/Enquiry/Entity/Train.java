package com.rail.app.RailReservation.Enquiry.Entity;


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
    private int trainNo;

    private String trainName;

    private int routeId;

    private String[] runsOnDays = new String[7];

    private String deptTime;

    private String arrvTime;

    @ElementCollection
    private List<String> availableJourneyClass = new ArrayList<>();

}
