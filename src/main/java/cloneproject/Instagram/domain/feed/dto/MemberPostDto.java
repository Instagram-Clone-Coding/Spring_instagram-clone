package cloneproject.Instagram.domain.feed.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPostDto {

	private Long postId;
	private PostImageDto postImages;
	private boolean hasManyPosts;
	private int postCommentsCount;
	private int postLikesCount;

	@Builder
	@QueryProjection
	public MemberPostDto(Long postId, boolean hasManyPosts, int postCommentsCount, int postLikesCount) {
		this.postId = postId;
		this.hasManyPosts = hasManyPosts;
		this.postCommentsCount = postCommentsCount;
		this.postLikesCount = postLikesCount;
	}

	public void setPostImages(PostImageDto postImageDto) {
		this.postImages = postImageDto;
	}

}
