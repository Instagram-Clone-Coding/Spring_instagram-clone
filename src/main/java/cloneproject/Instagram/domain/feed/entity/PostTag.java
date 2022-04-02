package cloneproject.Instagram.domain.feed.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_image_id")
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
