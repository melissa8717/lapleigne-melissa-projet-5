package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    
    public void calculateFare(Ticket ticket, boolean discount){
        if( (ticket.getOutTime().getTime() == 0) || (ticket.getOutTime().getTime() < (ticket.getInTime().getTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        long intHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();
        long durationSecond = ((outHour - intHour )/ 1000 );
        float durationMinute = (float)durationSecond/ 60;
        float duration = durationMinute / 60;
        //double discountPrice =  0.05 * ticket.getPrice();

        if(duration < 0.5){
          
           ticket.setPrice(0);

        }else{
            if(discount == true ){
                switch (ticket.getParkingSpot().getParkingType()){
                    case CAR: {
                        ticket.setPrice(0.95*(duration * Fare.CAR_RATE_PER_HOUR));
                        break;
                    }
                    case BIKE: {
                        ticket.setPrice(0.95*(duration * Fare.BIKE_RATE_PER_HOUR));
                        break;
                    }
                    default: throw new IllegalArgumentException("Unkown Parking Type");
                }
            }else{
               switch (ticket.getParkingSpot().getParkingType()){
                    case CAR: {
                        ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                        break;
                    }
                    case BIKE: {
                        ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                        break;
                    }
                    default: throw new IllegalArgumentException("Unkown Parking Type");
                } 
            }
        }
    }

    public void calculateFare(Ticket ticket){
        this.calculateFare(ticket, false);
    }

}