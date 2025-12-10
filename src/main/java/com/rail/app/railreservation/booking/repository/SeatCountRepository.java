package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.SeatCount;
import com.rail.app.railreservation.commons.enums.JourneyClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
public interface SeatCountRepository extends JpaRepository<SeatCount,Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE SeatCount sc " +
            "SET sc.seatCount = :seatCount " +
            "WHERE sc.trainNo = :trainNo " +
            "AND sc.journeyClass = :journeyClass " +
            "AND sc.startDate = :startDate " +
            "AND sc.endDate = :endDate")
    void updateSeatCount(@Param("trainNo") int trainNo,
                         @Param("journeyClass") JourneyClass journeyClass,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate,
                         @Param("seatCount") int seatCount);

    @Query("SELECT s.seatCount FROM SeatCount s " +
            "WHERE s.trainNo=:trainNo " +
            "AND s.journeyClass=:journeyClass " +
            "AND s.startDate=:startDate " +
            "AND s.endDate=:endDate")
    Integer findSeatCount(@Param("trainNo") int trainNo,
                          @Param("journeyClass") JourneyClass journeyClass,
                          @Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate);
}
