package cloneproject.Instagram.domain.feed.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostImageDto {

	@JsonIgnore
	private Long postId;
	private Long id;
	private String postImageUrl;
	private String altText;
	private List<PostTagDto> postTags = new ArrayList<>();

	@QueryProjection
	public PostImageDto(Long postId, Long id, String postImageUrl, String altText) {
		this.postId = postId;
		this.id = id;
		this.postImageUrl = postImageUrl;
		this.altText = altText;
	}

}
