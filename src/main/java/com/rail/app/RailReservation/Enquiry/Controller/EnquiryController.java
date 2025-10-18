package com.rail.app.RailReservation.Enquiry.Controller;

import com.rail.app.RailReservation.Enquiry.Service.Enquiry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.rail.app.RailReservation.Enquiry.DTO.TrainEnquiryResponse;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("enquiry/")
public class EnquiryController {

    private static final Logger logger = LogManager.getLogger(EnquiryController.class);

    private static final String COMMON_MESSAGE = "Inside Enquiry Controller...";

    @Autowired
    private Enquiry enquiryService;

    @GetMapping("train")
    public ResponseEntity<List<TrainEnquiryResponse>> trainEnquiryByStation(@RequestParam String src,@RequestParam String dest){

        logger.info(COMMON_MESSAGE);
        logger.info("Processing request to find all trains between {} and {}",src,dest);

        List<TrainEnquiryResponse> trainsFound = enquiryService.trainEnquiry(src,dest);
        if(trainsFound.isEmpty()){
            return ResponseEntity.badRequest().body(trainsFound);
        }
        return ResponseEntity.ok().body(trainsFound);
    }

}
