package com.rail.app.railreservation.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rail.app.railreservation.enquiry.entity.Route;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route,Integer> {

    @Query("SELECT DISTINCT r FROM Route r JOIN r.stations stn WHERE stn=:src OR stn=:dest")
    public List<Route> findBySrcAndDestn(@Param("src") String src, @Param("dest") String dest);

    @Query("SELECT DISTINCT r.routeID FROM Route r JOIN r.stations stn WHERE stn=:src AND stn=:dest")
    public List<Integer> findBySubRoute(@Param("src") String src, @Param("dest") String dest);
}
