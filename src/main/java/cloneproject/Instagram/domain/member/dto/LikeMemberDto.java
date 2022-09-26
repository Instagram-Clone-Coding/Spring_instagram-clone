package cloneproject.Instagram.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@NoArgsConstructor
public class LikeMemberDto {

	private MemberDto member;
	private boolean isFollowing;
	private boolean isFollower;

	@QueryProjection
	public LikeMemberDto(Member member, boolean isFollowing, boolean isFollower) {
		this.member = new MemberDto(member);
		this.isFollowing = isFollowing;
		this.isFollower = isFollower;
	}

}
