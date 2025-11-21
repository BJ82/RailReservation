package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.BookingOpen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingOpenRepository extends JpaRepository<BookingOpen,Integer> {

    List<BookingOpen> findByTrainNo(int trainNo);

    @Query("SELECT bo.isBookingOpen " +
           "FROM BookingOpen bo " +
           "WHERE bo.trainNo = :trainNo " +
           "AND bo.startDt = :startDt " +
           "AND bo.endDt = :endDt")
    Optional<Boolean> isBookingOpen(@Param("trainNo") int trainNo,
                                    @Param("startDt") LocalDate startDt,
                                    @Param("endDt") LocalDate endDt
                         );
}
