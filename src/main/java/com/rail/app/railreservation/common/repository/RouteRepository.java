package com.rail.app.railreservation.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rail.app.railreservation.enquiry.entity.Route;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route,Integer> {

    @Query("SELECT DISTINCT r FROM Route r JOIN r.stations stn WHERE stn=:src OR stn=:dest")
    public List<Route> findBySrcAndDestn(@Param("src") String src, @Param("dest") String dest);

    Optional<Route> findByRouteID(Integer routeID);



}
