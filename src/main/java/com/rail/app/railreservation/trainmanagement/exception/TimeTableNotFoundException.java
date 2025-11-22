package com.rail.app.railreservation.trainmanagement.exception;

public class TimeTableNotFoundException extends Exception{

    public TimeTableNotFoundException(String message) {
        super(message);
    }

    public TimeTableNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
