package cloneproject.Instagram.domain.follow.repository.querydsl;

import java.util.List;

import cloneproject.Instagram.domain.follow.dto.FollowerDTO;
import cloneproject.Instagram.domain.member.dto.MemberDTO;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import cloneproject.Instagram.domain.follow.dto.QFollowerDTO;
import lombok.RequiredArgsConstructor;

import static cloneproject.Instagram.domain.follow.entity.QFollow.follow;
import static cloneproject.Instagram.domain.member.entity.QMember.member;

@RequiredArgsConstructor
public class FollowRepositoryQuerydslImpl implements FollowRepositoryQuerydsl{

    private final JPAQueryFactory queryFactory;
    private final MemberStoryRedisRepository memberStoryRedisRepository;

    @Override
    public List<FollowerDTO> getFollowings(Long loginedMemberId, Long memberId){

        final List<FollowerDTO> followerDTOs = queryFactory
                    .select(new QFollowerDTO(
                        member,
                        JPAExpressions
                            .selectFrom(follow)
                            .where(follow.member.id.eq(loginedMemberId).and(follow.followMember.eq(member)))
                            .exists(),
                        JPAExpressions
                            .selectFrom(follow)
                            .where(follow.member.eq(member).and(follow.followMember.id.eq(loginedMemberId)))
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

        followerDTOs.forEach(follower -> {
            final MemberDTO member = follower.getMember();
            final boolean hasStory = memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0;
            member.setHasStory(hasStory);
        });

        return followerDTOs;
    }

    @Override
    public List<FollowerDTO> getFollowers(Long loginedMemberId, Long memberId){

        final List<FollowerDTO> followerDTOs = queryFactory
                    .select(new QFollowerDTO(
                        member,
                        JPAExpressions
                            .selectFrom(follow)
                            .where(follow.member.id.eq(loginedMemberId).and(follow.followMember.eq(member)))
                            .exists(),
                        JPAExpressions
                            .selectFrom(follow)
                            .where(follow.member.eq(member).and(follow.followMember.id.eq(loginedMemberId)))
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

        followerDTOs.forEach(follower -> {
            final MemberDTO member = follower.getMember();
            final boolean hasStory = memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0;
            member.setHasStory(hasStory);
        });

        return followerDTOs;

    }

}
