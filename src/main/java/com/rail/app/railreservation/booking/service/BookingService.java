package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.*;
import com.rail.app.railreservation.booking.entity.BookingOpen;
import com.rail.app.railreservation.booking.entity.SeatCount;
import com.rail.app.railreservation.booking.entity.SeatNoTracker;
import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.booking.entity.Booking;
import com.rail.app.railreservation.booking.exception.BookingCannotOpenException;
import com.rail.app.railreservation.booking.exception.BookingNotOpenException;
import com.rail.app.railreservation.booking.repository.BookingOpenRepository;
import com.rail.app.railreservation.booking.repository.BookingRepository;
import com.rail.app.railreservation.booking.repository.SeatCountRepository;
import com.rail.app.railreservation.booking.repository.SeatNoTrackerRepository;
import com.rail.app.railreservation.common.entity.Train;
import com.rail.app.railreservation.common.enums.JourneyClass;
import com.rail.app.railreservation.common.repository.RouteRepository;
import com.rail.app.railreservation.common.repository.TrainRepository;
import com.rail.app.railreservation.booking.exception.InvalidBookingException;
import com.rail.app.railreservation.enquiry.entity.Route;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LogManager.getLogger(BookingService.class);

    private static final String INSIDE_BOOKING_SERVICE = "Inside Booking Service...";

    private final Set<Integer> seatNumbers;

    private BookingStatus BOOKING_STATUS = BookingStatus.CONFIRMED;

    private final List<Integer> pnrs;

    private TrainRepository trainRepo;

    private RouteRepository routeRepo;

    private SeatCountRepository seatCountRepo;

    private BookingRepository bookingRepo;

    private SeatNoTrackerRepository seatNoTrackerRepo;

    private BookingOpenRepository bookingOpenRepo;

    public BookingService(RouteRepository routeRepo, TrainRepository trainRepo,
                          SeatCountRepository seatCountRepo, BookingRepository bookingRepo,
                          SeatNoTrackerRepository seatNoTrackerRepo,
                          BookingOpenRepository bookingOpenRepo) {

        this.routeRepo = routeRepo;
        this.trainRepo = trainRepo;
        this.seatCountRepo = seatCountRepo;
        this.bookingRepo = bookingRepo;
        this.seatNoTrackerRepo = seatNoTrackerRepo;
        this.bookingOpenRepo = bookingOpenRepo;
        this.seatNumbers = Collections.synchronizedSet(new LinkedHashSet<>());
        this.pnrs = Collections.synchronizedList(new ArrayList<>());
    }

    ModelMapper mapper = new ModelMapper();

    public BookingOpenResponse openBooking(BookingOpenRequest request)
                                           throws BookingCannotOpenException{

        logger.info(INSIDE_BOOKING_SERVICE);

        LocalDate startDt = toLocalDate(request.getStartDt());

        if(startDt.isBefore(LocalDate.now()))
            throw new BookingCannotOpenException("Booking Open Date Cannot Be In Past.");

        trainRepo.findByTrainNo(request.getTrainNo())
                .orElseThrow(() -> new BookingCannotOpenException("Not Allowed To Open Booking On Non Existent Train"));

        logger.info("Processing To Open Booking For TrainNo:{}, StartDate:{}, EndDate{}",
                    request.getTrainNo(),request.getStartDt(),request.getEndDt());

        bookingOpenRepo.save(new BookingOpen(request.getTrainNo(),toLocalDate(request.getStartDt()),
                                             toLocalDate(request.getEndDt()),true,
                                             Timestamp.from(Instant.now())
                                            )
                            );

        for(JourneyClass jrnyClass:JourneyClass.values()){

            seatNoTrackerRepo.save(new SeatNoTracker(request.getTrainNo(),
                                                     jrnyClass,toLocalDate(request.getStartDt()),
                                                     toLocalDate(request.getEndDt()),0
                                                    )
                                  );


            seatCountRepo.save(new SeatCount(request.getTrainNo(),
                                             toLocalDate(request.getStartDt()),
                                             toLocalDate(request.getEndDt()),jrnyClass,0
                                            )
                              );
        }

        logger.info("Booking Opened For TrainNo:{}, StartDate:{}, EndDate:{}",
                    request.getTrainNo(),request.getStartDt(),request.getEndDt());

        return new BookingOpenResponse(request.getTrainNo(),request.getStartDt(),
                                       request.getEndDt(),true);
    }

    public BookingOpenInfo getBookingOpenInfo(int trainNo){

        List<BookingOpen> bookingOpens;
        bookingOpens = bookingOpenRepo.findByTrainNo(trainNo);

        List<BookingOpenDate> bookingOpenDates = new ArrayList<>();

        for(BookingOpen bookingOpen:bookingOpens){
            bookingOpenDates.add(mapper.map(bookingOpen,BookingOpenDate.class));
        }

        return new BookingOpenInfo(trainNo,bookingOpenDates);
    }


    public BookingResponse book(BookingRequest request) throws InvalidBookingException,BookingNotOpenException{

        logger.info(INSIDE_BOOKING_SERVICE);

        //Check if Train No is Valid
        Train trn = trainRepo.findByTrainNo(request.getTrainNo())
                .orElseThrow(() -> new InvalidBookingException("Booking Not Allowed On Non Existent Train"));

        //Check if Route is valid
        isValidRoute(request.getFrom(), request.getTo(), trn)
                .orElseThrow(() -> new InvalidBookingException("TrainNo:" + request.getTrainNo() + " Not Running " + "Between " +
                                                                request.getFrom() + "And " + request.getTo()));
        //Check If Booking Is Allowed
        isBookingOpen(request).orElseThrow(()->new BookingNotOpenException("Booking Not Yet Open For TrainNo:"+request.getTrainNo()+" For Dates "+request.getStartDt()+" And "+request.getEndDt()
                                                                      )
                                          );

        logger.info("Processing Ticket Booking For TrainNo:{}, StartDate:{}, EndDate:{}",
                     request.getTrainNo(),request.getStartDt(),request.getEndDt());


        seatNumbers.clear();
        seatNumbers.addAll(getSeatNumbers(request));

        pnrs.clear();

        int seatCount = getSeatCount(request);
        int seatNumber = 0;

        int seatNumberToUpdate = 0;
        int i = 0;
        for (Passenger psngr : request.getPassengers()) {


            if(i < seatNumbers.size()){

                seatNumber = new ArrayList<>(seatNumbers).get(i);
                seatNumberToUpdate = seatNumber;
                BOOKING_STATUS = BookingStatus.CONFIRMED;
                seatCount++;
            }
            else {

                seatNumber = 0;
                BOOKING_STATUS = BookingStatus.WAITING;
            }

            Booking bkng = bookingRepo.save(new Booking(psngr.getName(), psngr.getAge(), psngr.getSex(),
                            request.getTrainNo(),toLocalDate(request.getStartDt()),
                            toLocalDate(request.getEndDt()),
                            request.getFrom(),request.getTo(),toLocalDate(request.getDoj()),
                            request.getJourneyClass(), BOOKING_STATUS,Timestamp.from(Instant.now()),
                            seatNumber
                    )
            );


            pnrs.add(i,bkng.getPnr());

            i++;

        }

        if(seatNumberToUpdate != 0) {

            seatNoTrackerRepo.updateLastSeatNo(request.getTrainNo(),request.getJourneyClass(),
                    toLocalDate(request.getStartDt()),
                    toLocalDate(request.getEndDt()),seatNumberToUpdate);

        }



        seatCountRepo.updateSeatCount(request.getTrainNo(),request.getJourneyClass(),
                                      toLocalDate(request.getStartDt()),
                                      toLocalDate(request.getEndDt()),seatCount);

        BookingResponse bookingResponse = mapper.map(request, BookingResponse.class);
        bookingResponse.setBookingDateTime(Timestamp.from(Instant.now()));

        BookedPassenger bookedPassenger;

        int noOfPsngr = request.getPassengers().size();

        for(int j=0;j<noOfPsngr;j++){

            bookedPassenger = mapper.map(request.getPassengers().get(j),BookedPassenger.class);

            if(j < seatNumbers.size()){

                seatNumber = new ArrayList<>(seatNumbers).get(j);
                BOOKING_STATUS = BookingStatus.CONFIRMED;
            }
            else {

                seatNumber = 0;
                BOOKING_STATUS = BookingStatus.WAITING;
            }

            bookedPassenger.setPnr(pnrs.get(j));
            bookedPassenger.setSeatNo(seatNumber);
            bookedPassenger.setStatus(BOOKING_STATUS);

            bookingResponse.getPassengerList().add(bookedPassenger);

        }

        logger.info("Completed Ticket Booking For TrainNo:{}, StartDate:{}, EndDate:{}",
                             request.getTrainNo(),request.getStartDt(),request.getEndDt());
        return bookingResponse;

    }

    private Optional<Boolean> isBookingOpen(BookingRequest request){

        return bookingOpenRepo.isBookingOpen(request.getTrainNo(),toLocalDate(request.getStartDt()),
                toLocalDate(request.getEndDt()));
    }

    private LocalDate toLocalDate(String dateAsString){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        return LocalDate.parse(dateAsString,formatter);
    }

    private Set<Integer> getSeatNumbers(BookingRequest request){


        //Retrieve Last Allotted Seat Number
        SeatNoTracker seatNoTracker = seatNoTrackerRepo.findSeatNoTracker(request.getTrainNo(),
                request.getJourneyClass(),
                toLocalDate(request.getStartDt()),
                toLocalDate(request.getEndDt())
        );

        Set<Integer> seatNums;
        seatNums = Collections.synchronizedSet(new LinkedHashSet<>());

        AtomicInteger lstAllotedSeatNum;
        lstAllotedSeatNum = new AtomicInteger(seatNoTracker.getLstSeatNum());

        int seatsAvailable = 4 - seatNoTracker.getLstSeatNum();

        for(int i=1;i<=seatsAvailable;i++){

            seatNums.add(lstAllotedSeatNum.addAndGet(1));
        }


        //Obtain Seat Nos which would be free
        //Before Journey Starts

        seatNums.addAll(getSeatNosBefore(request));


        //Obtain Seat Nos which would be used
        //After Journey Ends

        seatNums.addAll(getSeatNosAfter(request));


        return seatNums;
    }

    private Integer getSeatCount(BookingRequest request){

        int seatCount = seatCountRepo.findSeatCount(request.getTrainNo(),
                                                    request.getJourneyClass(),
                                                    toLocalDate(request.getStartDt()),
                                                    toLocalDate(request.getEndDt()));

        return seatCount;
    }

    private Set<Integer> getSeatNosBefore(BookingRequest request){

        String src,dest;

        Set<Integer> seatNums = new LinkedHashSet<>();

        List<String> allStations = getAllStations(request.getTrainNo());

        int before = allStations.indexOf(request.getFrom());

        for(int i=0;i<=before;i++){

            src = allStations.get(i);

            for(int j=i+1;j<=before;j++){

                dest = allStations.get(j);

                seatNums.addAll(bookingRepo.findSeatNumbers( src,dest,request.getTrainNo(),
                                                             toLocalDate(request.getStartDt()),
                                                             toLocalDate(request.getEndDt()),
                                                             request.getJourneyClass()
                                                           )
                               );

                if(seatNums.size() >= request.getPassengers().size())
                    break;
            }
        }

        filterSeatNos(seatNums,request);
        return seatNums;
    }

    private void filterSeatNos(Set<Integer> seatNums,BookingRequest request){

        Set<Integer> seatNosToRetain = new LinkedHashSet<>(seatNums);

        for(Integer num:seatNums){

                List<Booking> bookings = bookingRepo.findBySeatNo(num,request.getTrainNo(),
                                                                  request.getJourneyClass(),
                                                                  toLocalDate(request.getStartDt()),
                                                                  toLocalDate(request.getEndDt()));

                String src,dest;
                Integer routeID;
                boolean isOverlapp = false;

                for(Booking bkng:bookings){

                    src = bkng.getStartFrom();
                    dest = bkng.getEndAt();
                    routeID = getRouteId(src,dest).get();

                    isOverlapp = getOverlappingRoutes(request.getFrom(),
                                                      request.getTo()).contains(routeID);

                    if(isOverlapp)
                        seatNosToRetain.remove(num);
                }


        }

        seatNums.clear();
        seatNums.addAll(seatNosToRetain);
    }

    private Set<Integer> getSeatNosAfter(BookingRequest request){

        String src,dest;

        Set<Integer> seatNums = new LinkedHashSet<>();

        List<String> allStations = getAllStations(request.getTrainNo());

        int after = allStations.indexOf(request.getTo());

        for (int i = after; i < allStations.size(); i++) {

            src = allStations.get(i);

            for (int j = i + 1; j < allStations.size(); j++) {

                dest = allStations.get(j);

                seatNums.addAll(bookingRepo.findSeatNumbers(src, dest, request.getTrainNo(),
                                                            toLocalDate(request.getStartDt()),
                                                            toLocalDate(request.getEndDt()),
                                                            request.getJourneyClass()
                                                           )
                               );

                if (seatNums.size() >= request.getPassengers().size())
                    break;
            }
        }

        filterSeatNos(seatNums,request);
        return seatNums;
    }

    private List<String> getAllStations(int trainNo){

        Train train;
        train = trainRepo.findByTrainNo(trainNo).get();

        int routeID;
        routeID = train.getRouteId();

        Route r;
        r = routeRepo.findByRouteID(routeID).get();

        return new ArrayList<>(r.getStations());
    }

    private Optional<Boolean> isValidRoute(String jurnyStartStn,String jurnyEndStn,Train trn){

            boolean isRouteValid = false;

            Route route = routeRepo.findByRouteID(trn.getRouteId()).get();

            List<String> stns = route.getStations();

            if(stns.contains(jurnyStartStn) && stns.contains(jurnyEndStn)){
                if(stns.indexOf(jurnyStartStn) < stns.indexOf(jurnyEndStn)){
                    isRouteValid = true;
                }
            }

           return toOptional(isRouteValid);
    }

    private Optional<Boolean> toOptional(boolean b){

        Optional asOptional;

        if(!b)
            asOptional = Optional.empty();

        asOptional = Optional.of(b);

        return asOptional;
    }

    private List<Integer> getOverlappingRoutes(String src, String dest){

        List<Route> routes = routeRepo.findBySrcAndDestn(src, dest);

        List<Route> overlappingRoutes = new ArrayList<>(routes);

        for(Route route:overlappingRoutes){

            if(route.getStations().getLast().equals(src) || route.getStations().getFirst().equals(dest))
                routes.remove(route);
        }

        return routes.stream().map(r->r.getRouteID()).collect(Collectors.toList());

    }

    private List<Integer> getChildRoutes(String src, String dest){

        List<String> stns = new ArrayList<>();
        int routeID = getRouteId(src,dest).orElse(-1);
        if(routeID != -1){
            Route route = routeRepo.findByRouteID(routeID).orElse(null);
            if(route != null){
                stns.addAll(route.getStations());
            }
        }

        List<Integer> childRoutes = new ArrayList<>();
        String source;
        String destn;
        for(int i=0;i<stns.size();i++){
            source = stns.get(i);
            for(int j=i+1;j<stns.size();j++){
                destn = stns.get(j);
                childRoutes.add(getRouteId(source,destn).get());
            }
        }

        return childRoutes;
    }

    private Optional<Integer> getRouteId(String src, String dest){

        List<Route> routes = routeRepo.findBySrcAndDestn(src, dest);

        return routes.stream().filter(r->{   boolean isTrue = false;
            if(r.getStations().get(0).equals(src)){
                if(r.getStations().get(r.getStations().size()-1).equals(dest))
                    isTrue = true;
            }
            return isTrue;
        }).map(r->r.getRouteID()).findFirst();
    }


    /*public BookedResponse; bookTatkal(BookingRequest request){

    }

    public boolean cancelBooking(int bookingID){

    }*/

}
