package com.rail.app.railreservation.booking.entity;


import com.rail.app.railreservation.commons.enums.JourneyClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="SeatCount")

@Getter
@Setter
@NoArgsConstructor
public class SeatCount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    private int trainNo;
    private LocalDate startDate;
    private LocalDate endDate;
    private JourneyClass journeyClass;
    private int seatCount;

    public SeatCount(int trainNo, LocalDate startDate, LocalDate endDate, JourneyClass journeyClass, int seatCount) {
        this.trainNo = trainNo;
        this.startDate = startDate;
        this.endDate = endDate;
        this.journeyClass = journeyClass;
        this.seatCount = seatCount;
    }
}
