package model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the order_details database table.
 * 
 */
@Entity
@Table(name = "order_details")
@NamedQuery(name = "OrderDetail.findAll", query = "SELECT o FROM OrderDetail o")
public class OrderDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	// bi-directional many-to-one association to Order
	@ManyToOne
	@JoinColumn(name = "OrderID")
	private Order order;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int orderDetailID;

	// bi-directional many-to-one association to Product
	@ManyToOne
	@JoinColumn(name = "ProductID")
	private Product product;

	private int quantity;

	public OrderDetail() {
	}

	public Order getOrder() {
		return this.order;
	}

	public int getOrderDetailID() {
		return this.orderDetailID;
	}

	public Product getProduct() {
		return this.product;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void setOrderDetailID(int orderDetailID) {
		this.orderDetailID = orderDetailID;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "OrderDetail [orderDetailID=" + orderDetailID + ", quantity=" + quantity + ", order=" + order
				+ ", product=" + product + "]";
	}

}