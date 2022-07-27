package cloneproject.Instagram.domain.feed.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

import cloneproject.Instagram.domain.feed.vo.Tag;

@Getter
public class PostTagDto {

	@JsonIgnore
	private Long postImageId;
	private Long id;
	private Tag tag;

	@QueryProjection
	public PostTagDto(Long postImageId, Long id, Tag tag) {
		this.postImageId = postImageId;
		this.id = id;
		this.tag = tag;
	}

}
