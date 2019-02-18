package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the joined_passengervehicle database table.
 * 
 */
@Entity
@Table(name="joined_passengervehicle")
@NamedQuery(name="JoinedPassengervehicle.findAll", query="SELECT j FROM JoinedPassengervehicle j")
public class JoinedPassengervehicle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int idvehicle;

	private int noofpassengers;

	public JoinedPassengervehicle() {
	}

	public int getIdvehicle() {
		return this.idvehicle;
	}

	public void setIdvehicle(int idvehicle) {
		this.idvehicle = idvehicle;
	}

	public int getNoofpassengers() {
		return this.noofpassengers;
	}

	public void setNoofpassengers(int noofpassengers) {
		this.noofpassengers = noofpassengers;
	}

}