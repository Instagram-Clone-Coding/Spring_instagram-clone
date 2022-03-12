package cloneproject.Instagram.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import cloneproject.Instagram.entity.member.RefreshToken;

public interface RefreshTokenRepositoryQuerydsl {
    
    List<RefreshToken> findAllWithMemberIdAndDateAfter(Long memberId, LocalDateTime date);
    Optional<RefreshToken> findWithMemberIdAndDateAfterAndValue(Long memberId, LocalDateTime date, String value);

}
