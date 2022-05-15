package cloneproject.Instagram.domain.follow.repository.querydsl;

import java.util.List;
import java.util.Map;

import cloneproject.Instagram.domain.follow.dto.FollowDTO;
import cloneproject.Instagram.domain.follow.dto.FollowerDTO;

public interface FollowRepositoryQuerydsl {

	List<FollowerDTO> getFollowings(Long loginedMemberId, Long memberId);

	List<FollowerDTO> getFollowers(Long loginedMemberId, Long memberId);

	Map<String, List<FollowDTO>> getFollowingMemberFollowMap(Long loginId, List<String> usernames);

}
