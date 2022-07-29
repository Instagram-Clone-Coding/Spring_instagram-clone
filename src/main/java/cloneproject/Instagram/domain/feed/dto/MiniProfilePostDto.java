package cloneproject.Instagram.domain.feed.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MiniProfilePostDto {

	private Long postId;
	private String postImageUrl;

	@Builder
	@QueryProjection
	public MiniProfilePostDto(Long postId, String postImageUrl) {
		this.postId = postId;
		this.postImageUrl = postImageUrl;
	}

}
