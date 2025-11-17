package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.common.enums.JourneyClass;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rail.app.railreservation.booking.entity.SeatNoTracker;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public interface SeatNoTrackerRepository extends JpaRepository<SeatNoTracker,Integer> {

    @Query("SELECT * FROM SeatNoTracker snt" +
            "WHERE snt.trainNo = :trainNo AND" +
            "snt.journeyClass = :jrnyClass AND" +
            "snt.startDate <= :doj AND" +
            "snt.endDt > :doj")
    SeatNoTracker findSeatNoTracker(@Param("trainNo") int trainNo, @Param("jrnyClass") JourneyClass journeyClass, @Param("doj") LocalDate doj);
}
