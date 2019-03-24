package database.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the shippers database table.
 * 
 */
@Entity
@Table(name = "shippers")
@NamedQuery(name = "Shipper.findAll", query = "SELECT s FROM Shipper s")
public class Shipper implements Serializable {
	private static final long serialVersionUID = 1L;

	// bi-directional many-to-one association to Order
	@OneToMany(mappedBy = "shipper")
	private List<Order> orders;

	@Column(length = 255)
	private String phone;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int shipperID;

	@Column(length = 255)
	private String shipperName;

	public Shipper() {
	}

	public Order addOrder(Order order) {
		getOrders().add(order);
		order.setShipper(this);

		return order;
	}

	public List<Order> getOrders() {
		return this.orders;
	}

	public String getPhone() {
		return this.phone;
	}

	public int getShipperID() {
		return this.shipperID;
	}

	public String getShipperName() {
		return this.shipperName;
	}

	public Order removeOrder(Order order) {
		getOrders().remove(order);
		order.setShipper(null);

		return order;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setShipperID(int shipperID) {
		this.shipperID = shipperID;
	}

	public void setShipperName(String shipperName) {
		this.shipperName = shipperName;
	}

	@Override
	public String toString() {
		return "Shipper [shipperID=" + shipperID + ", phone=" + phone + ", shipperName=" + shipperName + ", orders="
				+ orders + "]";
	}

}