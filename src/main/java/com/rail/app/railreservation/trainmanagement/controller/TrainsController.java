package com.rail.app.railreservation.trainmanagement.controller;

import com.rail.app.railreservation.enquiry.dto.TrainEnquiryResponse;
import com.rail.app.railreservation.enquiry.exception.TrainNotFoundException;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddRequest;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddResponse;
import com.rail.app.railreservation.trainmanagement.service.TrainService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("train/")
public class TrainsController {

    private static final Logger logger = LogManager.getLogger(TrainsController.class);

    private static final String COMMON_MESSAGE = "Inside TrainAddRequest Controller...";

    private static final String TRAIN_ADDED = "TrainAddRequest Added..";

    @Autowired
    private TrainService ts;

    @PostMapping("add")
    public ResponseEntity<TrainAddResponse> add(@RequestBody TrainAddRequest trn){

        logger.info(COMMON_MESSAGE);
        logger.info("Adding Train, with trainNo:{} and name:{}",trn.getTrainNo(),trn.getTrainName());

        TrainAddResponse trainAddResponse = new TrainAddResponse();
        trainAddResponse = ts.addTrain(trn);
/*
        if(trainAddResponse.getIsTrainAdded() == false){

        }*/
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

/*
    @ExceptionHandler(TrainNotAddedException.class)
    public ResponseEntity<List<TrainEnquiryResponse>> trainNotAddedExceptionHandler(TrainNotAddedException tntad){

        String src = tnfex.getSrc();
        String dest = tnfex.getDest();
        logger.error(tnfex.getMessage()+src+"And"+dest);
        logger.error("Exception Raised"+tntad);
        return ResponseEntity.notFound().header("Cause","TrainAddRequest Not Available Between"+src+"And"+dest).build();
    }
*/
}
