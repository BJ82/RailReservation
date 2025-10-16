package com.rail.app.RailReservation.Enquiry.Repository;

import com.rail.app.RailReservation.Enquiry.Entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train,Integer> {

    public List<Train> findByRouteIdIn(List<Integer> parentRouteIds);
}
