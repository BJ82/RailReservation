package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.BookingOpen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingOpenRepository extends JpaRepository<BookingOpen,Integer> {
}
