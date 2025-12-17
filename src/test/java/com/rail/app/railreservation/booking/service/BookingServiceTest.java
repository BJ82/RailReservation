package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.dto.Passenger;
import com.rail.app.railreservation.booking.exception.BookingNotOpenException;
import com.rail.app.railreservation.booking.exception.InvalidBookingException;
import com.rail.app.railreservation.booking.repository.BookingRepository;
import com.rail.app.railreservation.route.service.RouteInfoService;
import com.rail.app.railreservation.trainmanagement.entity.Train;
import com.rail.app.railreservation.trainmanagement.enums.JourneyClass;
import com.rail.app.railreservation.trainmanagement.exception.TimeTableNotFoundException;
import com.rail.app.railreservation.trainmanagement.service.TrainInfoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepo;

    private BookingServiceForTest bookingServiceUnderTest;

    @Mock private TrainInfoService trainInfoService;

    @Mock private RouteInfoService routeInfoService;

    @Mock private SeatInfoTrackerService seatInfoTrackerService;

    @Mock private BookingInfoTrackerService bookingInfoTrackerService;

    @Mock private BookingOpenInfoService bookingOpenInfoService;

    private ModelMapper mapper;

    @Value("${total.no.of.seats}")
    private int totalNoOfSeats;


    private LocalDate startDate;
    private LocalDate endDate;
    private DateTimeFormatter pattern;


    @BeforeEach
    void setUp() {
        pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDate = LocalDate.now();
        endDate = startDate.plusDays(2);
        mapper = new ModelMapper();
        bookingServiceUnderTest = new BookingServiceForTest(trainInfoService,routeInfoService,
                seatInfoTrackerService,bookingInfoTrackerService,bookingOpenInfoService,
                mapper,totalNoOfSeats);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void book() {
    }

    @Test
    void testInvalidBookingExceptionDueToNonExistentTrain() throws InvalidBookingException, TimeTableNotFoundException, BookingNotOpenException {

        //given

        String from = "stn1";
        String to = "stn5";
        String doj = startDate.plusDays(1).format(pattern);

        List<Passenger> passengers= new ArrayList<>();

        passengers.add(new Passenger("First Passenger",24,"M"));
        passengers.add(new Passenger("Second Passenger",25,"F"));
        passengers.add(new Passenger("Third Passenger",26,"F"));

        BookingRequest bookingRequest = new BookingRequest(1,"TRAIN1",startDate.format(pattern),
                endDate.format(pattern),from,to, JourneyClass.AC1,doj,passengers);


        //when
        when(trainInfoService.getByTrainNo(1)).thenReturn(Optional.empty());

        //then
        assertThrows(InvalidBookingException.class,()-> bookingServiceUnderTest.book(bookingRequest));


    }

    @Test
    void testInvalidBookingExceptionDueToIncorrectTrainNo() throws InvalidBookingException, TimeTableNotFoundException, BookingNotOpenException {

        //given

        String from = "stn1";
        String to = "stn5";
        String doj = startDate.plusDays(1).format(pattern);

        List<Passenger> passengers= new ArrayList<>();

        passengers.add(new Passenger("First Passenger",24,"M"));
        passengers.add(new Passenger("Second Passenger",25,"F"));
        passengers.add(new Passenger("Third Passenger",26,"F"));

        BookingRequest bookingRequest = new BookingRequest(1,"TRAIN1",startDate.format(pattern),
                endDate.format(pattern),from,to, JourneyClass.AC1,doj,passengers);

        //when
        when(trainInfoService.getByTrainNo(1)).thenReturn(Optional.of(new Train()));

        when(bookingServiceUnderTest.isValidRoute(bookingRequest.getFrom(),bookingRequest.getTo(),new Train())).
                thenReturn(Optional.empty());

        //then
        assertThrows(InvalidBookingException.class,()-> bookingServiceUnderTest.book(bookingRequest));


    }

    @Test
    void testBookingNotOpenException() throws InvalidBookingException, TimeTableNotFoundException, BookingNotOpenException {


        //given

        String from = "stn1";
        String to = "stn5";
        String doj = startDate.plusDays(1).format(pattern);

        List<Passenger> passengers= new ArrayList<>();

        passengers.add(new Passenger("First Passenger",24,"M"));
        passengers.add(new Passenger("Second Passenger",25,"F"));
        passengers.add(new Passenger("Third Passenger",26,"F"));

        BookingRequest bookingRequest = new BookingRequest(1,"TRAIN1",startDate.format(pattern),
                endDate.format(pattern),from,to, JourneyClass.AC1,doj,passengers);

        //when
        when(trainInfoService.getByTrainNo(1)).thenReturn(Optional.of(new Train()));

        when(bookingServiceUnderTest.isValidRoute(bookingRequest.getFrom(),bookingRequest.getTo(),new Train())).
                thenReturn(Optional.of(true));

        when(bookingOpenInfoService.isBookingOpen(bookingRequest)).thenReturn(Optional.empty());

        //then
        assertThrows(BookingNotOpenException.class,()-> bookingServiceUnderTest.book(bookingRequest));

    }

    @Test
    void cancelBooking() {
    }

    @Test
    void openBooking() {
    }

    @Test
    void getBookingOpenInfo() {
    }

    @Test
    void getAvailableSeatNumbers() {
    }

}