package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.Booking;
import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.common.enums.JourneyClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking,Integer> {

    @Query("")
    BookingStatus findByLastWaitingStatus(@Param("trainNo") int trainNo, @Param("frm") String frm,
                                          @Param("to") String to, @Param("doj")LocalDate doj,
                                          @Param("journyClass")JourneyClass journeyClass,
                                          @Param("bkngStatus")BookingStatus bkngStatus);
}
