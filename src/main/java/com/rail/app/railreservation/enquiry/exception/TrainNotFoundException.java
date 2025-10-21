package com.rail.app.railreservation.enquiry.exception;

import java.io.IOException;

public class TrainNotFoundException extends IOException {
    private String src;
    private String dest;

    public String getSrc() {
        return src;
    }

    public String getDest() {
        return dest;
    }

    public TrainNotFoundException(String message, String src, String dest){

        super(message);
        this.src = src;
        this.dest = dest;
    }

}
