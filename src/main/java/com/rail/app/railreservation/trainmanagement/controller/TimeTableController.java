package com.rail.app.railreservation.trainmanagement.controller;

import com.rail.app.railreservation.trainmanagement.dto.TimeTableAddRequest;
import com.rail.app.railreservation.trainmanagement.dto.TimeTableAddResponse;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddRequest;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddResponse;
import com.rail.app.railreservation.trainmanagement.exception.DuplicateTrainException;
import com.rail.app.railreservation.trainmanagement.exception.TimeTableAddFailException;
import com.rail.app.railreservation.trainmanagement.exception.TimeTableWithoutTrainException;
import com.rail.app.railreservation.trainmanagement.service.TimeTableService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("timetable/")
public class TimeTableController {

    private static final Logger logger = LogManager.getLogger(TimeTableController.class);

    private static final String INSIDE_TIME_TABLE_CONTROLLER = "Inside TimeTable Controller...";

    private TimeTableService timeTableService;

    public TimeTableController(TimeTableService timeTableService) {
        this.timeTableService = timeTableService;
    }

    @PostMapping("add")
    public ResponseEntity<TimeTableAddResponse> add(@RequestBody TimeTableAddRequest tmtbladdreq) throws TimeTableWithoutTrainException, TimeTableAddFailException {

        logger.info(INSIDE_TIME_TABLE_CONTROLLER);
        logger.info("Processing Request To Add Time Table For Train:{},TrainNo:{}",tmtbladdreq.getTrainName(),tmtbladdreq.getTrainNo());

        TimeTableAddResponse timeTableAddResponse = timeTableService.addTimeTable(tmtbladdreq);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(tmtbladdreq.getTrainNo())
                .toUri();

        return ResponseEntity.created(location).body(timeTableAddResponse);

    }
}
