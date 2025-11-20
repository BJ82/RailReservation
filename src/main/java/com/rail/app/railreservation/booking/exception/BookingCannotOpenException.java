package com.rail.app.railreservation.booking.exception;

import java.io.IOException;

public interface BookingCannotOpenException extends IOException {

    BookingCannotOpenException(String message){
        super(message);
    }
}
