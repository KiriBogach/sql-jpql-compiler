package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the customers database table.
 * 
 */
@Entity
@Table(name = "customers")
@NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c")
public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(length = 255)
	private String address;

	@Column(length = 255)
	private String city;

	@Column(length = 255)
	private String contactName;

	@Column(length = 255)
	private String country;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int customerID;

	@Column(length = 255)
	private String customerName;

	// bi-directional many-to-one association to Order
	@OneToMany(mappedBy = "customer")
	private List<Order> orders;

	@Column(length = 255)
	private String postalCode;

	public Customer() {
	}

	public Order addOrder(Order order) {
		getOrders().add(order);
		order.setCustomer(this);

		return order;
	}

	public String getAddress() {
		return this.address;
	}

	public String getCity() {
		return this.city;
	}

	public String getContactName() {
		return this.contactName;
	}

	public String getCountry() {
		return this.country;
	}

	public int getCustomerID() {
		return this.customerID;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public List<Order> getOrders() {
		return this.orders;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public Order removeOrder(Order order) {
		getOrders().remove(order);
		order.setCustomer(null);

		return order;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Override
	public String toString() {
		return "Customer [customerID=" + customerID + ", address=" + address + ", city=" + city + ", contactName="
				+ contactName + ", country=" + country + ", customerName=" + customerName + ", postalCode=" + postalCode
				+ ", orders=" + orders + "]";
	}

}