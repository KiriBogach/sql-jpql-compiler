package jpa.table_per_class;

import javax.persistence.Entity;

@Entity
public class TABLE_PER_CLASS_TransportationVehicle extends TABLE_PER_CLASS_Vehicle {
	
	public TABLE_PER_CLASS_TransportationVehicle() {
	}

	private int loadCapacity;

	public int getLoadCapacity() {
		return loadCapacity;
	}

	public void setLoadCapacity(int loadCapacity) {
		this.loadCapacity = loadCapacity;
	}

}