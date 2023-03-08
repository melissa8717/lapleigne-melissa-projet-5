package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public static void disableWarning() {
    System.err.close();
    System.setErr(System.out);
    System.setProperty("com.google.inject.internal.cglib.$experimental_asm7", "true");
    }
    
    public void calculateFare(Ticket ticket, boolean discount){
        if( (ticket.getOutTime().getTime() == 0) || (ticket.getOutTime().getTime() < (ticket.getInTime().getTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        discount = true;
        long intHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();
        long durationSecond = ((outHour - intHour )/ 1000 );
        float durationMinute = (float)durationSecond/ 60;
        float duration = durationMinute / 60;
        double discountPrice = 5 / 100;

        if(duration < 0.5){
          
           ticket.setPrice(0);

        }else{
            if(discount == true ){
                switch (ticket.getParkingSpot().getParkingType()){
                    case CAR: {
                        ticket.setPrice((duration * Fare.CAR_RATE_PER_HOUR)- discountPrice);
                        break;
                    }
                    case BIKE: {
                        ticket.setPrice((duration * Fare.BIKE_RATE_PER_HOUR)- discountPrice);
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

}