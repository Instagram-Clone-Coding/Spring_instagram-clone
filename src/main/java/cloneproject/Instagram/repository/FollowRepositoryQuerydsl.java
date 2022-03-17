package cloneproject.Instagram.repository;

import java.util.List;

import cloneproject.Instagram.dto.member.FollowerDTO;

public interface FollowRepositoryQuerydsl {
    
    List<FollowerDTO> getFollowings(Long loginedMemberId, Long memberId);
    List<FollowerDTO> getFollowers(Long loginedMemberId, Long memberId);
}
