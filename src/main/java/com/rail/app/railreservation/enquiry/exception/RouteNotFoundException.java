package com.rail.app.railreservation.enquiry.exception;

import java.io.IOException;

public class RouteNotFoundException extends IOException {

    private int routeID;
    public RouteNotFoundException(String message,int routeID) {
        super(message);
        this.routeID = routeID;
    }
}
