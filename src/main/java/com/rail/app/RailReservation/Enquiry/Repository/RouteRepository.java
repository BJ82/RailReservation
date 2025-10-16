package com.rail.app.RailReservation.Enquiry.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rail.app.RailReservation.Enquiry.Entity.Route;

public interface RouteRepository extends JpaRepository<Route,Integer> {

    public int findBySrcAndDestn(String src,String dest);
}
