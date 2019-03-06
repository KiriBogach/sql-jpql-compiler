package model.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the post_comment database table.
 * 
 */
@Entity
@Table(name="post_comment")
@NamedQuery(name="PostComment.findAll", query="SELECT p FROM PostComment p")
public class PostComment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;

	private String review;

	private int version;

	//bi-directional many-to-one association to Post
	@ManyToOne
	private Post post;

	public PostComment() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReview() {
		return this.review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Post getPost() {
		return this.post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

}