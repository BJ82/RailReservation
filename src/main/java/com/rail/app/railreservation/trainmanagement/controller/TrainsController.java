package com.rail.app.railreservation.trainmanagement.controller;

import com.rail.app.railreservation.enquiry.dto.TrainEnquiryResponse;
import com.rail.app.railreservation.enquiry.exception.TrainNotFoundException;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddRequest;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddResponse;
import com.rail.app.railreservation.trainmanagement.exception.TrainNotAddedException;
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
    public ResponseEntity<TrainAddResponse> add(@RequestBody TrainAddRequest trnAddReq) throws TrainNotAddedException{

        logger.info(COMMON_MESSAGE);
        logger.info("Processing Request To Add New Train, with name:{}",trnAddReq.getTrainName());

        TrainAddResponse trainAddResponse = ts.addNewTrain(trnAddReq);

        if(trainAddResponse == null || trainAddResponse.getTrainNo() == -1){

            throw new TrainNotAddedException(trnAddReq.getTrainName());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @ExceptionHandler(TrainNotAddedException.class)
    public ResponseEntity<TrainAddResponse> trainNotAddedExceptionHandler(TrainNotAddedException tntadex){

        logger.error("!! Failed To Add Train With Name:{}",tntadex.getTrnName());
        return ResponseEntity.internalServerError().header("Cause","Unable To Add Train With Name:"+tntadex.getTrnName()).build();
    }

}
