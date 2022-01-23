package cloneproject.Instagram.repository;

import java.util.List;

import cloneproject.Instagram.dto.member.FollowerDTO;

public interface FollowRepositoryQuerydsl {
    
    public List<FollowerDTO> getFollwings(Long loginedMemberId, Long memberId);
    public List<FollowerDTO> getFollwers(Long loginedMemberId, Long memberId);

}
