package com.rail.app.railreservation.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rail.app.railreservation.booking.dto.BookedPassenger;
import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.dto.BookingResponse;
import com.rail.app.railreservation.booking.dto.Passenger;
import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.booking.service.BookingService;
import com.rail.app.railreservation.security.entity.Users;
import com.rail.app.railreservation.security.role.Role;
import com.rail.app.railreservation.security.service.UserService;
import com.rail.app.railreservation.security.util.JwtUtil;
import com.rail.app.railreservation.trainmanagement.enums.JourneyClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    private LocalDate startDate;
    private LocalDate endDate;
    private DateTimeFormatter pattern;

    private BookingRequest bookingRequest;

    private String jwt;

    @BeforeEach
    void setUp() {

        pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        startDate = LocalDate.now();
        endDate = startDate.plusDays(2);

        List<Passenger> passengers= new ArrayList<>();
        passengers.add(new Passenger("First Passenger",24,"M"));
        passengers.add(new Passenger("Second Passenger",25,"F"));
        passengers.add(new Passenger("Third Passenger",26,"F"));
        jwt = "JwtForTest";

        String from = "stn1";
        String to = "stn5";
        String doj = startDate.plusDays(1).format(pattern);

        bookingRequest = new BookingRequest(1,"TRAIN1",startDate.format(pattern),
                endDate.format(pattern),from,to, JourneyClass.AC1,doj,passengers);

    }

    @Test
    @WithMockUser(username = "bj", roles = {"ROLE.USER"})
    void testBookTicket() throws Exception {

        ModelMapper mapper = new ModelMapper();

        BookingResponse bookingResponse = mapper.map(bookingRequest,BookingResponse.class);
        bookingResponse.setBookingDateTime(Timestamp.from(Instant.now()));

        BookedPassenger bookedPassenger;

        int noOfPsngr = bookingRequest.getPassengers().size();

        for(int j=0;j<noOfPsngr;j++) {

            bookedPassenger = mapper.map(bookingRequest.getPassengers().get(j), BookedPassenger.class);

            bookedPassenger.setPnr((int)Math.random());
            bookedPassenger.setSeatNo((int)Math.random());
            bookedPassenger.setStatus(BookingStatus.CONFIRMED);

            bookingResponse.getPassengerList().add(bookedPassenger);

        }

        when(bookingService.book(bookingRequest)).thenReturn(bookingResponse);

        mockMvc.perform(post("/api/v1/bookings")
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookingRequest))
                .with(csrf()))
                .andExpect(status().isCreated());

    }

    @Test
    @Disabled
    void testOpenBooking() {
    }

    @Test
    @Disabled
    void testIsBookingOpen() {
    }

    @Test
    @Disabled
    void testCancelTicket() {
    }
}