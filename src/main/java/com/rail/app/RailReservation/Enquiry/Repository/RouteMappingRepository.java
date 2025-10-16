package com.rail.app.RailReservation.Enquiry.Repository;

import com.rail.app.RailReservation.Enquiry.Entity.RouteMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteMappingRepository extends JpaRepository<RouteMapping,Integer> {

    public List<RouteMapping> findByChildRoutesContains(Integer routeID);
}
