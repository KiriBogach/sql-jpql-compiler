package jpa.single_table;

import javax.persistence.Entity;

@Entity
public class SINGLE_TABLE_PassengerVehicle extends SINGLE_TABLE_Vehicle {
	
	public SINGLE_TABLE_PassengerVehicle() {
	}
 
	private int noOfpassengers;
 
	public int getNoOfpassengers() {
		return noOfpassengers;
	}
 
	public void setNoOfpassengers(int noOfpassengers) {
		this.noOfpassengers = noOfpassengers;
	}
 
}