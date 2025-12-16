package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.Booking;
import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.trainmanagement.enums.JourneyClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepo;

    private LocalDate startDate;
    private LocalDate endDate;
    private DateTimeFormatter pattern;

    @BeforeEach
    void beforeEach() {

        pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDate = LocalDate.now();
        endDate = startDate.plusDays(2);
        bookingRepo.deleteAll();


        Booking booking1 = new Booking("FirstName",24,"M",1,startDate,endDate,
                "stn1","stn5",startDate.plusDays(1),JourneyClass.AC1,
                BookingStatus.CONFIRMED, Timestamp.valueOf(LocalDateTime.now()),1);

        bookingRepo.save(booking1);


        Booking booking2 = new Booking("SecondName",25,"M",1,startDate,endDate,
                "stn1","stn5",startDate.plusDays(1),JourneyClass.AC1,
                BookingStatus.WAITING, Timestamp.valueOf(LocalDateTime.now()),0);

        bookingRepo.save(booking2);

    }

    @Test
    @Disabled
    void findLastBooking() {
    }

    @Test
    @Disabled
    void findFirstBooking() {
    }

    @Test
    void checkSeatNumbersHaveConfirmStatus() {
        
        //given
         String startFrom = "stn1";
         String endAt = "stn5";
         int trainNo = 1;
         LocalDate strtDt = startDate;
         LocalDate endDt = endDate;
         JourneyClass jrnyClass = JourneyClass.AC1;

         //when

        List<Integer> seatNums = bookingRepo.findSeatNumbers(startFrom,endAt,trainNo,strtDt,endDt,jrnyClass);

        List<Booking> bookings = new ArrayList<>();
        seatNums.stream().forEach((seatNo)->bookings.addAll(
                bookingRepo.findBySeatNo(seatNo,1,jrnyClass,strtDt,endDt)
        ));


        //then
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.getFirst().getBookingStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    @Disabled
    void findCountOfSeatNumber() {
    }

    @Test
    @Disabled
    void findBySeatNo() {
    }

    @Test
    @Disabled
    void findBookingstatus() {
    }

    @Test
    void findByBookingStatus() {

        //given
        int trainNo = 1;
        JourneyClass jrnyClass = JourneyClass.AC1;
        LocalDate strtDt = startDate;
        LocalDate endDt = endDate;

        //when
        Optional<List<Booking>> bookings =  bookingRepo.findByBookingStatus(BookingStatus.CONFIRMED,trainNo,
                        jrnyClass,startDate,endDate);

        //then
        assertThat(bookings.isPresent() && bookings.get().size() == 1 &&
                bookings.get().getFirst().getBookingStatus().equals(BookingStatus.CONFIRMED)).isTrue();

    }

    @Test
    @Disabled
    void updateBooking() {
    }

}