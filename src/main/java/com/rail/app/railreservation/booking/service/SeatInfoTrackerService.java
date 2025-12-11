package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.BookingOpenRequest;
import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.entity.SeatCount;
import com.rail.app.railreservation.booking.entity.SeatNoTracker;
import com.rail.app.railreservation.booking.repository.BookingRepository;
import com.rail.app.railreservation.booking.repository.SeatCountRepository;
import com.rail.app.railreservation.booking.repository.SeatNoTrackerRepository;
import com.rail.app.railreservation.util.Utils;
import com.rail.app.railreservation.trainmanagement.enums.JourneyClass;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatInfoTrackerService {

    private final SeatNoTrackerRepository seatNoTrackerRepo;

    private final SeatCountRepository seatCountRepo;

    private final BookingRepository bookingRepo;

    public SeatInfoTrackerService(SeatNoTrackerRepository seatNoTrackerRepo, SeatCountRepository seatCountRepo, BookingRepository bookingRepo) {
        this.seatNoTrackerRepo = seatNoTrackerRepo;
        this.seatCountRepo = seatCountRepo;
        this.bookingRepo = bookingRepo;
    }

    public void initSeatInfoTracker(int trainNo, BookingOpenRequest request){

        for(JourneyClass jrnyClass:JourneyClass.values()){

            seatNoTrackerRepo.save(new SeatNoTracker(trainNo,
                            jrnyClass,Utils.toLocalDate(request.getStartDt()),
                            Utils.toLocalDate(request.getEndDt()),0
                    )
            );


            seatCountRepo.save(new SeatCount(trainNo,
                            Utils.toLocalDate(request.getStartDt()),
                            Utils.toLocalDate(request.getEndDt()),jrnyClass,0
                    )
            );
        }

    }

    public void trackCountOfSeats(BookingRequest request,int noOfConfirmedSeats){

        seatCountRepo.updateSeatCount(request.getTrainNo(),request.getJourneyClass(),
                Utils.toLocalDate(request.getStartDt()),
                Utils.toLocalDate(request.getEndDt()),noOfConfirmedSeats);


    }

    public int getLastAllocatedSeatNo(BookingRequest request){

        SeatNoTracker seatNoTracker = seatNoTrackerRepo.findSeatNoTracker(request.getTrainNo(),
                request.getJourneyClass(),
                Utils.toLocalDate(request.getStartDt()),
                Utils.toLocalDate(request.getEndDt())
        );

        return seatNoTracker.getLstSeatNum();

    }

    public void trackLastSeatNo(BookingRequest request,int lastGivenSeatNo){

        seatNoTrackerRepo.updateLastSeatNo(request.getTrainNo(),request.getJourneyClass(),
                Utils.toLocalDate(request.getStartDt()),
                Utils.toLocalDate(request.getEndDt()),lastGivenSeatNo);
    }

    public int getCountOfConfirmedSeats(BookingRequest request){

        int seatCount = seatCountRepo.findSeatCount(request.getTrainNo(),
                request.getJourneyClass(),
                Utils.toLocalDate(request.getStartDt()),
                Utils.toLocalDate(request.getEndDt()));

        return seatCount;

    }

    public List<Integer> getSeatNumbers(String startFrom, String endAt, BookingRequest request){

        return bookingRepo.findSeatNumbers(startFrom,endAt,request.getTrainNo(),
                                           Utils.toLocalDate(request.getStartDt()),
                                           Utils.toLocalDate(request.getEndDt()),
                                           request.getJourneyClass());
    }

}
