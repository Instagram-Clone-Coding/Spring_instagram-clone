package cloneproject.Instagram.repository;

import java.util.List;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import cloneproject.Instagram.dto.member.FollowerDTO;
import cloneproject.Instagram.dto.member.QFollowerDTO;
import lombok.RequiredArgsConstructor;

import static cloneproject.Instagram.entity.member.QFollow.follow;
import static cloneproject.Instagram.entity.member.QMember.member;

@RequiredArgsConstructor
public class FollowRepositoryQuerydslImpl implements FollowRepositoryQuerydsl{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FollowerDTO> getFollwings(Long loginedMemberId, Long memberId){

        final List<FollowerDTO> followerDTOs = queryFactory
                    .select(new QFollowerDTO(
                        member.username,
                        member.name,
                        member.image,
                        JPAExpressions
                            .selectFrom(follow)
                            .where(follow.member.id.eq(loginedMemberId).and(follow.followMember.eq(member)))
                            .exists(),
                        JPAExpressions
                            .selectFrom(follow)
                            .where(follow.member.eq(member).and(follow.followMember.id.eq(loginedMemberId)))
                            .exists(),
                        JPAExpressions
                            .selectFrom(follow)
                            .where()
                            .exists(),
                        member.id.eq(loginedMemberId)
                    ))
                    .from(member)
                    .where(member.id.in(JPAExpressions
                                        .select(follow.followMember.id)
                                        .from(follow)
                                        .where(follow.member.id.eq(memberId))
                    ))
                    .fetch();

        return followerDTOs;

    }

    @Override
    public List<FollowerDTO> getFollwers(Long loginedMemberId, Long memberId){

        final List<FollowerDTO> followerDTOs = queryFactory
                    .select(new QFollowerDTO(
                        member.username,
                        member.name,
                        member.image,
                        JPAExpressions
                            .selectFrom(follow)
                            .where(follow.member.id.eq(loginedMemberId).and(follow.followMember.eq(member)))
                            .exists(),
                        JPAExpressions
                            .selectFrom(follow)
                            .where(follow.member.eq(member).and(follow.followMember.id.eq(loginedMemberId)))
                            .exists(),
                        JPAExpressions
                            .selectFrom(follow)
                            .where()
                            .exists(),
                        member.id.eq(loginedMemberId)
                    ))
                    .from(member)
                    .where(member.id.in(JPAExpressions
                                        .select(follow.member.id)
                                        .from(follow)
                                        .where(follow.followMember.id.eq(memberId))
                    ))
                    .fetch();

        return followerDTOs;

    }

}
