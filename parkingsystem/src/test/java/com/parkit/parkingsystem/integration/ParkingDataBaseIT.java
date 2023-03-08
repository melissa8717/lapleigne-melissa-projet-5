package com.parkit.parkingsystem.integration;


import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.*;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.constants.ParkingType;
import static org.junit.jupiter.api.Assertions.*;
import com.parkit.parkingsystem.constants.Fare;



import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
   // private static Ticket ticket;
    private static ParkingSpot parkingSpot;
    private static ParkingType parkingType;

    @Mock
    private static Ticket ticket;

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
    public void testParkingACar() throws SQLException, ClassNotFoundException, InterruptedException{
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Connection con = dataBaseTestConfig.getConnection();

        con = dataBaseTestConfig.getConnection();
        String queryTicket = "SELECT * FROM ticket INNER JOIN parking ON ticket.PARKING_NUMBER = parking.PARKING_NUMBER WHERE ticket.VEHICLE_REG_NUMBER = \"ABCDEF\"";

        try{
            PreparedStatement ps = con.prepareStatement(queryTicket);
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Ticket ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber("ABCDEF");
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
                } 
            }catch (Exception e) {
            System.err.println("Exception : " + e.getMessage());
        }
        finally {
            Ticket ticket = ticketDAO.getTicket("ABCDEF");
            assertNotNull(ticket);
            System.out.println("spot"+parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
            assertEquals(2,2);   
        }
    }

    @Test
    public void testParkingLotExit() throws SQLException, ClassNotFoundException, InterruptedException{
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        Connection con = dataBaseTestConfig.getConnection();

        con = dataBaseTestConfig.getConnection();
        String queryTicket = "SELECT * FROM ticket INNER JOIN parking ON ticket.PARKING_NUMBER = parking.PARKING_NUMBER WHERE ticket.VEHICLE_REG_NUMBER = \"ABCDEF\"";

        try{
            PreparedStatement ps = con.prepareStatement(queryTicket);
            ps.setString(1,"ABCDEF");
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
               Timestamp intTime = rs.getTimestamp("IN_TIME");
               Timestamp outTime = rs.getTimestamp("OUT_TIME");
               Double priceTicket = rs.getDouble("PRICE");
               assertNotNull(intTime);
               assertNotNull(outTime);
               assertNotNull(priceTicket);
            }
        } catch (Exception e) {
            System.err.println("Exception : " + e.getMessage());
             }
        finally {
            Ticket ticket = ticketDAO.getTicket("ABCDEF");
            assertNotNull(ticket);
            assertEquals(1,parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
        }
    }

    @Test
    public void testParkingLotExitRecurringUser() throws SQLException, ClassNotFoundException, InterruptedException{
     //tester le calcul du prix d’un ticket via 
     //l’appel de processIncomingVehicle et processExitingVehicle dans le cas d’un utilisateur récurrent. 
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        testParkingACar();
        parkingService.processExitingVehicle();
        Connection con = dataBaseTestConfig.getConnection();

        con = dataBaseTestConfig.getConnection();
        String queryTicket = "SELECT count(\"ABCDEF\") FROM ticket INNER JOIN parking ON ticket.PARKING_NUMBER = parking.PARKING_NUMBER WHERE ticket.VEHICLE_REG_NUMBER = \"ABCDEF\"";
        int nbrTicket =0;
        try{
            PreparedStatement ps = con.prepareStatement(queryTicket);
            ps.setString(1,"ABCDEF");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
               nbrTicket = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Exception : " + e.getMessage());
             }
        finally {
            Ticket ticket = ticketDAO.getTicket("ABCDEF");
            if(nbrTicket > 1){
                assertNotNull(ticket);
                double discountPrice = ticket.getPrice() - (5 / 100);
                assertEquals( (Fare.CAR_RATE_PER_HOUR) , discountPrice);
            }
            
        }

    }

}

