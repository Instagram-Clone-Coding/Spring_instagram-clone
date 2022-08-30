package cloneproject.Instagram.domain.hashtag.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

import cloneproject.Instagram.global.vo.Image;

@Getter
public class HashtagProfileResponse {

	private Image image;
	private String name;
	private Long postCount;
	private boolean isFollowing;

	@QueryProjection
	public HashtagProfileResponse(String name, Long postCount, boolean isFollowing) {
		this.name = name;
		this.postCount = postCount;
		this.isFollowing = isFollowing;
	}

	public void setImage(Image image) {
		this.image = image;
	}

}
