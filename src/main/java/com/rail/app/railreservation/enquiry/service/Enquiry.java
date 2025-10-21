package com.rail.app.railreservation.enquiry.service;

import com.rail.app.railreservation.enquiry.dto.TrainEnquiryResponse;
import com.rail.app.railreservation.enquiry.entity.RouteMapping;
import com.rail.app.railreservation.common.entity.Train;
import com.rail.app.railreservation.enquiry.repository.RouteMappingRepository;
import com.rail.app.railreservation.common.repository.RouteRepository;
import com.rail.app.railreservation.common.repository.TrainRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class Enquiry {

    private static final Logger logger = LogManager.getLogger(Enquiry.class);

    private static final String COMMON_MESSAGE = "Inside Enquiry Service...";

    @Autowired
    private RouteMappingRepository routeMappingRepo;

    @Autowired
    private RouteRepository routeRepo;

    @Autowired
    private TrainRepository trainRepo;

    /*public TrainEnquiryResponse trainEnquiry(int trainNo){

    }*/

    public List<TrainEnquiryResponse> trainEnquiry(String src,String dest){

        logger.info(COMMON_MESSAGE);
        logger.info("Searching for trains running between {} and {}",src,dest);
        List<Integer> parentRouteIds = new ArrayList<>();

        // Step1: Obtain route ID for given source and destination
        int routeID = routeRepo.findBySrcAndDestn(src,dest);
        logger.info("Step1: Obtained routeID:{} for source:{} and destination:{}",routeID,src,dest);

        //Step2: Obtain those parent routes which have routeID as subroute
        List<RouteMapping> routeMappings = routeMappingRepo.findByChildRoutesContains(routeID);
        routeMappings.forEach(mapping-> {
                                            int parentRoouteId = mapping.getParentRoute();
                                            parentRouteIds.add(parentRoouteId);
                                        }
                             );

        logger.info("Step2: Obtained parent routes which have routeID:{} as subroute",routeID);

        List<TrainEnquiryResponse> trainEnquiryResponses = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        //Step3: Obtain trains that are running on parentRouteIds
        List<Train> availableTrains = trainRepo.findByRouteIdIn(parentRouteIds);
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

    /*public TrainEnquiryResponse trainEnquiry(String trainName){

    }

    public PnrEnquiryResponse pnrEnquiry(int pnrNo){

    }

    public SeatEnquiryResponse seatEnquiry(int trainNo, String doj){

    }*/

}
