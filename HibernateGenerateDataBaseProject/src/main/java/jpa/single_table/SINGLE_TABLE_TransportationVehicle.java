package jpa.single_table;

import javax.persistence.Entity;

@Entity
public class SINGLE_TABLE_TransportationVehicle extends SINGLE_TABLE_Vehicle {
	
	public SINGLE_TABLE_TransportationVehicle() {
	}

	private int loadCapacity;

	public int getLoadCapacity() {
		return loadCapacity;
	}

	public void setLoadCapacity(int loadCapacity) {
		this.loadCapacity = loadCapacity;
	}

}