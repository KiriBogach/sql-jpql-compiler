package database.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the employees database table.
 * 
 */
@Entity
@Table(name = "employees")
@NamedQuery(name = "Employee.findAll", query = "SELECT e FROM Employee e")
public class Employee implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.DATE)
	private Date birthDate;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int employeeID;

	@Column(length = 255)
	private String firstName;

	@Column(length = 255)
	private String lastName;

	private String notes;

	// bi-directional many-to-one association to Order
	@OneToMany(mappedBy = "employee")
	private List<Order> orders;

	@Column(length = 255)
	private String photo;

	public Employee() {
	}

	public Order addOrder(Order order) {
		getOrders().add(order);
		order.setEmployee(this);

		return order;
	}

	public Date getBirthDate() {
		return this.birthDate;
	}

	public int getEmployeeID() {
		return this.employeeID;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getNotes() {
		return this.notes;
	}

	public List<Order> getOrders() {
		return this.orders;
	}

	public String getPhoto() {
		return this.photo;
	}

	public Order removeOrder(Order order) {
		getOrders().remove(order);
		order.setEmployee(null);

		return order;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	@Override
	public String toString() {
		return "Employee [employeeID=" + employeeID + ", birthDate=" + birthDate + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", notes=" + notes + ", photo=" + photo + ", orders=" + orders + "]";
	}

}