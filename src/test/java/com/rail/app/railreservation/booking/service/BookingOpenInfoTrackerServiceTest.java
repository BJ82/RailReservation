package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.BookingOpenRequest;
import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.entity.BookingOpen;
import com.rail.app.railreservation.booking.repository.BookingOpenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingOpenInfoTrackerServiceTest {

    @Mock private BookingOpenRepository bookingOpenRepo;
    private BookingOpenInfoTrackerService serviceUnderTest;

    private LocalDate startDate;
    private LocalDate endDate;
    private DateTimeFormatter pattern;

    @BeforeEach
    void setUp() {

        pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDate = LocalDate.now();
        endDate = startDate.plusDays(2);
        serviceUnderTest = new BookingOpenInfoTrackerService(bookingOpenRepo);

    }

    @Test
    void initBookingOpenInfoTracker() {


        BookingOpenRequest bookingOpenRequest =
                new BookingOpenRequest(startDate.format(pattern),endDate.format(pattern));

        //when
        serviceUnderTest.initBookingOpenInfoTracker(1,bookingOpenRequest);

        //then
        verify(bookingOpenRepo).save(ArgumentMatchers.any(BookingOpen.class));
    }

    @Test
    void isBookingOpen() {

        //given
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setTrainNo(1);
        bookingRequest.setStartDt(startDate.format(pattern));
        bookingRequest.setEndDt(endDate.format(pattern));

        //when
        serviceUnderTest.isBookingOpen(bookingRequest);

        //then
        verify(bookingOpenRepo).isBookingOpen(1,startDate,endDate);
    }

    @Test
    void getBookingOpenByTrainNo() {

        //given
        int trainNo=1;

        //when
        serviceUnderTest.getBookingOpenByTrainNo(trainNo);

        //then
        verify(bookingOpenRepo).findByTrainNo(trainNo);
    }


}