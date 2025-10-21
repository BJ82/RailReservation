package com.rail.app.railreservation.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rail.app.railreservation.enquiry.entity.Route;

public interface RouteRepository extends JpaRepository<Route,Integer> {

    public int findBySrcAndDestn(String src,String dest);
}
