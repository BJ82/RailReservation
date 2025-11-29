package com.rail.app.railreservation.commons.repository;

import com.rail.app.railreservation.commons.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainRepository extends JpaRepository<Train,Integer> {

    List<Train> findByRouteIdIn(List<Integer> parentRouteIds);
    Optional<Train> findByTrainNo(int trainNo);
    Optional<Train> findByTrainName(String name);

}
