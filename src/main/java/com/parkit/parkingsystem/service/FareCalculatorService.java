package com.parkit.parkingsystem.service;

import java.time.Duration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }


        //Some tests are failing here. Need to check if this logic is correct
        
        long duration = Duration.between(ticket.getOutTime(), ticket.getInTime()).abs().getSeconds();
        if(duration  < Fare.RATE_THIRTY_MINUTES * 3600) {
        	duration = (long)Fare.FREE;
        	System.out.println("as you parked less than 30 minutes you don't have to pay anything!");
        } 
        

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(((double)duration / 3600) /* - (Fare.RATE_THIRTY_MINUTES))*/ 
                		* Fare.CAR_RATE_PER_HOUR);
                calculateDiscount(ticket.getPrice(), ticket);
                break;
            }
            case BIKE: {
                ticket.setPrice(((double)duration / 3600) /* - (Fare.RATE_THIRTY_MINUTES)) */ 
                		* Fare.BIKE_RATE_PER_HOUR);
                calculateDiscount(ticket.getPrice(), ticket);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
    
    public void calculateDiscount(double price, Ticket ticket) {
    	if(ticket.isDiscountPrice()) {
    		double discount = (price / 100) * 5;
    		price = price - discount;
    	}
    	ticket.setPrice(price);
    }
}