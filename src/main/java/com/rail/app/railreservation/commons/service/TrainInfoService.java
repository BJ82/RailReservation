package com.rail.app.railreservation.commons.service;

import com.rail.app.railreservation.commons.entity.Train;
import com.rail.app.railreservation.commons.repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainInfoService {

    private final TrainRepository trainRepo;

    public TrainInfoService(TrainRepository trainRepo) {
        this.trainRepo = trainRepo;
    }

    public  Optional<Train> getByTrainNo(int trainNo){

        return trainRepo.findByTrainNo(trainNo);

    }

    public Optional<Train> getByTrainName(String trainName){

        return trainRepo.findByTrainName(trainName);

    }

    public List<Train> getByRouteIds(List<Integer> routeIDs){

        return trainRepo.findByRouteIdIn(routeIDs);
    }

    public List<Train> getAllTrains(){
        return trainRepo.findAll();
    }

    public void addTrain(Train train){
        trainRepo.save(train);
    }
}
