package com.rail.app.railreservation.trainmanagement.exception;

import java.io.IOException;

public class TimeTableWithoutTrainException extends IOException {

    public TimeTableWithoutTrainException(String message) {
        super(message);
    }
}
