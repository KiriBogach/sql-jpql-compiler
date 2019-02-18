package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the table_per_class_transportationvehicle database table.
 * 
 */
@Entity
@Table(name="table_per_class_transportationvehicle")
@NamedQuery(name="TablePerClassTransportationvehicle.findAll", query="SELECT t FROM TablePerClassTransportationvehicle t")
public class TablePerClassTransportationvehicle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int idvehicle;

	private int loadcapacity;

	private String manufacturer;

	public TablePerClassTransportationvehicle() {
	}

	public int getIdvehicle() {
		return this.idvehicle;
	}

	public void setIdvehicle(int idvehicle) {
		this.idvehicle = idvehicle;
	}

	public int getLoadcapacity() {
		return this.loadcapacity;
	}

	public void setLoadcapacity(int loadcapacity) {
		this.loadcapacity = loadcapacity;
	}

	public String getManufacturer() {
		return this.manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

}