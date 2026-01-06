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

It interacts with these DB tables:

    1. Booking
    2. Booking Open
    3. Seat Count
    4. Seat No Tracker

2) <ins>***Enquiry***</ins>

Processes various types of enquiries
such as train enquiry(by stations/by train no),
seat enquiry & pnr enquiry.

Interacts with following modules 

    1. Booking
    2. Train Management
    3. Route

To process various types of enquiry request.


3) <ins>***Train-Management***</ins>
     
  Mostly used by admin to perform tasks
  like adding new trains, retrieving all trains
  And adding new timetable.

  Interacts with these DB tables

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
