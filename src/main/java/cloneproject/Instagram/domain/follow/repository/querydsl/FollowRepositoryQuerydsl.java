package cloneproject.Instagram.domain.follow.repository.querydsl;

import java.util.List;
import java.util.Map;

import cloneproject.Instagram.domain.follow.dto.FollowDto;
import cloneproject.Instagram.domain.follow.dto.FollowerDto;

public interface FollowRepositoryQuerydsl {

	List<FollowerDto> getFollowings(Long loginedMemberId, Long memberId);

	List<FollowerDto> getFollowers(Long loginedMemberId, Long memberId);

	Map<String, List<FollowDto>> getFollowingMemberFollowMap(Long loginId, List<String> usernames);

}
