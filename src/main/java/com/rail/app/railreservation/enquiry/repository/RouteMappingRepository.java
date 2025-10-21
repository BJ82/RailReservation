package com.rail.app.railreservation.enquiry.repository;

import com.rail.app.railreservation.enquiry.entity.RouteMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteMappingRepository extends JpaRepository<RouteMapping,Integer> {

    public List<RouteMapping> findByChildRoutesContains(Integer routeID);
}
