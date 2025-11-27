package com.rail.app.railreservation.enquiry.service;

import com.rail.app.railreservation.booking.dto.BookingRequest;
import com.rail.app.railreservation.booking.service.BookingService;
import com.rail.app.railreservation.common.entity.Train;
import com.rail.app.railreservation.common.repository.RouteRepository;
import com.rail.app.railreservation.common.repository.TrainRepository;
import com.rail.app.railreservation.enquiry.dto.SeatEnquiryRequest;
import com.rail.app.railreservation.enquiry.dto.SeatEnquiryResponse;
import com.rail.app.railreservation.enquiry.dto.TrainEnquiryResponse;
import com.rail.app.railreservation.enquiry.entity.Route;
import com.rail.app.railreservation.enquiry.exception.InvalidSeatEnquiryException;
import com.rail.app.railreservation.enquiry.exception.RouteNotFoundException;
import com.rail.app.railreservation.enquiry.exception.TrainNotFoundException;
import com.rail.app.railreservation.trainmanagement.dto.TimeTableEnquiryResponse;
import com.rail.app.railreservation.trainmanagement.entity.Timing;
import com.rail.app.railreservation.trainmanagement.exception.TimeTableNotFoundException;
import com.rail.app.railreservation.trainmanagement.service.TimeTableService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnquiryService {

    private static final Logger logger = LogManager.getLogger(EnquiryService.class);

    private static final String INSIDE_ENQUIRY_SERVICE = "Inside EnquiryService Service...";

    private final RouteRepository routeRepo;
    private final TrainRepository trainRepo;

    private final BookingService bookingService;

    private final TimeTableService timeTableService;

    ModelMapper mapper = new ModelMapper();

    public EnquiryService(RouteRepository routeRepo, TrainRepository trainRepo,
                          BookingService bookingService, TimeTableService timeTableService) {
        this.routeRepo = routeRepo;
        this.trainRepo = trainRepo;
        this.bookingService = bookingService;
        this.timeTableService = timeTableService;
    }


    /*public TrainEnquiryResponse trainEnquiry(int trainNo){

    }*/

    public List<TrainEnquiryResponse> trainEnquiry(String src,String dest) throws TrainNotFoundException {

        logger.info(INSIDE_ENQUIRY_SERVICE);
        logger.info("Searching for trains running between {} and {}",src,dest);


        // Step1: Obtain route ID for given source and destination
        Integer routeID = null;

        if (getRouteId(src, dest).isPresent())
            routeID = getRouteId(src, dest).get();

        logger.info("Step1: Obtained routeID:{} for source:{} and destination:{}",routeID,src,dest);


        //Step2: Obtain routes which contain routeID as subroute

        List<Integer> parentRouteIds;
        parentRouteIds = new ArrayList<>();
        parentRouteIds.addAll(getParentRoutes(src,dest));

        logger.info("parentRouteIds size:{}",parentRouteIds.size());

        logger.info("Step2: Obtained parent routes which have routeID:{} as subroute",routeID);

        List<TrainEnquiryResponse> trainEnquiryResponses = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        //Step3: Obtain trains that are running on parentRouteIds
        List<Train> availableTrains = trainRepo.findByRouteIdIn(parentRouteIds);

        if(availableTrains.isEmpty())
            throw new TrainNotFoundException("No Train Found Between Stations "+src+" And "+dest,src,dest);

        logger.info("Number Of Available Trains:{}",availableTrains.size());
        availableTrains.forEach(

                                    (train)-> {
                                                TrainEnquiryResponse trainEnquiryResponse = modelMapper.map(train, TrainEnquiryResponse.class);
                                                trainEnquiryResponse.setSrc(src);
                                                trainEnquiryResponse.setDest(dest);
                                                trainEnquiryResponses.add(trainEnquiryResponse);
                                             }
                                );

        logger.info("Step3: Obtained trains that are running on parentRouteIds");

        return trainEnquiryResponses;
    }

    public TrainEnquiryResponse trainEnquiry(Integer trainNo) throws TrainNotFoundException, RouteNotFoundException {

        logger.info(INSIDE_ENQUIRY_SERVICE);
        logger.info("Searching For Train With TrainNo:{}", trainNo);


        Train trn = trainRepo.findByTrainNo(trainNo).
                orElseThrow(() -> new TrainNotFoundException("Train Not Found For TrainNo: " + trainNo, trainNo));


        Integer routeID = trn.getRouteId();

        Route route= routeRepo.findByRouteID(routeID).orElseThrow(()->new RouteNotFoundException("Route Not Found For RouteID: "+routeID,routeID));
        List<String> stations = route.getStations();

        TrainEnquiryResponse trainEnquiryResponse = new ModelMapper().map(trn, TrainEnquiryResponse.class);
        trainEnquiryResponse.setSrc(stations.getFirst());
        trainEnquiryResponse.setDest(stations.getLast());

        return trainEnquiryResponse;
    }


    private Optional<Integer> getRouteId(String src, String dest){

        List<Route> routes = routeRepo.findBySrcAndDestn(src, dest);

        return routes.stream().filter(r->{   boolean isTrue = false;
            if(r.getStations().get(0).equals(src)){
                if(r.getStations().get(r.getStations().size()-1).equals(dest))
                    isTrue = true;
            }
            return isTrue;
        }).map(r->r.getRouteID()).findFirst();
    }

    private List<Integer> getParentRoutes(String src, String dest){

        List<Route> routes = routeRepo.findBySrcAndDestn(src, dest);

        return routes.stream().filter(r->{   boolean isTrue = false;
            if(r.getStations().contains(src)){
                if(r.getStations().contains(dest))
                    isTrue = true;
            }
            return isTrue;
        }).map(r->r.getRouteID()).collect(Collectors.toList());

    }

    public SeatEnquiryResponse seatEnquiry(SeatEnquiryRequest seatEnquiryRequest) throws InvalidSeatEnquiryException, TimeTableNotFoundException {

        logger.info(INSIDE_ENQUIRY_SERVICE);
        logger.info("Searching Available Seats In TrainNo:{}",seatEnquiryRequest.getTrainNo());


        String src = seatEnquiryRequest.getFrom();
        String dest = seatEnquiryRequest.getTo();

        List<Integer> parentRouteIds;
        parentRouteIds = new ArrayList<>(getParentRoutes(src, dest));

        List<Train> availableTrains = trainRepo.findByRouteIdIn(parentRouteIds);

        if(availableTrains.isEmpty())
            throw new InvalidSeatEnquiryException("Invalid Seat Enquiry Because ",
                                                  new TrainNotFoundException("No Train Found Between Stations "+src+" And "+dest,src,dest));

       LocalDate startDate = toLocalDate(seatEnquiryRequest.getStartDt());
       LocalDate dateOfArrival =  getArrivalDate(seatEnquiryRequest.getTrainNo(),src,startDate);
       LocalDate dateOfJourney = toLocalDate(seatEnquiryRequest.getDoj());

       if(!dateOfArrival.equals(dateOfJourney))
           throw new InvalidSeatEnquiryException("Invalid Seat Enquiry Because ",
                   new TrainNotFoundException("No Train Found For Date Of Journey: "+dateOfJourney.toString()));


        BookingRequest bookingRequest = mapper.map(seatEnquiryRequest,BookingRequest.class);

        SeatEnquiryResponse seatEnquiryResponse = mapper.map(seatEnquiryRequest,SeatEnquiryResponse.class);

        int seatsAvailable;
        seatsAvailable = bookingService.getSeatNumbers(bookingRequest).size();
        seatEnquiryResponse.setSeatsAvailable(seatsAvailable);

        return seatEnquiryResponse;
    }

    private LocalDate getArrivalDate(int trainNo, String stn,LocalDate startDate) throws TimeTableNotFoundException {

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

    private LocalTime toLocalTime(String timeAsString){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
        return LocalTime.parse(timeAsString,formatter);
    }

    private LocalDate toLocalDate(String dateAsString){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        return LocalDate.parse(dateAsString,formatter);
    }
    /*public TrainEnquiryResponse trainEnquiry(String trainName){

    }

    public PnrEnquiryResponse pnrEnquiry(int pnrNo){

    }

    public SeatEnquiryResponse seatEnquiry(int trainNo, String doj){

    }*/

}
