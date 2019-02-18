package jpa.joined;

import javax.persistence.Entity;

@Entity
public class JOINED_TransportationVehicle extends JOINED_Vehicle {
	
	public JOINED_TransportationVehicle() {
	}

	private int loadCapacity;

	public int getLoadCapacity() {
		return loadCapacity;
	}

	public void setLoadCapacity(int loadCapacity) {
		this.loadCapacity = loadCapacity;
	}

}