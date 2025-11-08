package com.rail.app.railreservation.common.repository;

import com.rail.app.railreservation.common.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train,Integer> {

    List<Train> findByRouteIdIn(List<Integer> parentRouteIds);
    Train findByTrainNo(int trainNo);
    Train findByTrainName(String name);

   /* @Modifying
    @Transactional
    @Query(value = "")

    boolean addTrain();*/
}
