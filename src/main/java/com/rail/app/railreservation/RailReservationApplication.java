package com.rail.app.railreservation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
public class RailReservationApplication {

	private static final Logger logger = LogManager.getLogger(RailReservationApplication.class);

	public static void main(String[] args) {

		logger.info("Starting Rail Reservation System....");
		SpringApplication.run(RailReservationApplication.class, args);
	}

}
