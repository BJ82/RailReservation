package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.entity.SeatNoTracker;
import com.rail.app.railreservation.booking.enums.BookingStatus;
import com.rail.app.railreservation.booking.dto.BookedResponse;
import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.dto.Passenger;
import com.rail.app.railreservation.booking.entity.Booking;
import com.rail.app.railreservation.booking.repository.BookingRepository;
import com.rail.app.railreservation.booking.repository.SeatCounterRepository;
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

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LogManager.getLogger(BookingService.class);

    private static final String INSIDE_BOOKING_SERVICE = "Inside Booking Service...";

    private TrainRepository trainRepo;

    private RouteRepository routeRepo;

    private SeatCounterRepository seatCountRepo;

    private BookingRepository bookingRepo;

    private SeatNoTrackerRepository seatNoTrackerRepo;

    public BookingService(RouteRepository routeRepo, TrainRepository trainRepo,
                          SeatCounterRepository seatCountRepo,BookingRepository bookingRepo,
                          SeatNoTrackerRepository seatNoTrackerRepo) {

        this.routeRepo = routeRepo;
        this.trainRepo = trainRepo;
        this.seatCountRepo = seatCountRepo;
        this.bookingRepo = bookingRepo;
        this.seatNoTrackerRepo = seatNoTrackerRepo;
    }

    ModelMapper mapper = new ModelMapper();

    public BookedResponse book(BookingRequest request) throws InvalidBookingException{

        //Check if Train No is Valid
        Train trn = trainRepo.findByTrainNo(request.getTrainNo())
                .orElseThrow(() -> new InvalidBookingException("Booking Not Allowed On Non Existent Train"));

        //Check if Route is valid
        isValidRoute(request.getFrom(), request.getTo(), trn)
                .orElseThrow(() -> new InvalidBookingException("TrainNo:" + request.getTrainNo() + " Not Running " + "Between " +
                                                                request.getFrom() + "And " + request.getTo()));

        //isBookingOpen();
        //isSeatAvailable();

        /*
          Get All Route IDS
          This includes the main journey route
          And all the parent and child routes
          Of the main route

        */

        List<Integer> routeIDS = new ArrayList<>();
        routeIDS.add(trn.getRouteId());

        List<Integer> parentRoutes = getParentRoutes(request.getFrom(), request.getTo());
        routeIDS.addAll(parentRoutes);

        List<Integer> childRoutes = getChildRoutes(request.getFrom(), request.getTo());
        routeIDS.addAll(childRoutes);

        //Update(Decrement) Seat Count By No Of Passenger In Booking Request.
        seatCountRepo.updateSeatCount(request.getTrainNo(),routeIDS,
                                      request.getPassengers().size(),
                                      LocalDate.parse(request.getDoj()),
                                      request.getJourneyClass());

        //Retrieve The Last Alloted Seat Number
        SeatNoTracker seatNoTracker = seatNoTrackerRepo.findSeatNoTracker(request.getTrainNo(),
                                                                            request.getJourneyClass(),
                                                                            LocalDate.parse(request.getDoj()));

        AtomicInteger lstAllotedSeatNum = new AtomicInteger(seatNoTracker.getLstSeatNum());

        int seatNum = 0;
        for(Passenger psngr:request.getPassengers()){

            seatNum = lstAllotedSeatNum.addAndGet(1);

            bookingRepo.save(new Booking(psngr.getName(),psngr.getAge(),psngr.getSex(),
                                         request.getTrainNo(),request.getFrom(),request.getTo(),
                                         LocalDate.parse(request.getDoj()),request.getJourneyClass(),
                                         BookingStatus.CONFIRMED,seatNum));
        }

        //Save The Last Newly Alloted Seat Number
        seatNoTracker.setLstSeatNum(seatNum);
        seatNoTrackerRepo.save(seatNoTracker);



    }

    private Optional<Boolean> isValidRoute(String jurnyStartStn,String jurnyEndStn,Train trn){

            boolean isRouteValid = false;

            Route route = routeRepo.findByRouteID(trn.getRouteID()).get();

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

    private List<Integer> getParentRoutes(String src, String dest){

        List<Route> routes = routeRepo.findBySrcAndDestn(src, dest);

        return routes.stream().filter(r->{   boolean isTrue = false;
            if(r.getStations().contains(src)){
                if(r.getStations().contains(dest))
                    isTrue = true;
            }
            return isTrue;
        }).map(r->r.getRouteID()).collect(Collectors.toList());

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
