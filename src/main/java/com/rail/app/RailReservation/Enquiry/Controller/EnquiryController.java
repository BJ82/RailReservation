package com.rail.app.RailReservation.Enquiry.Controller;

import com.rail.app.RailReservation.Enquiry.Service.Enquiry;
import com.rail.app.RailReservation.Enquiry.TrainNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<List<TrainEnquiryResponse>> trainEnquiryByStation(@RequestParam String src,@RequestParam String dest)
            throws TrainNotFoundException {

        logger.info(COMMON_MESSAGE);
        logger.info("Processing request to find all trains between {} and {}",src,dest);

        List<TrainEnquiryResponse> trainsFound = enquiryService.trainEnquiry(src,dest);
        if(trainsFound.isEmpty()){

            throw new TrainNotFoundException("Train Not Found Between",src,dest);
        }
        return ResponseEntity.ok().body(trainsFound);
    }

    @ExceptionHandler(TrainNotFoundException.class)
    public ResponseEntity<List<TrainEnquiryResponse>> trainNotFoundExceptionHandler(TrainNotFoundException tnfex){

        String src = tnfex.getSrc();
        String dest = tnfex.getDest();
        logger.error(tnfex.getMessage()+src+"And"+dest);
        logger.error("Exception Raised"+tnfex);
        return ResponseEntity.notFound().header("Cause","Train Not Available Between"+src+"And"+dest).build();
    }

}
