package com.rail.app.railreservation.commons.service;

import com.rail.app.railreservation.commons.repository.RouteRepository;
import org.springframework.stereotype.Service;
import com.rail.app.railreservation.enquiry.entity.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RouteInfoService {

    private final RouteRepository routeRepo;

    public RouteInfoService(RouteRepository routeRepo) {
        this.routeRepo = routeRepo;
    }

    public Optional<Route> getByRouteId(int routeId){

        return routeRepo.findByRouteID(routeId);
    }

    public List<Route> containsSrcOrDest(String src,String dest){

        return routeRepo.findBySrcAndDestn(src,dest);
    }

    public Optional<Integer> getBySrcAndDest(String src, String dest){

        List<Route> routes = routeRepo.findBySrcAndDestn(src, dest);

        return routes.stream().filter(r->{   boolean isTrue = false;
            if(r.getStations().get(0).equals(src)){
                if(r.getStations().get(r.getStations().size()-1).equals(dest))
                    isTrue = true;
            }
            return isTrue;
        }).map(r->r.getRouteID()).findFirst();
    }

    public void addRoute(List<String> stations ){

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

}
