package model.jpa;

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
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idvehicle;

	private int loadcapacity;

	//bi-directional one-to-one association to JoinedVehicle
	@OneToOne
	@JoinColumn(name="IDVEHICLE")
	private JoinedVehicle joinedVehicle;

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

	public JoinedVehicle getJoinedVehicle() {
		return this.joinedVehicle;
	}

	public void setJoinedVehicle(JoinedVehicle joinedVehicle) {
		this.joinedVehicle = joinedVehicle;
	}

}