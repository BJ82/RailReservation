package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.Booking;
import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.trainmanagement.enums.JourneyClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Integer> {

    @Query("SELECT b FROM Booking b " +
            "WHERE b.name = :name AND b.age = :age AND b.sex = :sex " +
            "AND b.trainNo = :trainNo AND b.startFrom = :frm " +
            "AND b.endAt = :to AND b.dtOfJourny = :doj " +
            "AND b.journeyClass = :journyClass " +
            "AND b.bookingStatus = :bkngStatus " +
            "ORDER BY b.timestamp DESC LIMIT 1"
          )
    Booking findLastBooking(@Param("name") String name,@Param("age") int age,
                            @Param("sex") String sex,@Param("trainNo") int trainNo, @Param("frm") String startFrom,
                            @Param("to") String to, @Param("doj") LocalDate dtOfJourny,
                            @Param("journyClass") JourneyClass journeyClass,
                            @Param("bkngStatus") BookingStatus bookingStatus);



    @Query("SELECT b FROM Booking b " +
            "WHERE b.name = :name AND b.age = :age AND b.sex = :sex " +
            "AND b.trainNo = :trainNo AND b.startFrom = :frm " +
            "AND b.endAt = :to AND b.dtOfJourny = :doj " +
            "AND b.journeyClass = :journyClass " +
            "AND b.bookingStatus = :bkngStatus " +
            "ORDER BY b.timestamp ASC LIMIT 1")
    Booking findFirstBooking(@Param("name") String name,@Param("age") int age,
                             @Param("sex") String sex,@Param("trainNo") int trainNo, @Param("frm") String startFrom,
                             @Param("to") String endAt, @Param("doj") LocalDate doj,
                             @Param("journyClass") JourneyClass journeyClass,
                             @Param("bkngStatus") BookingStatus bkngStatus);




    @Query("SELECT b.seatNo FROM Booking b " +
           "WHERE b.startFrom = :frm AND b.endAt = :to " +
           "AND b.trainNo = :trainNo " +
           "AND b.journeyClass = :jrnyClass " +
           "AND b.startDt = :strtDt " +
           "AND b.endDt = :endDt")
    List<Integer> findSeatNumbers(@Param("frm") String startFrom,@Param("to") String endAt,
                                     @Param("trainNo") int trainNo,@Param("strtDt") LocalDate strtDt,
                                     @Param("endDt") LocalDate endDt,
                                     @Param("jrnyClass") JourneyClass jrnyClass );



    @Query("SELECT COUNT(*) FROM Booking b " +
            "WHERE b.trainNo = :trainNo " +
            "AND b.journeyClass = :jrnyClass " +
            "AND b.startDt = :strtDt " +
            "AND b.endDt = :endDt " +
            "AND b.seatNo = :seatNo")
    int findCountOfSeatNumber(@Param("trainNo") int trainNo,
                  @Param("jrnyClass") JourneyClass jrnyClass,
                  @Param("strtDt") LocalDate strtDt,
                  @Param("endDt") LocalDate endDt,
                  @Param("seatNo") int seatNo);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.seatNo = :seatNo " +
            "AND b.trainNo = :trainNo " +
            "AND b.journeyClass = :jrnyClass " +
            "AND b.startDt = :strtDt " +
            "AND b.endDt = :endDt")
    List<Booking> findBySeatNo(@Param("seatNo") int seatNo,@Param("trainNo") int trainNo,
                               @Param("jrnyClass") JourneyClass jrnyClass,
                               @Param("strtDt") LocalDate strtDt,
                               @Param("endDt") LocalDate endDt);


    @Query("SELECT b FROM Booking b " +
           "WHERE b.pnr = :pnrNo")
    Optional<Booking> findBookingstatus(@Param("pnrNo") int pnrNo);





    @Query("SELECT b FROM Booking b " +
            "WHERE b.bookingStatus = :bookingStatus " +
            "AND b.trainNo = :trainNo " +
            "AND b.journeyClass = :jrnyClass " +
            "AND b.startDt = :strtDt " +
            "AND b.endDt = :endDt")
    Optional<List<Booking>> findByBookingStatus(@Param("bookingStatus") BookingStatus bookingStatus,
                                      @Param("trainNo") int trainNo,
                                      @Param("jrnyClass") JourneyClass jrnyClass,
                                      @Param("strtDt") LocalDate strtDt,
                                      @Param("endDt") LocalDate endDt);


}
