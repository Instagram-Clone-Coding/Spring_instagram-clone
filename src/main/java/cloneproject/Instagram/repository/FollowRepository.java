package cloneproject.Instagram.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.entity.member.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long>{

    public boolean existsByMemberIdAndFollowMemberId(Long memberId, Long followMemberId);
    public Optional<Follow> findByMemberIdAndFollowMemberId(Long memberId, Long followMemberId);
    public List<Follow> findAllByMemberId(Long memberId);
    public List<Follow> findAllByFollowMemberId(Long followMemberId);
    public int countByMemberId(Long memberId);
    public int countByFollowMemberId(Long followMemberId);

}
