package cloneproject.Instagram.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import cloneproject.Instagram.entity.member.RefreshToken;
import lombok.RequiredArgsConstructor;

import static cloneproject.Instagram.entity.member.QRefreshToken.refreshToken;

@RequiredArgsConstructor
public class RefreshTokenRepositoryQuerydslImpl  implements RefreshTokenRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RefreshToken> findAllWithMemberIdAndDateAfter(Long memberId, LocalDateTime date){
        List<RefreshToken> refreshTokens = queryFactory
            .select(refreshToken)
            .from(refreshToken)
            .where(refreshToken.member.id.eq(memberId)
                .and(refreshToken.deleted.isFalse())
                .and(refreshToken.lastModifiedAt.after(date)))
        .orderBy(refreshToken.lastModifiedAt.asc())
            .fetch();
        return refreshTokens;
    }

    @Override
    public Optional<RefreshToken> findWithMemberIdAndDateAfterAndValue(Long memberId, LocalDateTime date, String value){
        RefreshToken result = queryFactory
            .select(refreshToken)
            .from(refreshToken)
            .where(refreshToken.member.id.eq(memberId)
                .and(refreshToken.value.eq(value))
                .and(refreshToken.lastModifiedAt.after(date)))
            .fetchFirst();
        return Optional.ofNullable(result);
    }

}