package com.rail.app.railreservation.booking.exception;

import java.io.IOException;

public class BookingCannotOpenException extends IOException {
    public BookingCannotOpenException(String message) {
        super(message);
    }
}
