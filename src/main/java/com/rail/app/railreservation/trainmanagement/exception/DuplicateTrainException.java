package com.rail.app.railreservation.trainmanagement.exception;

import java.io.IOException;

public class DuplicateTrainException extends IOException {

    private String trnName;

    public String getTrnName() {
        return trnName;
    }

    private Integer trnNo;

    public Integer getTrnNo() {
        return trnNo;
    }

    public DuplicateTrainException(String trnName,Integer trnNo) {
        super("Exception Caused Due To Duplicate Train Addition");
        this.trnName = trnName;
        this.trnNo = trnNo;
    }
}
