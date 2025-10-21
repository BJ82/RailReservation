package com.rail.app.railreservation.trainmanagement.service;

import com.rail.app.railreservation.common.repository.RouteRepository;
import com.rail.app.railreservation.common.repository.TrainRepository;
import com.rail.app.railreservation.common.entity.Train;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddRequest;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

        ROUTE_ID = routeRepo.findBySrcAndDestn(src,dest);
        if(ROUTE_ID != null){
            if(trainRepo.findByTrainNo(trnReq.getTrainNo()) == null){

                ModelMapper modelMapper = getMapper(trnReq);
                Train train = modelMapper.map(trnReq, Train.class);
                train.setRouteId(ROUTE_ID);
                train = trainRepo.save(train);

                if(train.getTrainNo() != null)
                    return new TrainAddResponse(train.getTrainNo(),train.getTrainName(),src,dest,true);
            }
        }
    }

    private ModelMapper getMapper(TrainAddRequest trnReq){
        ModelMapper modelMapper = new ModelMapper();
        PropertyMap<TrainAddRequest, Train> skipStationsField = new PropertyMap<TrainAddRequest, Train>() {
            @Override
            protected void configure() {

                skip(trnReq.getStations());
            }
        };
        modelMapper.addMappings(skipStationsField);

        return modelMapper;
    }
}
