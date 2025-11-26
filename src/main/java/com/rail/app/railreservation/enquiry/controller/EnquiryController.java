package com.rail.app.railreservation.enquiry.controller;

import com.rail.app.railreservation.enquiry.dto.SeatEnquiryRequest;
import com.rail.app.railreservation.enquiry.dto.SeatEnquiryResponse;
import com.rail.app.railreservation.enquiry.exception.InvalidSeatEnquiryException;
import com.rail.app.railreservation.enquiry.exception.RouteNotFoundException;
import com.rail.app.railreservation.enquiry.service.EnquiryService;
import com.rail.app.railreservation.enquiry.exception.TrainNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rail.app.railreservation.enquiry.dto.TrainEnquiryResponse;

import java.util.List;

@RestController
@RequestMapping("enquiry/")
public class EnquiryController {

    private static final Logger logger = LogManager.getLogger(EnquiryController.class);

    private static final String INSIDE_ENQUIRY_CONTROLLER = "Inside EnquiryService Controller...";

    private EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    @GetMapping("train")
    public ResponseEntity<List<TrainEnquiryResponse>> trainEnquiryByStation(@RequestParam String src,@RequestParam String dest)
            throws TrainNotFoundException {

        logger.info(INSIDE_ENQUIRY_CONTROLLER);
        logger.info("Processing request to find all trains between {} and {}",src,dest);

        return ResponseEntity.ok().body(enquiryService.trainEnquiry(src,dest));
    }

    @GetMapping("train/{trainNo}")
    public ResponseEntity<TrainEnquiryResponse> trainEnquiryByTrainNo(@PathVariable("trainNo") int trnNo) throws TrainNotFoundException, RouteNotFoundException {

        logger.info(INSIDE_ENQUIRY_CONTROLLER);
        logger.info("Processing request to find train with TrainNo:{}",trnNo);

        return ResponseEntity.ok().body(enquiryService.trainEnquiry(trnNo));
    }

    @PostMapping("seats")
    public ResponseEntity<SeatEnquiryResponse> seatEnquiry(@RequestBody SeatEnquiryRequest seatEnquiryRequest) throws InvalidSeatEnquiryException {

        logger.info(INSIDE_ENQUIRY_CONTROLLER);
        logger.info("Processing request to find available seats");

        SeatEnquiryResponse seatEnquiryResponse;
        seatEnquiryResponse = enquiryService.seatEnquiry(seatEnquiryRequest);

        return ResponseEntity.ok().body(seatEnquiryResponse);
    }

}
