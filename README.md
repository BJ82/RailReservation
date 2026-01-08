 # (PEARS) 
   
  ## PASSENGER ENQUIRY AND RESERVATION SYSTEM

  ### INTRODUCTION

A Webservice which provides rail passenger booking & enquiry services
through REST APIs. Developed & Designed using Java, Spring Boot, Postgres DB 
as a monolith application. Primarily the service is composed of following modules:
   
1) <ins>***Booking***</ins>

  This handles ticket booking & cancellation.
  Additionally it allows admins to open ticket
  booking for a train. Along with booking & cancellation
  it provides booking open status for a train.

   **How Ticket Booking Works?**
  
  Firstly the booking open status is checked
  in Booking Open table.If booking is opened
  then a seat number request is made to SeatNoService
  Which generates a seat number.

  To generate seat number, SeatNoService calculates
  the available seats as below
  
  int seatsAvailable = totalNoOfSeats - seatInfoTrackerService.getLastAllocatedSeatNo(request);

  Note that seat nos are generated in consecutive order.
  So lstAllotedSeatNum is incremented by 1 and this new
  Number is a seat no which can be alloted.

  Set<Integer> seatNums;
  
  for(int i=1;i<=seatsAvailable;i++){

            seatNums.add(lstAllotedSeatNum.addAndGet(1));
  }
  
  Now getSeatNosBefore is called to get seat nos
  Which would have their journey end before our journey starts.

  Similary getSeatNosAfter is called to get seat nos
  Which would begin their journey after our journey ends.

  These two types of seat numbers is added to the primary
  seat numbers(seatNums) and returned.Now For each passenger
  in the booking request, seat number is alloted from the
  above retrieved seat numbers and a corresponding entry
  is made into the Booking table.

   **How Ticket Cancellation Works?**
  
  This is handled by BookingService.cancelBooking.
  If the booking to be cancelled has waiting status
  Then corresponding entry in Booking table is deleted.
  If booking to cancel has confirmed status then waiting
  list is retrieved for corresponding train no, class and
  dates.This list is sorted(Ascending) as per the pnr no.
  
  Now all bookings are retrieved for the seatNo whose booking
  Is to be cancelled.Note that a seat no can be shared by multiple
  Bookings if their routes dont conflict.This list is filtered to
  contain bookings which are not to be deleted.
  
  The waiting list is then checked against the above bookings(have same seat no)
  for route compatibility.If their routes dont conflict then that booking is changed
  From waiting to confirm status and the corresponding entry in Booking
  Table is deleted.

  Booking bookingToConfirm = null;

   for(Booking bookingWithStatusWait:waitingList){

       if(allBookings.isEmpty() || routeInfoService.isRouteCompatible(bookingWithStatusWait,allBookings)){
             bookingToConfirm = bookingWithStatusWait;
                 break;
       }
   }

   if(bookingToConfirm != null){

      bookingInfoTrackerService.changeBookingToConfirm(bookingToConfirm.getPnr(),seatNo);
      logger.info("Changed Booking Status For PnrNo:{},From Waiting To Confirmed",bookingToConfirm.getPnr());
   }
   
Booking Module interacts with below DB tables:

    1. Booking
    2. Booking Open
    3. Seat Count
    4. Seat No Tracker

2) <ins>***Enquiry***</ins>

Processes various types of enquiries
such as train enquiry(by stations/by train no),
seat enquiry & pnr enquiry.

**Train Enquiry by stations:** Returns all 
available trains that serve the given stations.
Handled by EnquiryService.trainEnquiry

**Train Enquiry by trainNo:** Returns train details
for given train No.
Handled by EnquiryService.trainEnquiry

**Seat Enquiry:** Returns seat availability(If Any)
for given seat enquiry request.
Handled by EnquiryService.seatEnquiry

**Pnr Enquiry:** Returns booking status(Confirmed/Waiting)
for given pnrNo.
Handled by EnquiryService.pnrEnquiry

This Module Interacts with following modules 

    1. Booking
    2. Train Management
    3. Route

3) <ins>***Train-Management***</ins>
     
  Mostly used by admin to perform tasks
  like add new train, retrieve all trains,
  add new timetable, retrieve train time table.

  Below services, part of Train-Management Module
  process & handle the above mentioned tasks

  1. TrainService
  2. TrainInfoService
  3. TimeTableService
  4. TrainArrivalDateService

The services interact with these DB tables

  1. Train
  2. Time Table
  3. Timing

4) <ins>***Sign-Up***</ins>

    Handles new user signup(Both admin & user).
   Interacts with only one DB table which is Users.

5) <ins>***Login***<ins>

    Handles user login for existing user.
   Interacts with Spring Security Filter Chain
   To authenticate user & returns a JWT token.

### TECHNOLOGIES USED

1. Java 21
2. Spring Boot 3.5.7
3. Spring Security
4. JJWT For JWT Token
5. Postgres DB

**Pls Note**
**the Project is a Backend Service.**
**Front End is Under Development.**
