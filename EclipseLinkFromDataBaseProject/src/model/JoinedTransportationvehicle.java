package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the joined_transportationvehicle database table.
 * 
 */
@Entity
@Table(name="joined_transportationvehicle")
@NamedQuery(name="JoinedTransportationvehicle.findAll", query="SELECT j FROM JoinedTransportationvehicle j")
public class JoinedTransportationvehicle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int idvehicle;

	private int loadcapacity;

	public JoinedTransportationvehicle() {
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

}