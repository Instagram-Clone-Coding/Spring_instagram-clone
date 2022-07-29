package cloneproject.Instagram.domain.feed.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.querydsl.core.annotations.QueryInit;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.feed.vo.Tag;

@Getter
@Entity
@Table(name = "post_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_tag_id")
	private Long id;

	/**
	 * @QueryInit : to resolve Querydsl's depth limit
	 * @Ref https://github.com/querydsl/querydsl/issues/2129
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_image_id")
	@QueryInit({"*.*", "post.member"})
	private PostImage postImage;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "y", column = @Column(name = "post_tag_y")),
		@AttributeOverride(name = "x", column = @Column(name = "post_tag_x")),
		@AttributeOverride(name = "username", column = @Column(name = "post_tag_username"))
	})
	private Tag tag;

	@Builder
	public PostTag(PostImage postImage, Tag tag) {
		this.postImage = postImage;
		this.tag = tag;
	}

}
