package com.rail.app.railreservation.util;

import com.rail.app.railreservation.trainmanagement.dto.TimeTableEnquiryResponse;
import com.rail.app.railreservation.trainmanagement.entity.Timing;
import com.rail.app.railreservation.trainmanagement.exception.TimeTableNotFoundException;
import com.rail.app.railreservation.trainmanagement.service.TimeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class Utils {

    private static TimeTableService timeTableService;

    public Utils(TimeTableService timeTableService) {

        this.timeTableService = timeTableService;
    }

    public static LocalDate toLocalDate(String dateAsString){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        return LocalDate.parse(dateAsString,formatter);
    }

    public static LocalTime toLocalTime(String timeAsString){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
        return LocalTime.parse(timeAsString,formatter);
    }

    public static LocalDate getArrivalDate(int trainNo, String stn,LocalDate startDate) throws TimeTableNotFoundException {

        TimeTableEnquiryResponse timeTableEnquiryResponse;
        timeTableEnquiryResponse = timeTableService.getTimeTable(trainNo);

        List<Timing> trainTimings;
        trainTimings = timeTableEnquiryResponse.getTrainTimings();

        long minutes = 0;

        LocalTime prevDeptTime = toLocalTime(trainTimings.get(0).getDeptTime());

        LocalTime arrivalTime, departureTime;

        Duration between1,between2;

        LocalDate arrivalDate = startDate;

        for(Timing timing:trainTimings){

            arrivalTime = toLocalTime(timing.getArrvTime());
            between1 = Duration.between(prevDeptTime,arrivalTime);

            minutes = between1.toMinutes();

            if( minutes < 0){

                arrivalDate = arrivalDate.plusDays(1);
            }


            if(timing.getStation().equals(stn))
                break;


            departureTime = toLocalTime(timing.getDeptTime());
            between2 = Duration.between(arrivalTime,departureTime);

            minutes = between2.toMinutes();

            if( minutes < 0){

                arrivalDate = arrivalDate.plusDays(1);
            }

            prevDeptTime = departureTime;
        }

        return arrivalDate;
    }

    public static Optional<Boolean> toOptional(boolean b){

        Optional asOptional;

        if(!b)
            asOptional = Optional.empty();

        asOptional = Optional.of(b);

        return asOptional;
    }
}
