package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.SeatCounter;
import com.rail.app.railreservation.common.enums.JourneyClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.time.LocalDate;

public interface SeatCounterRepository extends JpaRepository<SeatCounter,Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE SeatCount sc " +
            "SET sc.seatCount = sc.seatCount - :count " +
            "WHERE sc.trainNo = :trainNo AND sc.routeID IN (:routeIDS) " +
            "AND sc.journeyClass = :journeyClass AND sc.startDate <= :doj AND sc.endDate > :doj")
    void updateSeatCount(@Param("trainNo") int trainNo, @Param("routeIDS") List<Integer> routeIDS,
                         @Param("count") int count, @Param("doj") LocalDate doj, @Param("class") JourneyClass journeyClass);
}
