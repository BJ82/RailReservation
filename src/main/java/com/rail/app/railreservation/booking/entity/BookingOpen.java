package com.rail.app.railreservation.booking.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookingOpen that = (BookingOpen) o;
        return id == that.id && trainNo == that.trainNo && isBookingOpen == that.isBookingOpen && Objects.equals(startDt, that.startDt) && Objects.equals(endDt, that.endDt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trainNo, startDt, endDt, isBookingOpen);
    }
}
