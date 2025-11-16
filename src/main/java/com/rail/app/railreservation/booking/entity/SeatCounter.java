package com.rail.app.railreservation.booking.entity;


import com.rail.app.railreservation.common.enums.JourneyClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="SeatCounter")

@Getter
@Setter
@NoArgsConstructor
public class SeatCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    private int trainNo;
    private int routeID;
    private LocalDate startDate;
    private LocalDate endDate;
    private JourneyClass journeyClass;
    private int seatCount;
}
