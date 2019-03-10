package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the products database table.
 * 
 */
@Entity
@Table(name = "products")
@NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	// bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name = "CategoryID")
	private Category category;

	// bi-directional many-to-one association to OrderDetail
	@OneToMany(mappedBy = "product")
	private List<OrderDetail> orderDetails;

	private double price;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int productID;

	@Column(length = 255)
	private String productName;

	// bi-directional many-to-one association to Supplier
	@ManyToOne
	@JoinColumn(name = "SupplierID")
	private Supplier supplier;

	@Column(length = 255)
	private String unit;

	public Product() {
	}

	public OrderDetail addOrderDetail(OrderDetail orderDetail) {
		getOrderDetails().add(orderDetail);
		orderDetail.setProduct(this);

		return orderDetail;
	}

	public Category getCategory() {
		return this.category;
	}

	public List<OrderDetail> getOrderDetails() {
		return this.orderDetails;
	}

	public double getPrice() {
		return this.price;
	}

	public int getProductID() {
		return this.productID;
	}

	public String getProductName() {
		return this.productName;
	}

	public Supplier getSupplier() {
		return this.supplier;
	}

	public String getUnit() {
		return this.unit;
	}

	public OrderDetail removeOrderDetail(OrderDetail orderDetail) {
		getOrderDetails().remove(orderDetail);
		orderDetail.setProduct(null);

		return orderDetail;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "Product [productID=" + productID + ", price=" + price + ", productName=" + productName + ", unit="
				+ unit + ", orderDetails=" + orderDetails + ", category=" + category + ", supplier=" + supplier + "]";
	}

}