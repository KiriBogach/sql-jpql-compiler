package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the post_tag database table.
 * 
 */
@Entity
@Table(name="post_tag")
@NamedQuery(name="PostTag.findAll", query="SELECT p FROM PostTag p")
public class PostTag implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PostTagPK id;

	public PostTag() {
	}

	public PostTagPK getId() {
		return this.id;
	}

	public void setId(PostTagPK id) {
		this.id = id;
	}

}