package cloneproject.Instagram.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cloneproject.Instagram.entity.redis.EmailCode;

public interface EmailCodeRedisRepository extends CrudRepository<EmailCode, String>{
    
    public Optional<EmailCode> findByUsername(String username);
    
}
