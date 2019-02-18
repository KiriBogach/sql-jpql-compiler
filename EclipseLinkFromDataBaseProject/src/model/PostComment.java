package model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


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
	private String id;

	@Column(name="POST_ID")
	private BigInteger postId;

	private String review;

	private int version;

	public PostComment() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigInteger getPostId() {
		return this.postId;
	}

	public void setPostId(BigInteger postId) {
		this.postId = postId;
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

}