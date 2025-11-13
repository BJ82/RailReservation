package com.rail.app.railreservation.common;

import com.rail.app.railreservation.enquiry.exception.RouteNotFoundException;
import com.rail.app.railreservation.enquiry.exception.TrainNotFoundException;
import com.rail.app.railreservation.trainmanagement.controller.TrainsController;
import com.rail.app.railreservation.trainmanagement.exception.DuplicateTrainException;
import com.rail.app.railreservation.trainmanagement.exception.TimeTableAddFailException;
import com.rail.app.railreservation.trainmanagement.exception.TimeTableWithoutTrainException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateTrainException.class)
    public ResponseEntity<String> duplicateTrainExceptionHandler(DuplicateTrainException dupltrnex){

        logger.error(dupltrnex.getMessage());
        logger.error("Train With Name: {} and TrainNo: {} Already Present!!",dupltrnex.getTrnName(),dupltrnex.getTrnNo());
        return  ResponseEntity.status(HttpStatus.FORBIDDEN).header("Cause","Adding Duplicate Train").body("DuplicateTrainException");

    }

    @ExceptionHandler(TrainNotFoundException.class)
    public ResponseEntity<String> trainNotFoundHandler(TrainNotFoundException tnfex){

        logger.error(tnfex.getMessage());
        logger.error("Exception Raised"+tnfex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Cause",tnfex.getMessage()).body(tnfex.getMessage());
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<String> routeNotFoundHandler(RouteNotFoundException routnf){

        logger.error(routnf.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Cause",routnf.getMessage()).body(routnf.getMessage());
    }

    @ExceptionHandler(TimeTableWithoutTrainException.class)
    public ResponseEntity<String> timeTableWithoutTrainHandler(TimeTableWithoutTrainException timtblwithoutrnex){

        logger.error(timtblwithoutrnex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Cause",timtblwithoutrnex.getMessage()).body(timtblwithoutrnex.getMessage());
    }

    @ExceptionHandler(TimeTableAddFailException.class)
    public ResponseEntity<String> timeTableWithoutTrainHandler(TimeTableAddFailException tmtbladdfail ){

        logger.error(tmtbladdfail.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Cause",tmtbladdfail.getMessage()).body(tmtbladdfail.getMessage());
    }
}
