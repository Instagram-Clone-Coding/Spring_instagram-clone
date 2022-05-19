package cloneproject.Instagram.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.domain.member.entity.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LikeMembersDto {

	private MemberDto member;
	private boolean isFollowing;
	private boolean isFollower;
	private boolean hasStory;

	@QueryProjection
	public LikeMembersDto(Member member, boolean isFollowing, boolean isFollower) {
		this.member = new MemberDto(member);
		this.isFollowing = isFollowing;
		this.isFollower = isFollower;
		this.hasStory = false;
	}

}
