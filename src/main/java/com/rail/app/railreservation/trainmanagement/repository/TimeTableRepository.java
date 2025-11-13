package com.rail.app.railreservation.trainmanagement.repository;

import com.rail.app.railreservation.trainmanagement.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {

}
