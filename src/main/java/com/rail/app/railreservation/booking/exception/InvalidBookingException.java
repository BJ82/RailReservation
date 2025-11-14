package com.rail.app.railreservation.booking.exception;

import java.io.IOException;

public class InvalidBookingException extends IOException {

    public InvalidBookingException(String message) {
        super(message);
    }
}
