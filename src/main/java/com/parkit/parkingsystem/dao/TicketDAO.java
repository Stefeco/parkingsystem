package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    @SuppressWarnings("finally")
	public boolean saveTicket(Ticket ticket){
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, RETURNING_USER, IN_TIME, OUT_TIME, ISDISCOUNTPRICE)
            //ps.setInt(1,ticket.getId());
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setBoolean(4, ticket.isReturningUser());
            ps.setTimestamp(5, Timestamp.valueOf(ticket.getInTime()));
            ps.setTimestamp(6, null); 
            //		(ticket.getOutTime() == null
            //		?null
            //		:Timestamp.valueOf(ticket.getOutTime())));
            ps.setBoolean(7, ticket.isDiscountPrice());
            return ps.execute();
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
            return false;
        }
    }

    @SuppressWarnings("finally")
	public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //PARKING_NUMBER, (max)ID, PRICE, RETURNING_USER, IN_TIME, OUT_TIME, p.TYPE, ISDISCOUNTPRICE)
            ps.setString(1,vehicleRegNumber);
   			
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(7)),false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setReturningUser(rs.getBoolean(4));
                ticket.setInTime(rs.getTimestamp(5).toLocalDateTime());
                ticket.setOutTime((rs.getTimestamp(6) == null)
                	? null
                	: rs.getTimestamp(6).toLocalDateTime());
                ticket.setDiscountPrice(rs.getBoolean(8));//stef not working on ITests because the rs returns a discountPrice = false (wrong ticket).
                //System.out.println(rs.getBoolean(8));
                
                
                
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));//stef -- modified the Timestamp argument with .valueOf method to cast the LocalDateTime
            ps.setInt(3,ticket.getId());
            ps.execute();
            return true;
        }catch (Exception ex){
            logger.error("Error saving ticket info",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }
    
    public boolean returningUser(String vehicleRegNumber) {
    	Connection con = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
    		con = dataBaseConfig.getConnection();
    		ps = con.prepareStatement(DBConstants.RETURNING_USER);
    		ps.setString(1, vehicleRegNumber);
    		rs = ps.executeQuery();
    		
    		if(rs.next()) {
    			return rs.getBoolean(1);
    			}
    	}catch (Exception e) {
    			logger.error("the regNumber is incorrect", e);
    		}finally {
    			dataBaseConfig.closeResultSet(rs);
    			dataBaseConfig.closePreparedStatement(ps);
    			dataBaseConfig.closeConnection(con);
    		}
    	return false;
    	}
    
    /*
     * method used only for the integration tests
     */
    
    public boolean updateTicketITTest(Ticket ticket) {
    	Connection con = null;
    	PreparedStatement ps = null;
    	try {
    		con = dataBaseConfig.getConnection();
    		ps = con.prepareStatement(
    				DBConstants.TestRequestIT);
    		ps.setTimestamp(1, Timestamp.valueOf(ticket.getInTime()));
    		ps.setInt(2, ticket.getId());
    		ps.execute();
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		dataBaseConfig.closeConnection(con);
    		dataBaseConfig.closePreparedStatement(ps);
    	}
    	return false;
    }

}
