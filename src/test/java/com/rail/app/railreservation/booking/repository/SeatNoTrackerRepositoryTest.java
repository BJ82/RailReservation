package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.SeatNoTracker;
import com.rail.app.railreservation.trainmanagement.enums.JourneyClass;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SeatNoTrackerRepositoryTest {

    @Autowired
    private SeatNoTrackerRepository seatNoTrackerRepo;

    @Autowired
    private EntityManager entityManager;

    private SeatNoTracker seatNoTracker;

    private int trainNo;

    private JourneyClass journeyClass;

    private LocalDate startDate;

    private LocalDate endDate;

    private DateTimeFormatter pattern;

    private int lstSeatNum;

    private int id;

    @BeforeEach
    void setUp() {

        trainNo = 1;

        journeyClass = JourneyClass.AC1;

        pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDate = LocalDate.now();
        endDate = startDate.plusDays(2);

        lstSeatNum = 1;

        seatNoTracker = new SeatNoTracker(trainNo,journeyClass,startDate,endDate,lstSeatNum);

        seatNoTracker = seatNoTrackerRepo.save(seatNoTracker);

        id = seatNoTracker.getId();
    }

    @Test
    void findSeatNoTracker() {

        seatNoTracker = null;
        seatNoTracker = seatNoTrackerRepo.findSeatNoTracker(trainNo,journeyClass,startDate,endDate);

        assertThat(seatNoTracker.getId()).isEqualTo(id);
    }

    @Test
    void updateLastSeatNo() {

        seatNoTrackerRepo.updateLastSeatNo(trainNo,journeyClass,startDate,endDate,2);
        seatNoTracker = null;
        entityManager.clear();
        seatNoTracker = seatNoTrackerRepo.findById(id).orElse(new SeatNoTracker());

        assertThat(seatNoTracker.getLstSeatNum()).isEqualTo(2);

    }
}