package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the categories database table.
 * 
 */
@Entity
@Table(name = "categories")
@NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c")
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int categoryID;

	@Column(length = 255)
	private String categoryName;

	@Column(length = 255)
	private String description;

	// bi-directional many-to-one association to Product
	@OneToMany(mappedBy = "category")
	private List<Product> products;

	public Category() {
	}

	public Product addProduct(Product product) {
		getProducts().add(product);
		product.setCategory(this);

		return product;
	}

	public int getCategoryID() {
		return this.categoryID;
	}

	public String getCategoryName() {
		return this.categoryName;
	}

	public String getDescription() {
		return this.description;
	}

	public List<Product> getProducts() {
		return this.products;
	}

	public Product removeProduct(Product product) {
		getProducts().remove(product);
		product.setCategory(null);

		return product;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "Category [categoryID=" + categoryID + ", categoryName=" + categoryName + ", description=" + description
				+ ", products=" + products + "]";
	}
	
	

}