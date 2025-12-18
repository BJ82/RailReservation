package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.route.service.RouteInfoService;
import com.rail.app.railreservation.trainmanagement.service.TrainArrivalDateService;
import com.rail.app.railreservation.trainmanagement.service.TrainInfoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;

public class BookingServiceForTest extends BookingService {

    public BookingServiceForTest(TrainInfoService trainInfoService, RouteInfoService routeInfoService,
                                 SeatInfoTrackerService seatInfoTrackerService,
                                 BookingInfoTrackerService bookingInfoTrackerService,
                                 BookingOpenInfoService bookingOpenInfoService,
                                 TrainArrivalDateService trainArrivalDateService,
                                 ModelMapper mapper, @Value("${total.no.of.seats}") int totalNoOfSeats) {

        super(trainInfoService,routeInfoService,seatInfoTrackerService,bookingInfoTrackerService,
                bookingOpenInfoService,trainArrivalDateService,mapper,totalNoOfSeats);

    }
}
