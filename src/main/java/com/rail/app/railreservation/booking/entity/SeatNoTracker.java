package com.rail.app.railreservation.booking.entity;

import com.rail.app.railreservation.commons.enums.JourneyClass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="SeatNoTracker")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatNoTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int trainNo;
    private JourneyClass journeyClass;
    private LocalDate startDt;
    private LocalDate endDt;
    private int lstSeatNum;

    public SeatNoTracker(int trainNo, JourneyClass journeyClass, LocalDate startDt, LocalDate endDt, int lstSeatNum) {
        this.trainNo = trainNo;
        this.journeyClass = journeyClass;
        this.startDt = startDt;
        this.endDt = endDt;
        this.lstSeatNum = lstSeatNum;
    }
}
