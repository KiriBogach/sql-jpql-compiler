package jpa.table_per_class;

import javax.persistence.Entity;

@Entity
public class TABLE_PER_CLASS_PassengerVehicle extends TABLE_PER_CLASS_Vehicle {
	
	public TABLE_PER_CLASS_PassengerVehicle() {
	}
 
	private int noOfpassengers;
 
	public int getNoOfpassengers() {
		return noOfpassengers;
	}
 
	public void setNoOfpassengers(int noOfpassengers) {
		this.noOfpassengers = noOfpassengers;
	}
 
}