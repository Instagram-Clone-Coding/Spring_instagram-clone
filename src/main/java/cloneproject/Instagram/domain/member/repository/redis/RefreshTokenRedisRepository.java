package cloneproject.Instagram.domain.member.repository.redis;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cloneproject.Instagram.domain.member.entity.redis.RefreshToken;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {

	List<RefreshToken> findAllByMemberId(Long memberId);

	Optional<RefreshToken> findByMemberIdAndValue(Long memberId, String value);

	Optional<RefreshToken> findByMemberIdAndId(Long MemberId, String id);

}