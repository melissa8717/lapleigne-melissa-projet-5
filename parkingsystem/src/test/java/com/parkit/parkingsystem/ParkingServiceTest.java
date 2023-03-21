package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    @Mock
    private static Ticket ticket;
    @Mock
    private static ParkingSpot parkingSpot;
    

    @BeforeEach
    private void setUpPerTest() {
        try {
           // when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            //when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            //when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            //when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest(){
        String vehicleRegNumber = "ABCDEF";
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        parkingService.processExitingVehicle();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        //when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        //when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
         if(ticketDAO.getNbTicket(vehicleRegNumber) > 1){
            parkingSpotDAO.updateParking(parkingSpot); 
        }
        else{
            parkingSpotDAO.updateParking(parkingSpot); 
            }
    }

    @Test
    public void testProcessIncomingVehicle(){
        parkingService.processIncomingVehicle();
    }

    @Test
    public void processExitingVehicleTestUnableUpdate(){
        try{
            if(ticketDAO.updateTicket(ticket) == true){

            }
        }
        catch (Exception e) {
                e.printStackTrace();
                throw  new RuntimeException("Failed to update ticket when exiting");  
        }
    }

    @Test
    public void testGetNextParkingNumberIfAvailable(){
        parkingService.getNextParkingNumberIfAvailable();
        parkingSpot = new ParkingSpot(1,ParkingType.CAR, true);
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound(){
        parkingService.getNextParkingNumberIfAvailable();
        parkingSpot = new ParkingSpot(0,ParkingType.CAR, true);
   
    }
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument(){
        parkingService.getNextParkingNumberIfAvailable();
        parkingSpot = new ParkingSpot(1,null, true); 
    }
    

}
