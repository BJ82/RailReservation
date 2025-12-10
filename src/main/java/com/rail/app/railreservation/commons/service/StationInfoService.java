package com.rail.app.railreservation.commons.service;

import com.rail.app.railreservation.commons.entity.Train;
import com.rail.app.railreservation.commons.repository.RouteRepository;
import com.rail.app.railreservation.commons.repository.TrainRepository;
import com.rail.app.railreservation.enquiry.entity.Route;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StationInfoService {

    private final TrainRepository trainRepo;

    private final RouteRepository routeRepo;

    public StationInfoService(TrainRepository trainRepo, RouteRepository routeRepo) {
        this.trainRepo = trainRepo;
        this.routeRepo = routeRepo;
    }

    public List<String> getAllStations(int trainNo){

        Train train = trainRepo.findByTrainNo(trainNo).get();

        return getAllStations((long)train.getRouteId());
    }

    public List<String> getAllStations(long routeId){

        Route r;
        r = routeRepo.findByRouteID((int)routeId).get();

        return new ArrayList<>(r.getStations());

    }

    public List<String> getAllStations(String startFrom, String endAt){

        int routeId = getRouteId(startFrom,endAt).orElse(-1);

        List<String> allStations = new ArrayList<>();

        if(routeId != -1)
            allStations.addAll(getAllStations(routeId));

        return allStations;
    }

    private Optional<Integer> getRouteId(String startFrom, String endAt){

        List<Route> routes = routeRepo.findBySrcAndDestn(startFrom, endAt);

        return routes.stream().filter(r->{   boolean isTrue = false;
            if(r.getStations().get(0).equals(startFrom)){
                if(r.getStations().get(r.getStations().size()-1).equals(endAt))
                    isTrue = true;
            }
            return isTrue;
        }).map(r->r.getRouteID()).findFirst();
    }
}
