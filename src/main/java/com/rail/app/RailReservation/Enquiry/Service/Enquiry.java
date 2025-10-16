package com.rail.app.RailReservation.Enquiry.Service;

import com.rail.app.RailReservation.Enquiry.DTO.PnrEnquiryResponse;
import com.rail.app.RailReservation.Enquiry.DTO.SeatEnquiryResponse;
import com.rail.app.RailReservation.Enquiry.DTO.TrainEnquiryResponse;
import org.springframework.stereotype.Service;

@Service
public class Enquiry {

    public TrainEnquiryResponse trainEnquiry(int trainNo){

    }

    public TrainEnquiryResponse trainEnquiry(String src,String dest){

    }

    public TrainEnquiryResponse trainEnquiry(String trainName){

    }

    public PnrEnquiryResponse pnrEnquiry(int pnrNo){

    }

    public SeatEnquiryResponse seatEnquiry(int trainNo, String doj){

    }

    public SeatEnquiryResponse seatEnquiry(int trainName,String doj){

    }
}
