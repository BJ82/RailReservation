package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.BookingOpenRequest;
import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.dto.Passenger;
import com.rail.app.railreservation.booking.entity.Booking;
import com.rail.app.railreservation.booking.entity.BookingOpen;
import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.booking.repository.BookingOpenRepository;
import com.rail.app.railreservation.booking.repository.BookingRepository;
import com.rail.app.railreservation.trainmanagement.enums.JourneyClass;
import com.rail.app.railreservation.util.Utils;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingInfoTrackerService {

    private final BookingRepository bookingRepo;

    private final BookingOpenRepository bookingOpenRepo;

    public BookingInfoTrackerService(BookingRepository bookingRepo, BookingOpenRepository bookingOpenRepo) {
        this.bookingRepo = bookingRepo;
        this.bookingOpenRepo = bookingOpenRepo;
    }

    public int trackBooking(Passenger psngr, BookingRequest request,
                             BookingStatus BOOKING_STATUS,int seatNumber){

        Booking bkng =  bookingRepo.save(new Booking(psngr.getName(), psngr.getAge(), psngr.getSex(),
                                                    request.getTrainNo(), Utils.toLocalDate(request.getStartDt()),
                                                    Utils.toLocalDate(request.getEndDt()),
                                                    request.getFrom(),request.getTo(), Utils.toLocalDate(request.getDoj()),
                                                    request.getJourneyClass(), BOOKING_STATUS, Timestamp.from(Instant.now()),
                                                    seatNumber));

        return bkng.getPnr();
    }

    public int trackBooking(Booking booking){

        booking = bookingRepo.save(booking);
        return booking.getPnr();
    }

    public List<Booking> getBookingBySeatNumber(int seatNumber,BookingRequest request){

        return bookingRepo.findBySeatNo(seatNumber,request.getTrainNo(),
                                        request.getJourneyClass(),
                                        Utils.toLocalDate(request.getStartDt()),
                                        Utils.toLocalDate(request.getEndDt()));

    }

    public Optional<List<Booking>> getBookingBySeatNumber(int seatNumber,Booking booking){

        return Optional.of(bookingRepo.findBySeatNo(seatNumber, booking.getTrainNo(),
                                                                booking.getJourneyClass(),
                                                                booking.getStartDt(),
                                                                booking.getEndDt()));

    }

    public void trackBookingOpen(int trainNo, BookingOpenRequest request){

        bookingOpenRepo.save(new BookingOpen(trainNo,Utils.toLocalDate(request.getStartDt()),
                                            Utils.toLocalDate(request.getEndDt()),true,
                                            Timestamp.from(Instant.now())
                )
        );

    }

    public Optional<Booking> getBookingByPnrNo(int pnrNo){

        return bookingRepo.findById(pnrNo);
    }

    public void deleteBookingByPnrNo(int pnrNo){

        bookingRepo.deleteById(pnrNo);
    }

    public Optional<List<Booking>> getWaitingList(int trainNo,
                                                  JourneyClass jrnyClass, LocalDate strtDt,
                                                  LocalDate endDt){

        return bookingRepo.findByBookingStatus(BookingStatus.WAITING,trainNo,jrnyClass,strtDt,endDt);

    }

    public void changeBookingToConfirm(int pnrNo,int seatNoToAllocate){

        bookingRepo.updateBooking(pnrNo,seatNoToAllocate,BookingStatus.CONFIRMED);
    }

}
