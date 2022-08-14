package cloneproject.Instagram.domain.search.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.follow.dto.FollowDto;
import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchMemberDto extends SearchDto {

	private MemberDto member;
	private boolean isFollowing;
	private boolean isFollower;
	private List<FollowDto> followingMemberFollow;
	private int followingMemberFollowCount;

	@QueryProjection
	public SearchMemberDto(String dtype, Member member, boolean isFollowing, boolean isFollower) {
		super(dtype);
		this.member = new MemberDto(member);
		this.isFollowing = isFollowing;
		this.isFollower = isFollower;
	}

	public void setFollowingMemberFollow(List<FollowDto> followingMemberFollow, int maxCount) {
		if (followingMemberFollow == null) {
			this.followingMemberFollow = Collections.emptyList();
			this.followingMemberFollowCount = 0;
			return;
		}
		this.followingMemberFollow = followingMemberFollow
			.stream()
			.limit(maxCount)
			.collect(Collectors.toList());
		this.followingMemberFollowCount = followingMemberFollow.size() - this.followingMemberFollow.size();
	}

}
