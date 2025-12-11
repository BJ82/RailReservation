package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.*;
import com.rail.app.railreservation.booking.entity.Booking;
import com.rail.app.railreservation.booking.entity.BookingOpen;
import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.booking.exception.BookingCannotOpenException;
import com.rail.app.railreservation.booking.exception.BookingNotOpenException;
import com.rail.app.railreservation.booking.exception.InvalidBookingException;
import com.rail.app.railreservation.commons.Utils;
import com.rail.app.railreservation.commons.entity.Train;
import com.rail.app.railreservation.commons.repository.RouteRepository;
import com.rail.app.railreservation.commons.service.TrainInfoService;
import com.rail.app.railreservation.enquiry.entity.Route;
import com.rail.app.railreservation.enquiry.exception.TrainNotFoundException;
import com.rail.app.railreservation.trainmanagement.exception.TimeTableNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
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

    private final TrainInfoService trainInfoService;

    private final RouteRepository routeRepo;

    private final SeatInfoTrackerService seatInfoTrackerService;

    private final BookingInfoTrackerService bookingInfoTrackerService;

    private final BookingOpenInfoTrackerService bookingOpenInfoTrackerService;

    private final ModelMapper mapper;

    public BookingService(RouteRepository routeRepo, TrainInfoService trainInfoService,
                          SeatInfoTrackerService seatInfoTrackerService,
                          BookingInfoTrackerService bookingInfoTrackerService,
                          BookingOpenInfoTrackerService bookingOpenInfoTrackerService,
                          ModelMapper mapper) {

        this.routeRepo = routeRepo;
        this.trainInfoService = trainInfoService;
        this.seatInfoTrackerService = seatInfoTrackerService;
        this.bookingInfoTrackerService = bookingInfoTrackerService;
        this.bookingOpenInfoTrackerService = bookingOpenInfoTrackerService;
        this.mapper = mapper;
        this.seatNumbers = Collections.synchronizedSet(new LinkedHashSet<>());
        this.pnrs = Collections.synchronizedList(new ArrayList<>());
    }

    public BookingOpenResponse openBooking(int trainNo,BookingOpenRequest request)
                                           throws BookingCannotOpenException{

        logger.info(INSIDE_BOOKING_SERVICE);

        LocalDate startDt = Utils.toLocalDate(request.getStartDt());

        if(startDt.isBefore(LocalDate.now()))
            throw new BookingCannotOpenException("Booking Open Date Cannot Be In Past.");

        trainInfoService.getByTrainNo(trainNo)
                .orElseThrow(() -> new BookingCannotOpenException("Not Allowed To Open Booking On Non Existent Train"));

        logger.info("Processing To Open Booking For TrainNo:{}, StartDate:{}, EndDate{}",
                trainNo,request.getStartDt(),request.getEndDt());


        bookingOpenInfoTrackerService.initBookingOpenInfoTracker(trainNo,request);

        seatInfoTrackerService.initSeatInfoTracker(trainNo,request);

        logger.info("Booking Opened For TrainNo:{}, StartDate:{}, EndDate:{}",
                trainNo,request.getStartDt(),request.getEndDt());

        return new BookingOpenResponse(trainNo,request.getStartDt(),
                                       request.getEndDt(),true);
    }

    public BookingOpenInfo getBookingOpenInfo(int trainNo){

        List<BookingOpen> bookingOpens;
        bookingOpens = bookingOpenInfoTrackerService.getBookingOpenByTrainNo(trainNo);

        List<BookingOpenDate> bookingOpenDates = new ArrayList<>();

        for(BookingOpen bookingOpen:bookingOpens){
            bookingOpenDates.add(mapper.map(bookingOpen,BookingOpenDate.class));
        }

        return new BookingOpenInfo(trainNo,bookingOpenDates);
    }


    public BookingResponse book(BookingRequest request) throws InvalidBookingException, BookingNotOpenException, TimeTableNotFoundException {

        logger.info(INSIDE_BOOKING_SERVICE);

        //Check if Train No is Valid
        Train trn = trainInfoService.getByTrainNo(request.getTrainNo())
                .orElseThrow(() -> new InvalidBookingException("Booking Not Allowed On Non Existent Train"));

        //Check if Route is valid
        isValidRoute(request.getFrom(), request.getTo(), trn)
                .orElseThrow(() -> new InvalidBookingException("TrainNo:" + request.getTrainNo() + " Not Running " + "Between " +
                                                                request.getFrom() + "And " + request.getTo()));
        //Check If Booking Is Allowed
        bookingOpenInfoTrackerService.isBookingOpen(request).orElseThrow(()->new BookingNotOpenException("Booking Not Yet Open For TrainNo:"+request.getTrainNo()+" For Dates "+request.getStartDt()+" And "+request.getEndDt()
                                                                      )
                                          );

        //Check if DOJ is Valid
        String startFrom = request.getFrom();

        LocalDate trainStartDateFrmSource = Utils.toLocalDate(request.getStartDt());
        LocalDate dateOfArrival =  Utils.getArrivalDate(request.getTrainNo(),startFrom,trainStartDateFrmSource);
        LocalDate dateOfJourney = Utils.toLocalDate(request.getDoj());

        if(!dateOfArrival.equals(dateOfJourney))
            throw new InvalidBookingException("Invalid Booking Because ",
                    new TrainNotFoundException("No Train Found For Date Of Journey: "+dateOfJourney.toString()));




        logger.info("Processing Ticket Booking For TrainNo:{}, StartDate:{}, EndDate:{}",
                     request.getTrainNo(),request.getStartDt(),request.getEndDt());


        seatNumbers.clear();
        seatNumbers.addAll(getAvailableSeatNumbers(request));

        pnrs.clear();

        int seatCount = seatInfoTrackerService.getCountOfConfirmedSeats(request);
        int seatNumber = 0;

        int lastSeatNumber = 0;
        int i = 0;
        for (Passenger psngr : request.getPassengers()) {


            if(i < seatNumbers.size()){

                seatNumber = new ArrayList<>(seatNumbers).get(i);
                lastSeatNumber = seatNumber;
                BOOKING_STATUS = BookingStatus.CONFIRMED;
                seatCount++;
            }
            else {

                seatNumber = 0;
                BOOKING_STATUS = BookingStatus.WAITING;
            }


            int pnrNo = bookingInfoTrackerService.trackBooking(psngr,request,BOOKING_STATUS,seatNumber);

            pnrs.add(i,pnrNo);

            i++;

        }

        if(lastSeatNumber != 0) {

            seatInfoTrackerService.trackLastSeatNo(request,lastSeatNumber);

        }


        seatInfoTrackerService.trackCountOfSeats(request,seatCount);

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

    public Set<Integer> getAvailableSeatNumbers(BookingRequest request){


        Set<Integer> seatNums;
        seatNums = Collections.synchronizedSet(new LinkedHashSet<>());

        AtomicInteger lstAllotedSeatNum;
        lstAllotedSeatNum = new AtomicInteger(seatInfoTrackerService.getLastAllocatedSeatNo(request));

        int seatsAvailable = 4 - seatInfoTrackerService.getLastAllocatedSeatNo(request);

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


    private Set<Integer> getSeatNosBefore(BookingRequest request){

        String src,dest;

        Set<Integer> seatNums = new LinkedHashSet<>();

        List<String> allStations = getAllStations(request.getTrainNo());

        int before = allStations.indexOf(request.getFrom());

        for(int i=0;i<=before;i++){

            src = allStations.get(i);

            for(int j=i+1;j<=before;j++){

                dest = allStations.get(j);

                seatNums.addAll(seatInfoTrackerService.getSeatNumbers(src,dest,request));

            }
        }

        filterSeatNos(seatNums,request);
        return seatNums;
    }

    private void filterSeatNos(Set<Integer> seatNums,BookingRequest request){

        Set<Integer> seatNosToRetain = new LinkedHashSet<>(seatNums);

        for(Integer num:seatNums){

            List<Booking> bookings = bookingInfoTrackerService.getBookingBySeatNumber(num,request);

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

                seatNums.addAll(seatInfoTrackerService.getSeatNumbers(src,dest,request));
            }
        }

        filterSeatNos(seatNums,request);
        return seatNums;
    }

    private List<String> getAllStations(int trainNo){

        Train train;
        train = trainInfoService.getByTrainNo(trainNo).get();

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

           return Utils.toOptional(isRouteValid);
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
