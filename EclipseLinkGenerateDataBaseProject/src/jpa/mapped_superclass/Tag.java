package jpa.mapped_superclass;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "Tag")
@Table(name = "tag")
public class Tag extends BaseEntity {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}