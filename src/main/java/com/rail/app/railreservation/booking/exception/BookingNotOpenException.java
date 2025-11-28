package com.rail.app.railreservation.booking.exception;

import java.io.IOException;

public class BookingNotOpenException extends IOException {
    public BookingNotOpenException(String message) {
        super(message);
    }
}
