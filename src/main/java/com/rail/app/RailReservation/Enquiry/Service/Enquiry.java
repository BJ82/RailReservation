package com.rail.app.RailReservation.Enquiry.Service;

import com.rail.app.RailReservation.Enquiry.DTO.PnrEnquiryResponse;
import com.rail.app.RailReservation.Enquiry.DTO.SeatEnquiryResponse;
import com.rail.app.RailReservation.Enquiry.DTO.TrainEnquiryResponse;
import com.rail.app.RailReservation.Enquiry.Entity.RouteMapping;
import com.rail.app.RailReservation.Enquiry.Entity.Train;
import com.rail.app.RailReservation.Enquiry.Repository.RouteMappingRepository;
import com.rail.app.RailReservation.Enquiry.Repository.RouteRepository;
import com.rail.app.RailReservation.Enquiry.Repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Enquiry {

    @Autowired
    private RouteMappingRepository routeMappingRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TrainRepository trainRepository;

    public TrainEnquiryResponse trainEnquiry(int trainNo){

    }

    public TrainEnquiryResponse trainEnquiry(String src,String dest){

        int routeID = routeRepository.findBySrcAndDestn(src,dest);

        List<RouteMapping> routeMappings = routeMappingRepository.findByChildRoutesContains(routeID);

        List<Integer> parentRouteIds = new ArrayList<>();

        for(RouteMapping mapping:routeMappings){
            parentRouteIds.add(mapping.getparentRoute());
        }

        List<Train> availableTrains = trainRepository.findByRouteIdIn(parentRouteIds);



    }

    public TrainEnquiryResponse trainEnquiry(String trainName){

    }

    public PnrEnquiryResponse pnrEnquiry(int pnrNo){

    }

    public SeatEnquiryResponse seatEnquiry(int trainNo, String doj){

    }

    public SeatEnquiryResponse seatEnquiry(int trainName,String doj){

    }
}
