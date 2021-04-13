package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static FareCalculatorService fareCalculatorService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        try {
        	Ticket ticket = ticketDAO.getTicket("ABCDEF");
        	assertThat(ticket).isNotNull();
        	assertNotEquals(1,parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
        	assertEquals(2,parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
        	assertThat(ticket).isNotEqualTo(ticket.isDiscountPrice());
        	ticket.setInTime(LocalDateTime.now().minusHours(2));
        	ticketDAO.updateTicketITTest(ticket);
        	parkingService.processExitingVehicle();
        	
        	//same vehicle comes back
        	parkingService.processIncomingVehicle();
        	Ticket ticket2 = ticketDAO.getTicket("ABCDEF");
        	assertThat(ticket2.isDiscountPrice()).isEqualTo(true);
        	ticket.setOutTime(LocalDateTime.now().plusHours(1));
        	ticketDAO.updateTicketITTest(ticket2);
        	//parkingService.processExitingVehicle();

        	//calcul fare
        	fareCalculatorService.calculateFare(ticket2);//created new static in class attributes
        	ticketDAO.updateTicketITTest(ticket2);
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        finally {
        	
        }
        
        
    }

    
    @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        try {
        	Ticket ticket = ticketDAO.getTicket("ABCDEF");
        	assertThat(ticket.getPrice()).isNotNull();
        	assertThat(ticket.getOutTime()).isNotNull();
        	
        	ParkingSpot parkingSpot = ticket.getParkingSpot();
        	parkingSpot.setAvailable(true);
        	assertThat(parkingSpotDAO.updateParking(parkingSpot)).isTrue();
        	assertThat(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).isEqualTo(1);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}

