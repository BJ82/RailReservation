package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.Booking;
import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.trainmanagement.enums.JourneyClass;
import jakarta.persistence.EntityManager;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private EntityManager entityManager;

    private LocalDate startDate;
    private LocalDate endDate;
    private DateTimeFormatter pattern;

    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void beforeEach() {

        bookingRepo.deleteAll();

        pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDate = LocalDate.now();
        endDate = startDate.plusDays(2);

         booking1 = new Booking("FirstName",24,"M",1,startDate,endDate,
                "stn1","stn5",startDate.plusDays(1),JourneyClass.AC1,
                BookingStatus.CONFIRMED, Timestamp.valueOf(LocalDateTime.now()),1);

        bookingRepo.save(booking1);


         booking2 = new Booking("SecondName",25,"M",1,startDate,endDate,
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
    void testIfAllBookingsRetrievedBySeatNumberHaveConfirmStatus() {
        
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

        BookingStatus bkngStatus = bookings.getFirst().getBookingStatus();

        assertThat(bkngStatus).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void testCountOfSeatNumber() {

        //given
        int trainNo = 1;
        JourneyClass jrnyClass = JourneyClass.AC1;
        LocalDate strtDt = startDate;
        LocalDate endDt = endDate;
        int seatNo = 1;

        Booking booking3 = new Booking("FirstName",24,"M",1,startDate,endDate,
                "stn5","stn7",startDate.plusDays(1),JourneyClass.AC1,
                BookingStatus.CONFIRMED, Timestamp.valueOf(LocalDateTime.now()),1);

        bookingRepo.save(booking3);

        //when
        int count = bookingRepo.findCountOfSeatNumber(trainNo,jrnyClass,strtDt,endDt,seatNo);

        //then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testToFindAllBookingsWhichShareSameSeatNo() {

        //given
        Booking booking3 = new Booking("FirstName",24,"M",1,startDate,endDate,
                "stn5","stn7",startDate.plusDays(1),JourneyClass.AC1,
                BookingStatus.CONFIRMED, Timestamp.valueOf(LocalDateTime.now()),1);

        bookingRepo.save(booking3);

        //when
        List<Booking> bookings = bookingRepo.findBySeatNo(1,1,
                JourneyClass.AC1,startDate,endDate);

        //then
        assertThat(bookings.stream().
                filter((b)->b.getSeatNo() == 1).
                collect(Collectors.toList()).
                size()).isEqualTo(2);


    }

    @Test
    void testToFindBookingtatusGivenPnrNo() {

        //given
        int pnrNo = 1;

        //when
        BookingStatus bookingStatus = bookingRepo.findBookingstatus(pnrNo).get();

        //then
        assertThat(bookingStatus).isEqualTo(BookingStatus.CONFIRMED);

    }

    @Test
    void testToFindAllBookingsByBookingStatus() {

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
    void testIfBookingDetailsGetUpdated() throws InterruptedException {

        //given
        int pnrNo = booking2.getPnr();

        //when
        bookingRepo.updateBooking(pnrNo,1,BookingStatus.CONFIRMED);
        entityManager.clear();
        Booking booking = bookingRepo.findById(pnrNo).orElse(new Booking());

        //then
        assertThat(booking.getSeatNo() == 1 &&
                booking.getBookingStatus().equals(BookingStatus.CONFIRMED)).isTrue();
    }

}