package model.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the single_table_vehicle database table.
 * 
 */
@Entity
@Table(name="single_table_vehicle")
@NamedQuery(name="SingleTableVehicle.findAll", query="SELECT s FROM SingleTableVehicle s")
public class SingleTableVehicle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idvehicle;

	private int loadcapacity;

	private String manufacturer;

	private int noofpassengers;

	@Column(name="VEHICLE_TYPE")
	private String vehicleType;

	public SingleTableVehicle() {
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

	public int getNoofpassengers() {
		return this.noofpassengers;
	}

	public void setNoofpassengers(int noofpassengers) {
		this.noofpassengers = noofpassengers;
	}

	public String getVehicleType() {
		return this.vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

}