package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the post_details database table.
 * 
 */
@Entity
@Table(name="post_details")
@NamedQuery(name="PostDetail.findAll", query="SELECT p FROM PostDetail p")
public class PostDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column(name="created_by")
	private String createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name="created_on")
	private Date createdOn;

	@Column(name="POST_ID")
	private BigInteger postId;

	private int version;

	public PostDetail() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public BigInteger getPostId() {
		return this.postId;
	}

	public void setPostId(BigInteger postId) {
		this.postId = postId;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}