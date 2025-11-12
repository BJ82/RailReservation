package com.rail.app.railreservation.enquiry.controller;

import com.rail.app.railreservation.common.repository.TrainRepository;
import com.rail.app.railreservation.enquiry.exception.RouteNotFoundException;
import com.rail.app.railreservation.enquiry.service.Enquiry;
import com.rail.app.railreservation.enquiry.exception.TrainNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rail.app.railreservation.enquiry.dto.TrainEnquiryResponse;

import java.util.List;

@RestController
@RequestMapping("enquiry/")
public class EnquiryController {

    private static final Logger logger = LogManager.getLogger(EnquiryController.class);

    private static final String INSIDE_ENQUIRY_CONTROLLER = "Inside Enquiry Controller...";

    private Enquiry enquiryService;

    public EnquiryController(Enquiry enquiryService) {
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

   /* @ExceptionHandler(TrainNotFoundException.class)
    public ResponseEntity<String> trainNotFoundHandler(TrainNotFoundException tnfex){

        logger.error(tnfex.getMessage());
        logger.error("Exception Raised"+tnfex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Cause",tnfex.getMessage()).body(tnfex.getMessage());
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<String> routeNotFoundHandler(RouteNotFoundException routnf){

        logger.error(routnf.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Cause",routnf.getMessage()).body(routnf.getMessage());
    }*/

}
