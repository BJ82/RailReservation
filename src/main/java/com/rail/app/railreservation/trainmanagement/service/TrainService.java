package com.rail.app.railreservation.trainmanagement.service;

import com.rail.app.railreservation.commons.entity.Train;
import com.rail.app.railreservation.commons.repository.RouteRepository;
import com.rail.app.railreservation.commons.repository.TrainRepository;
import com.rail.app.railreservation.enquiry.entity.Route;
import com.rail.app.railreservation.enquiry.exception.RouteNotFoundException;
import com.rail.app.railreservation.enquiry.exception.TrainNotFoundException;
import com.rail.app.railreservation.trainmanagement.dto.AllTrainResponse;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddRequest;
import com.rail.app.railreservation.trainmanagement.dto.TrainAddResponse;
import com.rail.app.railreservation.trainmanagement.dto.TrainInfo;
import com.rail.app.railreservation.trainmanagement.exception.DuplicateTrainException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrainService {

    private static final Logger logger = LogManager.getLogger(TrainService.class);

    private static final String INSIDE_TRAIN_SERVICE = "Inside Train Service...";

    private final TrainRepository trainRepo;

    private final RouteRepository routeRepo;

    private final ModelMapper mapper;

    private Integer ROUTE_ID = null;

    public TrainService(TrainRepository trainRepo, RouteRepository routeRepo, ModelMapper mapper) {
        this.trainRepo = trainRepo;
        this.routeRepo = routeRepo;
        this.mapper = mapper;
    }


    public TrainAddResponse addNewTrain(TrainAddRequest trnReq) throws DuplicateTrainException {

        List<String> stations = trnReq.getStations();
        String src = stations.get(0);
        String dest = stations.get(stations.size() - 1);

        logger.info(INSIDE_TRAIN_SERVICE);
        logger.info("Adding train with name {}, running between {} and {}",trnReq.getTrainName(),src,dest);

        //Step1: Resolve src and dest to RouteID
        if (getRouteId(src, dest).isPresent()){

            ROUTE_ID = getRouteId(src, dest).get();
            logger.info("Step1: Resolved src and dest to RouteID:{}",ROUTE_ID);

        }

        //Step2: If route for src and dest is not found then add new route
        if (ROUTE_ID == null) {
            addRoute(stations);

            if (getRouteId(src, dest).isPresent())
                ROUTE_ID = getRouteId(src, dest).get();

            logger.info("ROUTE_ID:{}",ROUTE_ID);
            logger.info("Step2: Added new route for {} and {}",src,dest);
        }

        //Step3: If train not found then add train
        TrainAddResponse trnAddResponse = null;
        int trainNo = -1;
        if (trainRepo.findByTrainName(trnReq.getTrainName()).isEmpty()) {

            logger.info("Step3:Adding Train with Name:{}",trnReq.getTrainName());
            Train train = addTrain(trnReq,ROUTE_ID);

            if (train.getTrainNo() > 0){

                trainNo = train.getTrainNo();
                trnAddResponse = new TrainAddResponse(train.getTrainNo(), train.getTrainName(), src, dest, true);
                logger.info("Successfully Added Train with Name:{}, TrainNo{} , running between {} and {}",train.getTrainName(),train.getTrainNo(),src,dest);
            }

        } else {

            trainNo = trainRepo.findByTrainName(trnReq.getTrainName()).get().getTrainNo();
            throw new DuplicateTrainException(trnReq.getTrainName(),trainNo);

        }
        return trnAddResponse;
    }

    private Train addTrain(TrainAddRequest trnAddReq,Integer routeID){

        Train train = mapper.map(trnAddReq,Train.class);
        train.setRouteId(routeID);
        trainRepo.save(train);

        return train;
    }

    private void addRoute(List<String> stations ){

        List<String> stns = new ArrayList<>();
        for (int i = 0; i < stations.size(); i++) {

            stns.clear();
            stns.add(stations.get(i));

            for (int j = i + 1; j < stations.size(); j++) {

                stns.add(stations.get(j));

                Route newRoute = new Route();
                newRoute.getStations().addAll(stns);
                routeRepo.save(newRoute);
            }

        }
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

    public AllTrainResponse getAllTrains() throws TrainNotFoundException,RouteNotFoundException {

        logger.info(INSIDE_TRAIN_SERVICE);
        logger.info("Getting All Trains...");

        List<Train> trns = trainRepo.findAll();

        if(trns.isEmpty())
            throw new TrainNotFoundException("No Train Found.Pls Add New Train");

        Route route;
        TrainInfo trnInfo;
        AllTrainResponse allTrainResponse = new AllTrainResponse();

        for(Train trn:trns){

            trnInfo =  mapper.map(trn, TrainInfo.class);
            route = routeRepo.findByRouteID(trn.getRouteId()).orElseThrow(()->new RouteNotFoundException("Route Not Found For RouteID: "+trn.getRouteId(),trn.getRouteId()));
            trnInfo.getStns().addAll(route.getStations());
            allTrainResponse.getAllTrains().add(trnInfo);
        }

        return allTrainResponse;
    }
}
