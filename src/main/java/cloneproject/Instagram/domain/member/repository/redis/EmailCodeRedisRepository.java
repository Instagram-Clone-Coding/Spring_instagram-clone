package cloneproject.Instagram.domain.member.repository.redis;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cloneproject.Instagram.domain.member.entity.redis.EmailCode;

public interface EmailCodeRedisRepository extends CrudRepository<EmailCode, String>{
    
    public Optional<EmailCode> findByUsername(String username);
    
}
