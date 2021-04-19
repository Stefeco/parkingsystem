package com.parkit.parkingsystem.constants;

public class DBConstants {
	
	//stef 23/3 added a query to find vehicle_reg_number
	public static final String RETURNING_USER = "select exists(select * from ticket t where t.vehicle_reg_number = ?)";

    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, RETURNING_USER, IN_TIME, OUT_TIME, ISDISCOUNTPRICE) values(?,?,?,?,?,?,?)";
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.RETURNING_USER, t.IN_TIME, t.OUT_TIME, p.TYPE, t.ISDISCOUNTPRICE from ticket t, parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME desc limit 1";
    
    public static final String TestRequestIT = "update ticket set IN_TIME =? where ID =? ";

}
