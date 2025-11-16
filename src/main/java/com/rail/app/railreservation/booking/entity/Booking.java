package com.rail.app.railreservation.booking.entity;

import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.common.enums.JourneyClass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="Booking")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int pnr;

    private String name;
    private int age;
    private String sex;
    private int trainNo;
    private LocalDate startDt;
    private LocalDate endDt;
    private String from;
    private String to;
    private LocalDate dtOfJourny;
    private JourneyClass journeyClass;
    private BookingStatus bookingStatus;
    private int seatNo;
}
