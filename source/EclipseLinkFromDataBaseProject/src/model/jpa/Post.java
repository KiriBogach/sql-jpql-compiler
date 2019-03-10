package model.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the post database table.
 * 
 */
@Entity
@NamedQuery(name="Post.findAll", query="SELECT p FROM Post p")
public class Post implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;

	private String title;

	private int version;

	//bi-directional many-to-many association to Tag
	@ManyToMany
	@JoinTable(
		name="post_tag"
		, joinColumns={
			@JoinColumn(name="post_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="tag_id")
			}
		)
	private List<Tag> tags;

	//bi-directional many-to-one association to PostComment
	@OneToMany(mappedBy="post")
	private List<PostComment> postComments;

	//bi-directional many-to-one association to PostDetail
	@OneToMany(mappedBy="post")
	private List<PostDetail> postDetails;

	public Post() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<Tag> getTags() {
		return this.tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<PostComment> getPostComments() {
		return this.postComments;
	}

	public void setPostComments(List<PostComment> postComments) {
		this.postComments = postComments;
	}

	public PostComment addPostComment(PostComment postComment) {
		getPostComments().add(postComment);
		postComment.setPost(this);

		return postComment;
	}

	public PostComment removePostComment(PostComment postComment) {
		getPostComments().remove(postComment);
		postComment.setPost(null);

		return postComment;
	}

	public List<PostDetail> getPostDetails() {
		return this.postDetails;
	}

	public void setPostDetails(List<PostDetail> postDetails) {
		this.postDetails = postDetails;
	}

	public PostDetail addPostDetail(PostDetail postDetail) {
		getPostDetails().add(postDetail);
		postDetail.setPost(this);

		return postDetail;
	}

	public PostDetail removePostDetail(PostDetail postDetail) {
		getPostDetails().remove(postDetail);
		postDetail.setPost(null);

		return postDetail;
	}

}