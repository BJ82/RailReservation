package com.rail.app.railreservation.booking.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name="BookingOpen")

@Getter
@Setter
@NoArgsConstructor
public class BookingOpen {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int trainNo;
    private LocalDate startDt;
    private LocalDate endDt;
    private boolean isBookingOpen;
    private Timestamp timestamp;

    public BookingOpen(int trainNo, LocalDate startDt, LocalDate endDt, boolean isBookingOpen, Timestamp timestamp) {
        this.trainNo = trainNo;
        this.startDt = startDt;
        this.endDt = endDt;
        this.isBookingOpen = isBookingOpen;
        this.timestamp = timestamp;
    }
}
