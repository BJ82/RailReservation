package com.rail.app.RailReservation.Enquiry.Controller;

import com.rail.app.RailReservation.Enquiry.Service.Enquiry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.rail.app.RailReservation.Enquiry.DTO.TrainEnquiryResponse;
import java.util.List;

@RestController
@RequestMapping("enquiry/")
public class EnquiryController {

    Logger logger = (Logger) LogManager.getLogger(getClass());

    @Autowired
    private Enquiry enquiryService;

    @GetMapping("train")
    public List<TrainEnquiryResponse> trainEnquiryByStation(@RequestParam String src,@RequestParam String dest){

        return enquiryService.trainEnquiry(src,dest);
    }
}
