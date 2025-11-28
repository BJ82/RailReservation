package com.rail.app.railreservation.trainmanagement.repository;

import com.rail.app.railreservation.trainmanagement.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {

    Optional<TimeTable> findByTrainNo(int trainNo);

}
