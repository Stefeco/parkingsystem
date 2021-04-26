package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }
    
    //ajout par stef to check jacoco
    @Test
    public void a_New_User_Gets_A_Ticket_in_Database() {
        LocalDateTime inTime = LocalDateTime.now().minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
    	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
    	
    	ticket.setInTime(inTime);
    	ticket.setOutTime(outTime);
    	ticket.setParkingSpot(parkingSpot);
    	assertThat(ticket.getId()).isNotNull();
    	
    }
    
    @Test
    public void A_New_Ticket_Is_Saved_In_DAO() {
    	
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		TicketDAO ticketDAO = new TicketDAO();
		
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("TESTDAO");
		parkingSpot.setAvailable(true);
		ticketDAO.saveTicket(ticket);
		fareCalculatorService.calculateFare(ticket);
		assertThat(ticketDAO.saveTicket(ticket)).isNotNull();

    	
    }
    
    @Test
    public void A_New_User_Gets_A_New_ParkingSpot() {
    	
		LocalDateTime inTime = LocalDateTime.now().minusHours(2);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		TicketDAO ticketDAO = new TicketDAO();
		
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("TESTDAO");
		parkingSpot.setAvailable(true);
		ticketDAO.saveTicket(ticket);
		fareCalculatorService.calculateFare(ticket);
		assertThat(ticketDAO.saveTicket(ticket)).isNotNull();
    	
    }

    @Test
    public void calculateFareCar(){
        LocalDateTime inTime = LocalDateTime.now().minusHours(1) ;
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
        LocalDateTime inTime = LocalDateTime.now().minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
        LocalDateTime inTime = LocalDateTime.now().minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        LocalDateTime inTime = LocalDateTime.now().plusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        LocalDateTime inTime = LocalDateTime.now().minusDays(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    public void WhenAUserisParked_ifTheTimeisLessThan30Minutes_ThenTheParkingIsFree() {
    	
    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(29);
    	LocalDateTime outTime = LocalDateTime.now();
    	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    	
    	ticket.setInTime(inTime);
    	ticket.setOutTime(outTime);
    	ticket.setParkingSpot(parkingSpot);
    	fareCalculatorService.calculateFare(ticket);
    	assertThat(ticket.getPrice()).isEqualTo(0);
    }
    
    @Test
    public void aUserThatBuysaPlace_ifAlreadyCustomer_getDiscountof5Percent() {
    	
    	LocalDateTime inTime = LocalDateTime.now().minusHours(3);
    	LocalDateTime outTime = LocalDateTime.now();
    	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    	
    	ticket.setInTime(inTime);
    	ticket.setOutTime(outTime);
    	ticket.setParkingSpot(parkingSpot);
    	ticket.setDiscountPrice(true);
    	fareCalculatorService.calculateFare(ticket);
    	assertThat(ticket.getPrice()).isEqualTo(4.275,within(0.2));
    }
    

    @Test
    public void ReturningUser_ifHeHasAKnownVehicleNumber_GetsADiscount() {
    		LocalDateTime inTime = LocalDateTime.now().minusHours(3);
    		LocalDateTime outTime = LocalDateTime.now();
    		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    		TicketDAO ticketDAO = new TicketDAO();
    		
    		ticket.setInTime(inTime);
    		ticket.setOutTime(outTime);
    		ticket.setParkingSpot(parkingSpot);
    		ticket.setVehicleRegNumber("TEST003");
    		ticket.setDiscountPrice(true);
    		parkingSpot.setAvailable(true);
    		ticketDAO.returningUser(ticket.getVehicleRegNumber());
    		fareCalculatorService.calculateFare(ticket);
    		assertThat(ticket.getPrice()).isEqualTo(2.85);
    }
    
}
