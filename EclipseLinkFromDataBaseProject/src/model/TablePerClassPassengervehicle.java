package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the table_per_class_passengervehicle database table.
 * 
 */
@Entity
@Table(name="table_per_class_passengervehicle")
@NamedQuery(name="TablePerClassPassengervehicle.findAll", query="SELECT t FROM TablePerClassPassengervehicle t")
public class TablePerClassPassengervehicle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int idvehicle;

	private String manufacturer;

	private int noofpassengers;

	public TablePerClassPassengervehicle() {
	}

	public int getIdvehicle() {
		return this.idvehicle;
	}

	public void setIdvehicle(int idvehicle) {
		this.idvehicle = idvehicle;
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

}