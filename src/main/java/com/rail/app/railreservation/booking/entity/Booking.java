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
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int pnr;

    private String name;
    private int age;
    private String sex;
    private int trainNo;
    private String from;
    private String to;
    private LocalDate dtOfJourny;
    private JourneyClass journeyClass;
    private BookingStatus bookingStatus;
    private int seatNo;


    public Booking(String name, int age, String sex, int trainNo, String from, String to, LocalDate dtOfJourny, JourneyClass journeyClass, BookingStatus bookingStatus, int seatNo) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.trainNo = trainNo;
        this.from = from;
        this.to = to;
        this.dtOfJourny = dtOfJourny;
        this.journeyClass = journeyClass;
        this.bookingStatus = bookingStatus;
        this.seatNo = seatNo;
    }
}
