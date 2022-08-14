package cloneproject.Instagram.domain.follow.repository.querydsl;

import java.util.List;
import java.util.Map;

import cloneproject.Instagram.domain.follow.dto.FollowDto;
import cloneproject.Instagram.domain.follow.dto.FollowerDto;
import cloneproject.Instagram.domain.follow.entity.Follow;

public interface FollowRepositoryQuerydsl {

	List<FollowerDto> findFollowings(Long loginId, Long memberId);

	List<FollowerDto> findFollowers(Long loginId, Long memberId);

	Map<String, List<FollowDto>> findFollowingMemberFollowMap(Long loginId, List<String> usernames);

	List<FollowDto> findFollowingMemberFollowList(Long loginId, String username);

	List<Follow> findFollows(Long memberId, List<Long> agentIds);

}
