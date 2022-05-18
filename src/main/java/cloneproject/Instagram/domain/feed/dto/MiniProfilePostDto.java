package cloneproject.Instagram.domain.feed.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
