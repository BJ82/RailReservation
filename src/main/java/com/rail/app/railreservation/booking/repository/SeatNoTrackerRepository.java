package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.SeatNoTracker;
import com.rail.app.railreservation.commons.enums.JourneyClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface SeatNoTrackerRepository extends JpaRepository<SeatNoTracker,Integer> {

    @Query("SELECT snt FROM SeatNoTracker snt " +
            "WHERE snt.trainNo = :trainNo AND " +
            "snt.journeyClass = :jrnyClass AND " +
            "snt.startDt = :startDt AND " +
            "snt.endDt = :endDt")
    SeatNoTracker findSeatNoTracker(@Param("trainNo") int trainNo,
                                    @Param("jrnyClass") JourneyClass journeyClass,
                                    @Param("startDt") LocalDate startDt,
                                    @Param("endDt") LocalDate endDt
                                   );

    @Modifying
    @Transactional
    @Query("UPDATE SeatNoTracker snt " +
            "SET snt.lstSeatNum = :lastSeatNo " +
            "WHERE snt.trainNo = :trainNo " +
            "AND snt.journeyClass = :journeyClass " +
            "AND snt.startDt = :startDate " +
            "AND snt.endDt = :endDate")
    void updateLastSeatNo(@Param("trainNo") int trainNo,
                          @Param("journeyClass") JourneyClass journeyClass,
                          @Param("startDate") LocalDate startDt,
                          @Param("endDate") LocalDate endDt,
                          @Param("lastSeatNo") int lstSeatNum);
}
