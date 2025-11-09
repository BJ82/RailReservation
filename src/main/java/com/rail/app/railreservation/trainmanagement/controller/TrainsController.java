package com.rail.app.railreservation.trainmanagement.controller;

import com.rail.app.railreservation.trainmanagement.dto.TrainAddRequest;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddResponse;
import com.rail.app.railreservation.trainmanagement.exception.DuplicateTrainException;
import com.rail.app.railreservation.trainmanagement.service.TrainService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("train/")
public class TrainsController {

    private static final Logger logger = LogManager.getLogger(TrainsController.class);

    private static final String COMMON_MESSAGE = "Inside TrainsController...";

    @Autowired
    private TrainService ts;

    @PostMapping("add")
    public ResponseEntity<TrainAddResponse> add(@RequestBody TrainAddRequest trnAddReq) throws DuplicateTrainException {

        logger.info(COMMON_MESSAGE);
        logger.info("Processing Request To Add New Train, with name:{}",trnAddReq.getTrainName());

        TrainAddResponse trainAddResponse = ts.addNewTrain(trnAddReq);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(trainAddResponse.getTrainNo())
                .toUri();

        return ResponseEntity.created(location).body(trainAddResponse);
    }


    @ExceptionHandler(DuplicateTrainException.class)
    public ResponseEntity<String> duplicateTrainExceptionHandler(DuplicateTrainException dupltrnex){

       logger.error(dupltrnex.getMessage());
       logger.error("Train With Name: {} and TrainNo: {} Already Present!!",dupltrnex.getTrnName(),dupltrnex.getTrnNo());
       return  ResponseEntity.status(HttpStatus.FORBIDDEN).header("Cause","Adding Duplicate Train").body("DuplicateTrainException");

    }

}
