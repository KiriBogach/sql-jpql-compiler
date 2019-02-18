package auto;
// Generated 18-feb-2019 16:40:31 by Hibernate Tools 5.1.10.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JoinedVehicle generated by hbm2java
 */
@Entity
@Table(name = "joined_vehicle", catalog = "jpa")
public class JoinedVehicle implements java.io.Serializable {

	private Integer idVehicle;
	private String manufacturer;

	public JoinedVehicle() {
	}

	public JoinedVehicle(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "idVehicle", unique = true, nullable = false)
	public Integer getIdVehicle() {
		return this.idVehicle;
	}

	public void setIdVehicle(Integer idVehicle) {
		this.idVehicle = idVehicle;
	}

	@Column(name = "manufacturer")
	public String getManufacturer() {
		return this.manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

}
