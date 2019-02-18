package model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the post_tag database table.
 * 
 */
@Embeddable
public class PostTagPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="post_id")
	private String postId;

	@Column(name="tag_id")
	private String tagId;

	public PostTagPK() {
	}
	public String getPostId() {
		return this.postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getTagId() {
		return this.tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PostTagPK)) {
			return false;
		}
		PostTagPK castOther = (PostTagPK)other;
		return 
			this.postId.equals(castOther.postId)
			&& this.tagId.equals(castOther.tagId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.postId.hashCode();
		hash = hash * prime + this.tagId.hashCode();
		
		return hash;
	}
}