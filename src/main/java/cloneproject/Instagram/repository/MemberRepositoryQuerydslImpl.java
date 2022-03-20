package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.member.*;
import cloneproject.Instagram.entity.member.QMember;
import cloneproject.Instagram.repository.story.MemberStoryRedisRepository;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import cloneproject.Instagram.dto.post.PostImageDTO;
import cloneproject.Instagram.dto.post.QPostImageDTO;
import cloneproject.Instagram.entity.member.Member;
import lombok.RequiredArgsConstructor;

import static cloneproject.Instagram.entity.member.QFollow.follow;
import static cloneproject.Instagram.entity.member.QMember.member;
import static cloneproject.Instagram.entity.member.QBlock.block;
import static cloneproject.Instagram.entity.post.QPost.post;
import static cloneproject.Instagram.entity.post.QPostImage.postImage;
import static com.querydsl.core.group.GroupBy.groupBy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MemberRepositoryQuerydslImpl implements MemberRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;
    private final MemberStoryRedisRepository memberStoryRedisRepository;

    @Override
    public UserProfileResponse getUserProfile(Long loginedUserId, String username) {
        final String followingMemberFollow = queryFactory
                .select(follow.member.username)
                .from(follow)
                .where(follow.followMember.username.eq(username)
                        .and(follow.member.id.in(
                                JPAExpressions
                                        .select(follow.followMember.id)
                                        .from(follow)
                                        .where(follow.member.id.eq(loginedUserId))
                        )))
                .fetchFirst();

        final UserProfileResponse result = queryFactory
                .select(new QUserProfileResponse(
                        member.username,
                        member.name,
                        member.website,
                        member.image,
                        JPAExpressions
                                .selectFrom(follow)
                                .where(follow.member.id.eq(loginedUserId).and(follow.followMember.username.eq(username)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(follow)
                                .where(follow.member.username.eq(username).and(follow.followMember.id.eq(loginedUserId)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(block)
                                .where(block.member.id.eq(loginedUserId).and(block.blockMember.username.eq(username)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(block)
                                .where(block.member.username.eq(username).and(block.blockMember.id.eq(loginedUserId)))
                                .exists(),
                        member.introduce,
                        JPAExpressions
                                .select(post.count())
                                .from(post)
                                .where(post.member.username.eq(username)),
                        JPAExpressions
                                .select(follow.count())
                                .from(follow)
                                .where(follow.member.username.eq(username)),
                        JPAExpressions
                                .select(follow.count())
                                .from(follow)
                                .where(follow.followMember.username.eq(username)),
                        member.id.eq(loginedUserId)
                ))
                .from(member)
                .where(member.username.eq(username))
                .fetchOne();

        final Member member = queryFactory
                .selectFrom(QMember.member)
                .where(QMember.member.username.eq(username))
                .fetchOne();
        result.setHasStory(memberStoryRedisRepository.findById(member.getId()).isPresent());
        result.checkBlock();
        result.setFollowingMemberFollow(followingMemberFollow);

        return result;
    }

    @Override
    public MiniProfileResponse getMiniProfile(Long loginedUserId, String username) {
        final String followingMemberFollow = queryFactory
                .select(follow.member.username)
                .from(follow)
                .where(follow.followMember.username.eq(username)
                        .and(follow.member.id.in(
                                JPAExpressions
                                        .select(follow.followMember.id)
                                        .from(follow)
                                        .where(follow.member.id.eq(loginedUserId))
                        )))
                .fetchFirst();

        final MiniProfileResponse result = queryFactory
                .select(new QMiniProfileResponse(
                        member.username,
                        member.name,
                        member.image,
                        JPAExpressions
                                .selectFrom(follow)
                                .where(follow.member.id.eq(loginedUserId).and(follow.followMember.username.eq(username)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(follow)
                                .where(follow.member.username.eq(username).and(follow.followMember.id.eq(loginedUserId)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(block)
                                .where(block.member.id.eq(loginedUserId).and(block.blockMember.username.eq(username)))
                                .exists(),
                        JPAExpressions
                                .selectFrom(block)
                                .where(block.member.username.eq(username).and(block.blockMember.id.eq(loginedUserId)))
                                .exists(),
                        JPAExpressions
                                .select(post.count())
                                .from(post)
                                .where(post.member.username.eq(username)),
                        JPAExpressions
                                .select(follow.count())
                                .from(follow)
                                .where(follow.member.username.eq(username)),
                        JPAExpressions
                                .select(follow.count())
                                .from(follow)
                                .where(follow.followMember.username.eq(username)),
                        member.id.eq(loginedUserId)
                ))
                .from(member)
                .where(member.username.eq(username))
                .fetchOne();

        final List<Long> postIds = queryFactory
                .select(post.id)
                .from(post)
                .where(post.member.username.eq(username))
                .limit(3)
                .orderBy(post.uploadDate.desc())
                .fetch();

        final List<PostImageDTO> postImageDTOs = queryFactory
                .select(new QPostImageDTO(
                        postImage.post.id,
                        postImage.id,
                        postImage.image.imageUrl,
                        postImage.altText
                ))
                .from(postImage)
                .where(postImage.post.id.in(postIds))
                .fetch();

        final Member member = queryFactory
                .selectFrom(QMember.member)
                .where(QMember.member.username.eq(username))
                .fetchOne();
        result.setHasStory(memberStoryRedisRepository.findById(member.getId()).isPresent());
        result.setMemberPosts(postImageDTOs);
        result.checkBlock();
        result.setFollowingMemberFollow(followingMemberFollow);
        return result;

    }

    @Override
    public List<Member> findAllByUsernames(List<String> usernames) {
        return queryFactory
                .selectFrom(member)
                .where(member.username.in(usernames))
                .fetch();
    }
}
