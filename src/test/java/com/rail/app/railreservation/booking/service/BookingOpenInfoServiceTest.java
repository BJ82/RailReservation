package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.BookingOpenRequest;
import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.entity.Booking;
import com.rail.app.railreservation.booking.entity.BookingOpen;
import com.rail.app.railreservation.booking.repository.BookingOpenRepository;
import com.rail.app.railreservation.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingOpenInfoServiceTest {

    @Mock private BookingOpenRepository bookingOpenRepo;
    private BookingOpenInfoService serviceUnderTest;

    private LocalDate startDate;
    private LocalDate endDate;
    private DateTimeFormatter pattern;

    @BeforeEach
    void setUp() {

        pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDate = LocalDate.now();
        endDate = startDate.plusDays(2);
        serviceUnderTest = new BookingOpenInfoService(bookingOpenRepo);

    }

    @Test
    void testAddBookingOpenInfo() {

        //given
        BookingOpenRequest bookingOpenRequest =
                new BookingOpenRequest(startDate.format(pattern),endDate.format(pattern));

        BookingOpen bookingOpenExpected = new BookingOpen(1, Utils.toLocalDate(bookingOpenRequest.getStartDt()),
                Utils.toLocalDate(bookingOpenRequest.getEndDt()),true,
                Timestamp.from(Instant.now()));

        //when
        when(bookingOpenRepo.save(any(BookingOpen.class))).thenReturn(any(BookingOpen.class));

        serviceUnderTest.addBookingOpenInfo(1,bookingOpenRequest);

        //then
        ArgumentCaptor<BookingOpen> bookingOpenArgumentCaptor = ArgumentCaptor.forClass(BookingOpen.class);

        verify(bookingOpenRepo).save(bookingOpenArgumentCaptor.capture());

        BookingOpen bookingOpenActual = bookingOpenArgumentCaptor.getValue();
        assertTrue(bookingOpenExpected.equals(bookingOpenActual));
    }

    @Test
    void testWhenBookingIsOpen() {

        //given
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setTrainNo(1);
        bookingRequest.setStartDt(startDate.format(pattern));
        bookingRequest.setEndDt(endDate.format(pattern));

        //when
        when(bookingOpenRepo.isBookingOpen(1,startDate,endDate)).thenReturn(Optional.of(true));
        Optional<Boolean> isBookingOpen = serviceUnderTest.isBookingOpen(bookingRequest);

        //then
        assertEquals(true,isBookingOpen.get());

    }

    @Test
    void testWhenBookingIsClosed() {

        //given
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setTrainNo(1);
        bookingRequest.setStartDt(startDate.format(pattern));
        bookingRequest.setEndDt(endDate.format(pattern));

        //when
        when(bookingOpenRepo.isBookingOpen(1,startDate,endDate)).thenReturn(Optional.of(false));
        Optional<Boolean> isBookingOpen = serviceUnderTest.isBookingOpen(bookingRequest);

        //then
        assertTrue(isBookingOpen.isEmpty());

    }

    @Test
    void testFindBookingOpenInfoByTrainNo() {

        //given
        int trainNo=1;


        BookingOpen bookingOpenExpected = new BookingOpen(trainNo, startDate,
                endDate,true,
                Timestamp.from(Instant.now()));


        //when
        when(bookingOpenRepo.findByTrainNo(trainNo)).thenReturn(List.of(bookingOpenExpected));

        BookingOpen bookingOpenActual = serviceUnderTest.getBookingOpenInfoByTrainNo(trainNo).getFirst();

        //then
        assertTrue(bookingOpenExpected.equals(bookingOpenActual));
    }


}