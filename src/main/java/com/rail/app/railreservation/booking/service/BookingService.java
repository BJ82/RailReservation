package com.rail.app.railreservation.booking.service;

import com.rail.app.railreservation.booking.dto.BookedResponse;
import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.common.entity.Train;
import com.rail.app.railreservation.common.repository.RouteRepository;
import com.rail.app.railreservation.common.repository.TrainRepository;
import com.rail.app.railreservation.booking.exception.InvalidBookingException;
import com.rail.app.railreservation.enquiry.entity.Route;
import com.rail.app.railreservation.trainmanagement.repository.TimeTableRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LogManager.getLogger(BookingService.class);

    private static final String INSIDE_BOOKING_SERVICE = "Inside Booking Service...";

    private TrainRepository trainRepo;

    private RouteRepository routeRepo;

    private TimeTableRepository timeTableRepo;

    public BookingService(RouteRepository routeRepo, TrainRepository trainRepo,TimeTableRepository timeTableRepo) {
        this.routeRepo = routeRepo;
        this.trainRepo = trainRepo;
        this.timeTableRepo = timeTableRepo;
    }

    ModelMapper mapper = new ModelMapper();

    public BookedResponse book(BookingRequest request) throws InvalidBookingException{

        Train trn = trainRepo.findByTrainNo(request.getTrainNo()).orElseThrow(()->new InvalidBookingException("Booking Not Allowed On Non Existent Train"));

        List<Integer> parentRoutes = getParentRoutes(request.getFrom(),request.getTo());

        List<Integer> childRoutes = getChildRoutes(request.getFrom(),request.getTo());

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

    /*public BookedResponse bookTatkal(BookingRequest request){

    }

    public boolean cancelBooking(int bookingID){

    }*/

}
