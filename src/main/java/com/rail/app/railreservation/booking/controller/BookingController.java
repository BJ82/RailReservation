package com.rail.app.railreservation.booking.controller;

import com.rail.app.railreservation.booking.dto.BookedResponse;
import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.exception.InvalidBookingException;
import com.rail.app.railreservation.booking.service.BookingService;
import com.rail.app.railreservation.enquiry.controller.EnquiryController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("booking/")
public class BookingController {

    private static final Logger logger = LogManager.getLogger(BookingController.class);

    private static final String INSIDE_BOOKING_CONTROLLER = "Inside Booking Controller...";

    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("book")
    public ResponseEntity<BookedResponse> bookTicket(@RequestBody BookingRequest bookingRequest) throws InvalidBookingException {

        logger.info(INSIDE_BOOKING_CONTROLLER);
        logger.info("Processing Request For Ticket Booking");

        bookingService.book(bookingRequest);
    }

}
