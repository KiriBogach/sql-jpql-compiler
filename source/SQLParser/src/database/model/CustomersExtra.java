package database.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the customers_extra database table.
 * 
 */
@Entity
@Table(name="customers_extra")
@NamedQuery(name="CustomersExtra.findAll", query="SELECT c FROM CustomersExtra c")
public class CustomersExtra implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int customer_ID;

	@Column(name="last_message")
	private String lastMessage;

	@Column(name="mobile_phone")
	private String mobilePhone;

	//bi-directional one-to-one association to Customer
	@OneToOne
	@JoinColumn(name="customer_ID")
	private Customer customer;

	public CustomersExtra() {
	}

	public int getCustomer_ID() {
		return this.customer_ID;
	}

	public void setCustomer_ID(int customer_ID) {
		this.customer_ID = customer_ID;
	}

	public String getLastMessage() {
		return this.lastMessage;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public String getMobilePhone() {
		return this.mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}