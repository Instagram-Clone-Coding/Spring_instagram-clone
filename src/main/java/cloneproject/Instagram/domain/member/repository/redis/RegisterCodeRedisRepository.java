package cloneproject.Instagram.domain.member.repository.redis;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cloneproject.Instagram.domain.member.entity.redis.RegisterCode;

public interface RegisterCodeRedisRepository extends CrudRepository<RegisterCode, String> {

	Optional<RegisterCode> findByUsername(String username);

}
