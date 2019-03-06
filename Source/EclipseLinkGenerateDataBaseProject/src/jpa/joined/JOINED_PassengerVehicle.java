package jpa.joined;

import javax.persistence.Entity;

@Entity
public class JOINED_PassengerVehicle extends JOINED_Vehicle {
	
	public JOINED_PassengerVehicle() {
	}
 
	private int noOfpassengers;
 
	public int getNoOfpassengers() {
		return noOfpassengers;
	}
 
	public void setNoOfpassengers(int noOfpassengers) {
		this.noOfpassengers = noOfpassengers;
	}
 
}