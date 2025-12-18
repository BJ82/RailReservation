package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.dto.Passenger;
import com.rail.app.railreservation.booking.exception.BookingNotOpenException;
import com.rail.app.railreservation.booking.exception.InvalidBookingException;
import com.rail.app.railreservation.booking.repository.BookingOpenRepository;
import com.rail.app.railreservation.booking.repository.BookingRepository;
import com.rail.app.railreservation.route.entity.Route;
import com.rail.app.railreservation.route.repository.RouteRepository;
import com.rail.app.railreservation.route.service.RouteInfoService;
import com.rail.app.railreservation.trainmanagement.dto.TimeTableEnquiryResponse;
import com.rail.app.railreservation.trainmanagement.entity.Timing;
import com.rail.app.railreservation.trainmanagement.entity.Train;
import com.rail.app.railreservation.trainmanagement.enums.Day;
import com.rail.app.railreservation.trainmanagement.enums.JourneyClass;
import com.rail.app.railreservation.trainmanagement.exception.TimeTableNotFoundException;
import com.rail.app.railreservation.trainmanagement.repository.TrainRepository;
import com.rail.app.railreservation.trainmanagement.service.TimeTableService;
import com.rail.app.railreservation.trainmanagement.service.TrainArrivalDateService;
import com.rail.app.railreservation.trainmanagement.service.TrainInfoService;
import com.rail.app.railreservation.util.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    private BookingServiceForTest bookingServiceUnderTest;

    @Mock private TrainInfoService trainInfoService;

    @Mock private RouteInfoService routeInfoService;

    @Mock private SeatInfoTrackerService seatInfoTrackerService;

    @Mock private BookingInfoTrackerService bookingInfoTrackerService;

    @Mock private BookingOpenInfoService bookingOpenInfoService;

    @Mock private TrainArrivalDateService trainArrivalDateService;

    private ModelMapper mapper;

    @Value("${total.no.of.seats}")
    private int totalNoOfSeats;


    private LocalDate startDate;
    private LocalDate endDate;
    private DateTimeFormatter pattern;

    private BookingRequest bookingRequest;

    private Route route;
    private Train train;

    @BeforeEach
    void setUp() {

        pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        startDate = LocalDate.now();
        endDate = startDate.plusDays(2);

        mapper = new ModelMapper();

        bookingServiceUnderTest = new BookingServiceForTest(trainInfoService,routeInfoService,
                seatInfoTrackerService,bookingInfoTrackerService,bookingOpenInfoService,trainArrivalDateService,
                mapper,totalNoOfSeats);



        List<Passenger> passengers= new ArrayList<>();
        passengers.add(new Passenger("First Passenger",24,"M"));
        passengers.add(new Passenger("Second Passenger",25,"F"));
        passengers.add(new Passenger("Third Passenger",26,"F"));


        String from = "stn1";
        String to = "stn5";
        String doj = startDate.plusDays(1).format(pattern);

        bookingRequest = new BookingRequest(1,"TRAIN1",startDate.format(pattern),
                endDate.format(pattern),from,to, JourneyClass.AC1,doj,passengers);

        route = new Route();
        route.setRouteID(1);
        route.setStations(List.of("stn1","stn2","stn3","stn4","stn5"));

        train = new Train();
        train.setRouteId(1);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void book() {
    }

    @Test
    void testInvalidBookingExceptionDueToNonExistentTrain() throws InvalidBookingException, TimeTableNotFoundException, BookingNotOpenException {


        //when
        when(trainInfoService.getByTrainNo(1)).thenReturn(Optional.empty());

        //then
        assertThrows(InvalidBookingException.class,()-> bookingServiceUnderTest.book(bookingRequest));

    }

    @Test
    void testInvalidBookingExceptionDueToIncorrectTrainNo() throws InvalidBookingException, TimeTableNotFoundException, BookingNotOpenException {


        //given

        Route route = new Route();
        route.setRouteID(1);
        route.setStations(List.of("stn1","stn2","stn3","stn4","stn5"));

        Train train = new Train();
        train.setRouteId(1);


        //when
        when(trainInfoService.getByTrainNo(1)).thenReturn(Optional.of(train));

        when(routeInfoService.getByRouteId(1)).thenReturn(Optional.of(route));

        when(routeInfoService.checkIfRouteContains(bookingRequest.getFrom(),
                bookingRequest.getTo(),route)).thenReturn(false);

        //then
        assertThrows(InvalidBookingException.class,()-> bookingServiceUnderTest.book(bookingRequest));


    }

    @Test
    void testBookingNotOpenException() throws InvalidBookingException, TimeTableNotFoundException, BookingNotOpenException {


        //given

        Route route = new Route();
        route.setRouteID(1);
        route.setStations(List.of("stn1","stn2","stn3","stn4","stn5"));

        Train train = new Train();
        train.setRouteId(1);


        //when
        when(trainInfoService.getByTrainNo(1)).thenReturn(Optional.of(train));

        when(routeInfoService.getByRouteId(1)).thenReturn(Optional.of(route));

        when(routeInfoService.checkIfRouteContains(bookingRequest.getFrom(),
                bookingRequest.getTo(),route)).thenReturn(true);

        when(bookingOpenInfoService.isBookingOpen(bookingRequest)).thenReturn(Optional.empty());

        //then
        assertThrows(BookingNotOpenException.class,()-> bookingServiceUnderTest.book(bookingRequest));

    }

    @Test
    void testWhenTrainArrivalDateIsNotEqualToJourneyDate() throws TimeTableNotFoundException {

        //given

        when(trainInfoService.getByTrainNo(1)).thenReturn(Optional.of(train));

        when(routeInfoService.getByRouteId(1)).thenReturn(Optional.of(route));

        when(routeInfoService.checkIfRouteContains(bookingRequest.getFrom(),
                bookingRequest.getTo(),route)).thenReturn(true);

        when(bookingOpenInfoService.isBookingOpen(bookingRequest)).thenReturn(Optional.of(true));

        when(trainArrivalDateService.getArrivalDate(bookingRequest.getTrainNo(),bookingRequest.getFrom(),
                Utils.toLocalDate(bookingRequest.getStartDt())))
                .thenReturn(LocalDate.now());

        //then
        assertThrows(InvalidBookingException.class,()->bookingServiceUnderTest.book(bookingRequest));

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