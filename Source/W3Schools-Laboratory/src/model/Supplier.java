package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the suppliers database table.
 * 
 */
@Entity
@Table(name = "suppliers")
@NamedQuery(name = "Supplier.findAll", query = "SELECT s FROM Supplier s")
public class Supplier implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(length = 255)
	private String address;

	@Column(length = 255)
	private String city;

	@Column(length = 255)
	private String contactName;

	@Column(length = 255)
	private String country;

	@Column(length = 255)
	private String phone;

	@Column(length = 255)
	private String postalCode;

	// bi-directional many-to-one association to Product
	@OneToMany(mappedBy = "supplier")
	private List<Product> products;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int supplierID;

	@Column(length = 255)
	private String supplierName;

	public Supplier() {
	}

	public Product addProduct(Product product) {
		getProducts().add(product);
		product.setSupplier(this);

		return product;
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

	public String getPhone() {
		return this.phone;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public List<Product> getProducts() {
		return this.products;
	}

	public int getSupplierID() {
		return this.supplierID;
	}

	public String getSupplierName() {
		return this.supplierName;
	}

	public Product removeProduct(Product product) {
		getProducts().remove(product);
		product.setSupplier(null);

		return product;
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

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public void setSupplierID(int supplierID) {
		this.supplierID = supplierID;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	@Override
	public String toString() {
		return "Supplier [supplierID=" + supplierID + ", address=" + address + ", city=" + city + ", contactName="
				+ contactName + ", country=" + country + ", phone=" + phone + ", postalCode=" + postalCode
				+ ", supplierName=" + supplierName + ", products=" + products + "]";
	}

}