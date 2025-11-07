package com.rail.app.railreservation.trainmanagement.service;

import com.rail.app.railreservation.common.entity.Train;
import com.rail.app.railreservation.common.repository.RouteRepository;
import com.rail.app.railreservation.common.repository.TrainRepository;
import com.rail.app.railreservation.enquiry.entity.Route;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddRequest;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainService {

    private static final Logger logger = LogManager.getLogger(TrainService.class);

    private static final String COMMON_MESSAGE = "Inside TrainAddRequest Service...";

    @Autowired
    private TrainRepository trainRepo;

    @Autowired
    private RouteRepository routeRepo;

    private Integer ROUTE_ID;

    public TrainAddResponse addTrain(TrainAddRequest trnReq){

        List<String> stations = trnReq.getStations();
        String src = stations.get(0);
        String dest = stations.get(stations.size() - 1);

        List<Route> routes = routeRepo.findBySrcAndDestn(src,dest);
        List<Integer> routeIDS = getTrainRouteId(src,dest,routes);

        if(routeIDS.isEmpty()){

        }

        if(trainRepo.findByTrainNo(trnReq.getTrainNo()) == null){

            Train train = convertToTrain(trnReq);
            train.setRouteId(ROUTE_ID);

            train = trainRepo.save(train);

            if(train.getTrainNo() > 0)
                return new TrainAddResponse(train.getTrainNo(),train.getTrainName(),src,dest,true);
        }

    }
    private List<Integer> getTrainRouteId(String src,String dest,List route){

       return route.stream().filter(r->{
                                                  if(r.getStations().get(0).equals(src)){
                                                      if(r.getStations().get(r.getStations().size()-1).equals(dest))
                                                         return true;
                                                  }

                                              }).map(r->r.getRouteID()).collect(Collectors.toList());
    }

    private Train convertToTrain(TrainAddRequest trnReq){
        ModelMapper modelMapper = new ModelMapper();
        PropertyMap<TrainAddRequest, Train> skipStationsField = new PropertyMap<TrainAddRequest, Train>() {
            @Override
            protected void configure() {

                skip(trnReq.getStations());
            }
        };
        modelMapper.addMappings(skipStationsField);
        return modelMapper.map(trnReq,Train.class);
    }

    /*private ModelMapper getMapper(TrainAddRequest trnReq){
        ModelMapper modelMapper = new ModelMapper();
        PropertyMap<TrainAddRequest, Train> skipStationsField = new PropertyMap<TrainAddRequest, Train>() {
            @Override
            protected void configure() {

                skip(trnReq.getStations());
            }
        };
        modelMapper.addMappings(skipStationsField);

        return modelMapper;
    }*/
}
