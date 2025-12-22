package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.SeatCount;
import com.rail.app.railreservation.trainmanagement.enums.JourneyClass;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SeatCountRepositoryTest {

    @Autowired
    private SeatCountRepository seatCountRepo;

    @Autowired
    private EntityManager entityManager;

    private SeatCount seatCount;

    private LocalDate startDate;
    private LocalDate endDate;
    private DateTimeFormatter pattern;

    private int trainNo;
    private JourneyClass journeyClass;
    private int count;

    @BeforeEach
    void setUp() {

        pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDate = LocalDate.now();
        endDate = startDate.plusDays(2);
        trainNo = 1;
        journeyClass = JourneyClass.AC1;
        count = 1;
        seatCount = new SeatCount(trainNo,startDate,endDate, journeyClass,count);

        seatCountRepo.save(seatCount);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void updateSeatCount() {

        seatCountRepo.updateSeatCount(trainNo,journeyClass,startDate,endDate,++count);

        entityManager.clear();

        List<SeatCount> seatCounts = seatCountRepo.findAll();

        int actualSeatCount = seatCounts.getFirst().getSeatCount();

        assertThat(actualSeatCount).isEqualTo(2);

    }

    @Test
    void findSeatCount() {

        entityManager.flush();

        int actualSeatCount = seatCountRepo.findSeatCount(trainNo,journeyClass,startDate,endDate);

        assertThat(actualSeatCount).isEqualTo(1);
    }
}