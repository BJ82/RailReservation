package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.Booking;
import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.common.enums.JourneyClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Integer> {

    @Query("SELECT TOP 1 * FROM Booking b" +
            "WHERE b.name = :name AND b.age = :age AND b.sex = :sex " +
            "AND b.trainNo = :trainNo AND b.from = :frm" +
            "AND b.to = :to AND b.dtOfJourny = :doj" +
            "AND b.journeyClass = :journyClass " +
            "AND b.bookingStatus = :bkngStatus" +
            "ORDER BY b.timestamp DESC ")
    Booking findLastBooking(@Param("name") String name,@Param("age") int age,
                            @Param("sex") String sex,@Param("trainNo") int trainNo, @Param("frm") String frm,
                            @Param("to") String to, @Param("doj")LocalDate doj,
                            @Param("journyClass")JourneyClass journeyClass,
                            @Param("bkngStatus")BookingStatus bkngStatus);



    @Query("SELECT TOP 1 * FROM Booking b" +
            "WHERE b.name = :name AND b.age = :age AND b.sex = :sex " +
            "AND b.trainNo = :trainNo AND b.from = :frm" +
            "AND b.to = :to AND b.dtOfJourny = :doj" +
            "AND b.journeyClass = :journyClass " +
            "AND b.bookingStatus = :bkngStatus" +
            "ORDER BY b.timestamp ASC ")
    Booking findFirstBooking(@Param("name") String name,@Param("age") int age,
                             @Param("sex") String sex,@Param("trainNo") int trainNo, @Param("frm") String frm,
                             @Param("to") String to, @Param("doj")LocalDate doj,
                             @Param("journyClass")JourneyClass journeyClass,
                             @Param("bkngStatus")BookingStatus bkngStatus);




    @Query("SELECT b.seatNo FROM Booking b" +
           "WHERE b.from = :frm AND b.to = :to" +
           "AND b.trainNo = :trainNo" +
           "AND b.journeyClass = :jrnyClass" +
           "AND b.startDt = :strtDt" +
           "AND b.endDt = :endDt")
    List<Integer> findSeatNumbers(@Param("frm") String frm,@Param("to") String to,
                                     @Param("trainNo") int trainNo,@Param("strtDt") LocalDate strtDt,
                                     @Param("endDt") LocalDate endDt,
                                     @Param("jrnyClass") JourneyClass jrnyClass );



    @Query("SELECT COUNT(*) FROM Booking b" +
            "WHERE b.trainNo = :trainNo" +
            "AND b.journeyClass = :jrnyClass" +
            "AND b.startDt = :strtDt" +
            "AND b.endDt = :endDt" +
            "AND b.seatNo = :seatNo")
    int findCountOfSeatNumber(@Param("trainNo") int trainNo,
                  @Param("jrnyClass") JourneyClass jrnyClass,
                  @Param("strtDt") LocalDate strtDt,
                  @Param("endDt") LocalDate endDt,
                  @Param("seatNo") int seatNo);

    @Query("SELECT * FROM Booking b" +
            "WHERE b.seatNo = :seatNo" +
            "WHERE b.trainNo = :trainNo" +
            "AND b.journeyClass = :jrnyClass" +
            "AND b.startDt = :strtDt" +
            "AND b.endDt = :endDt")
    List<Booking> findBySeatNo(@Param("seatNo") int seatNo,@Param("trainNo") int trainNo,
                               @Param("jrnyClass") JourneyClass jrnyClass,
                               @Param("strtDt") LocalDate strtDt,
                               @Param("endDt") LocalDate endDt);

}
