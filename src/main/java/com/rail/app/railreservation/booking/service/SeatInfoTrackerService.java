package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.BookingOpenRequest;
import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.entity.SeatCount;
import com.rail.app.railreservation.booking.entity.SeatNoTracker;
import com.rail.app.railreservation.booking.repository.SeatCountRepository;
import com.rail.app.railreservation.booking.repository.SeatNoTrackerRepository;
import com.rail.app.railreservation.commons.Utils;
import com.rail.app.railreservation.commons.enums.JourneyClass;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class SeatInfoTrackerService {

    private final SeatNoTrackerRepository seatNoTrackerRepo;

    private final SeatCountRepository seatCountRepo;

    public SeatInfoTrackerService(SeatNoTrackerRepository seatNoTrackerRepo, SeatCountRepository seatCountRepo) {
        this.seatNoTrackerRepo = seatNoTrackerRepo;
        this.seatCountRepo = seatCountRepo;
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
}
