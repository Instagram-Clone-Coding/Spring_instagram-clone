package cloneproject.Instagram.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cloneproject.Instagram.entity.redis.ResetPasswordCode;

public interface ResetPasswordCodeRedisRepository extends CrudRepository<ResetPasswordCode, String>{
    
    public Optional<ResetPasswordCode> findByUsername(String username);

}
