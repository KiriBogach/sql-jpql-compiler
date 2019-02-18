package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the joined_vehicle database table.
 * 
 */
@Entity
@Table(name="joined_vehicle")
@NamedQuery(name="JoinedVehicle.findAll", query="SELECT j FROM JoinedVehicle j")
public class JoinedVehicle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int idvehicle;

	private String dtype;

	private String manufacturer;

	public JoinedVehicle() {
	}

	public int getIdvehicle() {
		return this.idvehicle;
	}

	public void setIdvehicle(int idvehicle) {
		this.idvehicle = idvehicle;
	}

	public String getDtype() {
		return this.dtype;
	}

	public void setDtype(String dtype) {
		this.dtype = dtype;
	}

	public String getManufacturer() {
		return this.manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

}