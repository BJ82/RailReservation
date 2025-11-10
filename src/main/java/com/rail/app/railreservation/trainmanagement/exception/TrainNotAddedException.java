package com.rail.app.railreservation.trainmanagement.exception;

import java.io.IOException;

public class TrainNotAddedException extends IOException {

    private String trnName;

    public String getTrnName() {
        return trnName;
    }

    public TrainNotAddedException(String trnName) {
        super("Failed To Add Train");
        this.trnName = trnName;
    }
}
