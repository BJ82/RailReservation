package com.rail.app.railreservation.booking.repository;

import com.rail.app.railreservation.booking.entity.BookingOpen;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class BookingOpenRepositoryTest {

    @Autowired
    private BookingOpenRepository bookingOpenRepoUnderTest;

    @Test
    void checkWhenBookingIsOpen() {

        //given
        LocalDate startDate = LocalDate.now();

        BookingOpen bookingOpen = new BookingOpen(1,
                startDate, startDate.plusDays(1),
                true, Timestamp.from(Instant.now()));

        bookingOpenRepoUnderTest.save(bookingOpen);

        //when
        Optional<Boolean> expected = bookingOpenRepoUnderTest.isBookingOpen(1,startDate,
                                                        startDate.plusDays(1));

        //then
        assertThat(expected.get()).isTrue();

    }

    @Test
    void checkWhenBookingIsClosed() {

        //given
        LocalDate startDate = LocalDate.now();

        //when
        Optional<Boolean> expected = bookingOpenRepoUnderTest.isBookingOpen(1,startDate,
                startDate.plusDays(1));

        //then
        assertThat(expected.isEmpty()).isTrue();

    }
}