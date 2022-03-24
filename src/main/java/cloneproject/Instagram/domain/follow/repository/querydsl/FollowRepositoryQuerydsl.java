package cloneproject.Instagram.domain.follow.repository.querydsl;

import java.util.List;

import cloneproject.Instagram.domain.follow.dto.FollowerDTO;

public interface FollowRepositoryQuerydsl {
    
    List<FollowerDTO> getFollowings(Long loginedMemberId, Long memberId);
    List<FollowerDTO> getFollowers(Long loginedMemberId, Long memberId);
}
