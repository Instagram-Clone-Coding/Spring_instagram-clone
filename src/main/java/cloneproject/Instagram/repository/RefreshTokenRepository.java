package cloneproject.Instagram.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.entity.member.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenRepositoryQuerydsl{

    int countByMemberId(Long memberId);
    void deleteAllByLastModifiedAtBefore(LocalDateTime date);
    void deleteByValue(String value);

}
