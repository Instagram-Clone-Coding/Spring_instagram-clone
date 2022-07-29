package cloneproject.Instagram.domain.feed.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class PostLikeDto {

	private Long postId;
	private String username;

	@QueryProjection
	public PostLikeDto(Long postId, String username) {
		this.postId = postId;
		this.username = username;
	}

}
