package cloneproject.Instagram.domain.feed.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLikeDto {

	private Long postId;
	private String username;

	@QueryProjection
	public PostLikeDto(Long postId, String username) {
		this.postId = postId;
		this.username = username;
	}

}
