package com.rail.app.railreservation.enquiry.service;

import com.rail.app.railreservation.enquiry.dto.TrainEnquiryResponse;
import com.rail.app.railreservation.enquiry.entity.Route;
import com.rail.app.railreservation.common.entity.Train;
import com.rail.app.railreservation.common.repository.RouteRepository;
import com.rail.app.railreservation.common.repository.TrainRepository;
import com.rail.app.railreservation.enquiry.exception.RouteNotFoundException;
import com.rail.app.railreservation.enquiry.exception.TrainNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Enquiry {

    private static final Logger logger = LogManager.getLogger(Enquiry.class);

    private static final String COMMON_MESSAGE = "Inside Enquiry Service...";

    private RouteRepository routeRepo;
    private TrainRepository trainRepo;

    public Enquiry(RouteRepository routeRepo, TrainRepository trainRepo) {
        this.routeRepo = routeRepo;
        this.trainRepo = trainRepo;
    }


    /*public TrainEnquiryResponse trainEnquiry(int trainNo){

    }*/

    public List<TrainEnquiryResponse> trainEnquiry(String src,String dest) throws TrainNotFoundException {

        logger.info(COMMON_MESSAGE);
        logger.info("Searching for trains running between {} and {}",src,dest);

        List<Integer> parentRouteIds = new ArrayList<>();


        // Step1: Obtain route ID for given source and destination
        Integer routeID = null;

        if (getRouteId(src, dest).isPresent())
            routeID = getRouteId(src, dest).get();

        logger.info("Step1: Obtained routeID:{} for source:{} and destination:{}",routeID,src,dest);


        //Step2: Obtain routes which contain routeID as subroute
        parentRouteIds.addAll(getParentRoutes(src,dest));

        logger.info("parentRouteIds size:{}",parentRouteIds.size());

        logger.info("Step2: Obtained parent routes which have routeID:{} as subroute",routeID);

        List<TrainEnquiryResponse> trainEnquiryResponses = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        //Step3: Obtain trains that are running on parentRouteIds
        List<Train> availableTrains = trainRepo.findByRouteIdIn(parentRouteIds);

        if(availableTrains.isEmpty())
            throw new TrainNotFoundException("No Train Found Between Stations "+src+" And "+dest,src,dest);

        logger.info("Number Of Available Trains:{}",availableTrains.size());
        availableTrains.forEach(

                                    (train)-> {
                                                TrainEnquiryResponse trainEnquiryResponse = modelMapper.map(train, TrainEnquiryResponse.class);
                                                trainEnquiryResponse.setSrc(src);
                                                trainEnquiryResponse.setDest(dest);
                                                trainEnquiryResponses.add(trainEnquiryResponse);
                                             }
                                );

        logger.info("Step3: Obtained trains that are running on parentRouteIds");

        return trainEnquiryResponses;
    }

    public TrainEnquiryResponse trainEnquiry(Integer trainNo) throws TrainNotFoundException, RouteNotFoundException {

        logger.info(COMMON_MESSAGE);
        logger.info("Searching For Train With TrainNo:{}", trainNo);


        Train trn = trainRepo.findByTrainNo(trainNo).
                orElseThrow(() -> new TrainNotFoundException("Train Not Found For TrainNo: " + trainNo, trainNo));


        Integer routeID = trn.getRouteId();

        Route route= routeRepo.findByRouteID(routeID).orElseThrow(()->new RouteNotFoundException("Route Not Found For RouteID: "+routeID,routeID));
        List<String> stations = route.getStations();

        TrainEnquiryResponse trainEnquiryResponse = new ModelMapper().map(trn, TrainEnquiryResponse.class);
        trainEnquiryResponse.setSrc(stations.getFirst());
        trainEnquiryResponse.setDest(stations.getLast());

        return trainEnquiryResponse;
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



    /*public TrainEnquiryResponse trainEnquiry(String trainName){

    }

    public PnrEnquiryResponse pnrEnquiry(int pnrNo){

    }

    public SeatEnquiryResponse seatEnquiry(int trainNo, String doj){

    }*/

}
