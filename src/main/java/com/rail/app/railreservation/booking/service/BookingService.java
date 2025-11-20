package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.*;
import com.rail.app.railreservation.booking.entity.SeatNoTracker;
import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.booking.entity.Booking;
import com.rail.app.railreservation.booking.repository.BookingRepository;
import com.rail.app.railreservation.booking.repository.SeatCountRepository;
import com.rail.app.railreservation.booking.repository.SeatNoTrackerRepository;
import com.rail.app.railreservation.common.entity.Train;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LogManager.getLogger(BookingService.class);

    private static final String INSIDE_BOOKING_SERVICE = "Inside Booking Service...";

    private final List<Integer> seatNumbers;

    private BookingStatus BOOKING_STATUS = BookingStatus.CONFIRMED;

    private final List<Integer> pnrs;

    private TrainRepository trainRepo;

    private RouteRepository routeRepo;

    private SeatCountRepository seatCountRepo;

    private BookingRepository bookingRepo;

    private SeatNoTrackerRepository seatNoTrackerRepo;

    public BookingService(RouteRepository routeRepo, TrainRepository trainRepo,
                          SeatCountRepository seatCountRepo, BookingRepository bookingRepo,
                          SeatNoTrackerRepository seatNoTrackerRepo) {

        this.routeRepo = routeRepo;
        this.trainRepo = trainRepo;
        this.seatCountRepo = seatCountRepo;
        this.bookingRepo = bookingRepo;
        this.seatNoTrackerRepo = seatNoTrackerRepo;
        this.seatNumbers = Collections.synchronizedList(new ArrayList<>());
        this.pnrs = Collections.synchronizedList(new ArrayList<>());
    }

    ModelMapper mapper = new ModelMapper();

    public OpenBookingResponse openBooking(){

    }

    public BookingResponse book(BookingRequest request) throws InvalidBookingException{

        //Check if Train No is Valid
        Train trn = trainRepo.findByTrainNo(request.getTrainNo())
                .orElseThrow(() -> new InvalidBookingException("Booking Not Allowed On Non Existent Train"));

        //Check if Route is valid
        isValidRoute(request.getFrom(), request.getTo(), trn)
                .orElseThrow(() -> new InvalidBookingException("TrainNo:" + request.getTrainNo() + " Not Running " + "Between " +
                                                                request.getFrom() + "And " + request.getTo()));

        //isBookingOpen();

        int noOfPsngr = request.getPassengers().size();
        seatNumbers.addAll(getSeatNumbers(noOfPsngr,request));

        int seatCount = getSeatCount(request);
        int seatNumber;
        int i = 0;
        for (Passenger psngr : request.getPassengers()) {


            if(i < seatNumbers.size()){

                seatNumber = seatNumbers.get(i);
                BOOKING_STATUS = BookingStatus.CONFIRMED;
                seatCount++;
            }
            else {

                seatNumber = 0;
                BOOKING_STATUS = BookingStatus.WAITING;
            }

            Booking bkng = bookingRepo.save(new Booking(psngr.getName(), psngr.getAge(), psngr.getSex(),
                            request.getTrainNo(),LocalDate.parse(request.getStartDt()),
                            LocalDate.parse(request.getEndDt()),
                            request.getFrom(),request.getTo(),LocalDate.parse(request.getDoj()),
                            request.getJourneyClass(), BOOKING_STATUS,Timestamp.from(Instant.now()),
                            seatNumber
                    )
            );


            pnrs.add(i,bkng.getPnr());

            i++;

        }

        seatCountRepo.updateSeatCount(request.getTrainNo(),request.getJourneyClass(),
                                      LocalDate.parse(request.getStartDt()),
                                      LocalDate.parse(request.getEndDt()),seatCount);

        BookingResponse bookingResponse = mapper.map(request, BookingResponse.class);
        bookingResponse.setBookingDateTime(Timestamp.from(Instant.now()));

        i = 0;
        for(BookedPassenger bookedPassenger:bookingResponse.getPassengerList()){


            if(i < seatNumbers.size()){

                seatNumber = seatNumbers.get(i);
                BOOKING_STATUS = BookingStatus.CONFIRMED;
            }
            else {

                seatNumber = 0;
                BOOKING_STATUS = BookingStatus.WAITING;
            }

            bookedPassenger.setPnr(pnrs.get(i));
            bookedPassenger.setSeatNo(seatNumber);
            bookedPassenger.setStatus(BOOKING_STATUS);

            i++;
        }

        return bookingResponse;

    }

    private List<Integer> getSeatNumbers(int noOfPsngr,BookingRequest request){

        List<Integer> routeIDS = new ArrayList<>();
        routeIDS.add(getRouteId(request.getFrom(), request.getTo()).get());

        List<Integer> overlappingRoutes = getOverlappingRoutes(request.getFrom(), request.getTo());
        routeIDS.addAll(overlappingRoutes);

        List<Integer> childRoutes = getChildRoutes(request.getFrom(), request.getTo());
        routeIDS.addAll(childRoutes);

        int seatsBooked = getSeatCount(request);

        //Retrieve Last Allotted Seat Number
        SeatNoTracker seatNoTracker = seatNoTrackerRepo.findSeatNoTracker(request.getTrainNo(),
                request.getJourneyClass(),
                LocalDate.parse(request.getStartDt()),
                LocalDate.parse(request.getEndDt())
        );

        List<Integer> seatNums;
        seatNums = Collections.synchronizedList(new ArrayList<>());

        AtomicInteger lstAllotedSeatNum = new AtomicInteger(seatNoTracker.getLstSeatNum());

        int seatsAvailable = 50 - seatsBooked;
        for(int i=1;i<=seatsAvailable;i++){

            seatNums.add(lstAllotedSeatNum.addAndGet(1));
        }


        //Save Last Allotted Seat Number

        seatNoTracker.setLstSeatNum(seatNums.getLast());
        seatNoTrackerRepo.save(seatNoTracker);


        //Add Seat Nums Which Will Be De-Occupied
        //Before Our Journey Starts

        seatNums.addAll(getSeatNumsBefore(request));
        seatNums.addAll(filterByAvlbSeatNums(seatNums,request));


        //Add Seat Nums Which Will Be Occupied
        //After Our Journey Ends

        seatNums.addAll(getSeatNumsAfter(request));
        seatNums.addAll(filterByAvlbSeatNums(seatNums,request));

        return seatNums;
    }

    private Integer getSeatCount(BookingRequest request){

        int seatCount = seatCountRepo.findSeatCount(request.getTrainNo(),
                                                    request.getJourneyClass(),
                                                    LocalDate.parse(request.getStartDt()),
                                                    LocalDate.parse(request.getEndDt()));

        return seatCount;
    }

    private List<Integer> getSeatNumsBefore(BookingRequest request){

        String src;
        String dest;
        List<String> stns = getAllStations(request.getTrainNo());
        List<Integer> seatNums = new ArrayList<>();

        for(int i=0;i<=stns.indexOf(request.getFrom());i++){
            src = stns.get(i);
            for(int j=i+1;j<=stns.indexOf(request.getFrom());j++){
                dest = stns.get(j);
                seatNums.addAll(bookingRepo.findSeatNumbers( src,dest,request.getTrainNo(),
                                LocalDate.parse(request.getStartDt()),
                                LocalDate.parse(request.getEndDt()),
                                request.getJourneyClass()
                        )
                );

                if(seatNums.size() >= request.getPassengers().size())
                    break;
            }
        }

        return seatNums;
    }

    private List<Integer> filterByAvlbSeatNums(List<Integer> seatNums,BookingRequest request){

        List<Integer> avlblSeatNums = new ArrayList<>();
        int count = 0;
        for(Integer num:seatNums){

            count = bookingRepo.findCountOfSeatNumber(request.getTrainNo(),
                                                      request.getJourneyClass(),
                                                      LocalDate.parse(request.getStartDt()),
                                                      LocalDate.parse(request.getEndDt()),num
                                                     );
            if(count == 1){
                avlblSeatNums.add(num);
            }
            else if(count > 1){
                List<Booking> bookings = bookingRepo.findBySeatNo(num,request.getTrainNo(),
                                                                  request.getJourneyClass(),
                                                                  LocalDate.parse(request.getStartDt()),
                                                                  LocalDate.parse(request.getEndDt()));

                String src;
                String dest;
                Integer routeID;
                boolean isOverlapp = false;
                for(Booking bkng:bookings){
                    src = bkng.getStartFrom();
                    dest = bkng.getEndAt();
                    routeID = getRouteId(src,dest).get();
                    isOverlapp = getOverlappingRoutes(request.getFrom(),
                                                      request.getTo()).contains(routeID);

                    if(isOverlapp)
                        break;
                }
                if(!isOverlapp)
                    avlblSeatNums.add(num);
            }
        }

        return avlblSeatNums;
    }

    private List<Integer> getSeatNumsAfter(BookingRequest request){

        String src;
        String dest;
        List<String> stns = getAllStations(request.getTrainNo());
        List<Integer> seatNums = new ArrayList<>();

        for (int i = stns.indexOf(request.getTo()); i < stns.size(); i++) {
            src = stns.get(i);
            for (int j = i + 1; j < stns.size(); j++) {
                dest = stns.get(j);
                seatNums.addAll(bookingRepo.findSeatNumbers(src, dest, request.getTrainNo(),
                                LocalDate.parse(request.getStartDt()),
                                LocalDate.parse(request.getEndDt()),
                                request.getJourneyClass()
                        )
                );

                if (seatNums.size() >= request.getPassengers().size())
                    break;
            }
        }
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
