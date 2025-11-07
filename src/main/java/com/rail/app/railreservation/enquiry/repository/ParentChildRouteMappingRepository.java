package com.rail.app.railreservation.enquiry.repository;

import com.rail.app.railreservation.enquiry.entity.ParentChildRouteMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParentChildRouteMappingRepository extends JpaRepository<ParentChildRouteMapping,Integer> {

    public List<ParentChildRouteMapping> findByChildRoutesContains(Integer routeID);
}
