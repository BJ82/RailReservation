package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.BookingOpenRequest;
import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.entity.BookingOpen;
import com.rail.app.railreservation.booking.repository.BookingOpenRepository;
import com.rail.app.railreservation.util.Utils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class BookingOpenInfoService {

    private final BookingOpenRepository bookingOpenRepo;

    public BookingOpenInfoService(BookingOpenRepository bookingOpenRepo) {
        this.bookingOpenRepo = bookingOpenRepo;
    }

    public void addBookingOpenInfo(int trainNo, BookingOpenRequest request){

        bookingOpenRepo.save(new BookingOpen(trainNo, Utils.toLocalDate(request.getStartDt()),
                        Utils.toLocalDate(request.getEndDt()),true,
                        Timestamp.from(Instant.now())
                )
        );

    }

    public Optional<Boolean> isBookingOpen(BookingRequest request){

        Optional<Boolean> isBookingOpenAsOptional = bookingOpenRepo.isBookingOpen(request.getTrainNo(),Utils.toLocalDate(request.getStartDt()),
                Utils.toLocalDate(request.getEndDt()));

        if(isBookingOpenAsOptional.get() == true)
            return isBookingOpenAsOptional;

        return Optional.empty();
    }

    public List<BookingOpen> getBookingOpenInfoByTrainNo(int trainNo){

        return bookingOpenRepo.findByTrainNo(trainNo);
    }

}
