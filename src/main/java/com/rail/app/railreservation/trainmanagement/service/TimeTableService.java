package com.rail.app.railreservation.trainmanagement.service;

import com.rail.app.railreservation.common.entity.Train;
import com.rail.app.railreservation.common.repository.TrainRepository;
import com.rail.app.railreservation.trainmanagement.dto.TimeTableAddRequest;
import com.rail.app.railreservation.trainmanagement.dto.TimeTableAddResponse;
import com.rail.app.railreservation.trainmanagement.entity.TimeTable;
import com.rail.app.railreservation.trainmanagement.exception.TimeTableAddFailException;
import com.rail.app.railreservation.trainmanagement.exception.TimeTableWithoutTrainException;
import com.rail.app.railreservation.trainmanagement.repository.TimeTableRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class TimeTableService {

    private static final Logger logger = LogManager.getLogger(TimeTableService.class);

    private static final String INSIDE_TIME_TABLE_SERVICE = "Inside Time Table Service...";

    private TimeTableRepository timeTableRepo;

    private TrainRepository trainRepo;

    public TimeTableService(TimeTableRepository timeTableRepo,TrainRepository trainRepo) {
        this.timeTableRepo = timeTableRepo;
        this.trainRepo = trainRepo;
    }

    ModelMapper mapper = new ModelMapper();

    public TimeTableAddResponse addTimeTable(TimeTableAddRequest tmtbladdreq) throws TimeTableWithoutTrainException,TimeTableAddFailException{

        logger.info(INSIDE_TIME_TABLE_SERVICE);
        logger.info("Adding Time Table For TrainNo:{},TrainName:{}",tmtbladdreq.getTrainNo(),tmtbladdreq.getTrainName());

        trainRepo.findByTrainNo(tmtbladdreq.getTrainNo()).orElseThrow(()->new TimeTableWithoutTrainException("Trying To Add Time Table For Train Which Doesn't Exist"));

        TimeTable ttbl = mapper.map(tmtbladdreq,TimeTable.class);
        ttbl = timeTableRepo.save(ttbl);

        if(ttbl.getTimeTableID() == null){
            throw new TimeTableAddFailException("Failed To Add Time Table For Train No:"+tmtbladdreq.getTrainNo()+", TrainName:"+tmtbladdreq.getTrainName());
        }

        return mapper.map(ttbl,TimeTableAddResponse.class);

    }


}
