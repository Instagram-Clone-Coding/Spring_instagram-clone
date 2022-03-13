package cloneproject.Instagram.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cloneproject.Instagram.entity.member.RefreshToken;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String>{
    
    List<RefreshToken> findByMemberId(Long memberId);
    boolean existsByMemberIdAndValue(Long memberId, String value);
    Optional<RefreshToken> findByMemberIdAndValue(Long memberId, String value);
    Optional<RefreshToken> findByMemberIdAndId(Long MemberId, String id);

}