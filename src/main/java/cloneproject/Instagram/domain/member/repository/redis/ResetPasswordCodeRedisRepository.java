package cloneproject.Instagram.domain.member.repository.redis;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cloneproject.Instagram.domain.member.entity.redis.ResetPasswordCode;

public interface ResetPasswordCodeRedisRepository extends CrudRepository<ResetPasswordCode, String> {

	public Optional<ResetPasswordCode> findByUsername(String username);

}
