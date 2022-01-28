package cloneproject.Instagram.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import cloneproject.Instagram.dto.member.FollowDTO;
import cloneproject.Instagram.dto.member.MiniProfileResponse;
import cloneproject.Instagram.dto.member.QFollowDTO;
import cloneproject.Instagram.dto.member.QMiniProfileResponse;
import cloneproject.Instagram.dto.member.QSearchedMemberDTO;
import cloneproject.Instagram.dto.member.QUserProfileResponse;
import cloneproject.Instagram.dto.member.SearchedMemberDTO;
import cloneproject.Instagram.dto.member.UserProfileResponse;
import cloneproject.Instagram.dto.post.PostImageDTO;
import cloneproject.Instagram.dto.post.QPostImageDTO;
import cloneproject.Instagram.entity.member.Member;
import lombok.RequiredArgsConstructor;

import static cloneproject.Instagram.entity.member.QFollow.follow;
import static cloneproject.Instagram.entity.member.QMember.member;
import static cloneproject.Instagram.entity.member.QBlock.block;
import static cloneproject.Instagram.entity.post.QPost.post;
import static cloneproject.Instagram.entity.post.QPostImage.postImage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MemberRepositoryQuerydslImpl implements MemberRepositoryQuerydsl{
    
    private final JPAQueryFactory queryFactory;

    @Override
    public UserProfileResponse getUserProfile(Long loginedUserId, String username){
        
        final String followingMemberFollow = queryFactory
                                                .select(follow.member.username)
                                                .from(follow)
                                                .where(follow.followMember.username.eq(username)
                                                        .and(follow.member.id.ne(loginedUserId)))
                                                .fetchFirst();

        final UserProfileResponse result = queryFactory.select(new QUserProfileResponse(
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
        result.checkBlock();
        result.setFollowingMemberFollow(followingMemberFollow);
        return result;
    }

    @Override
    public MiniProfileResponse getMiniProfile(Long loginedUserId, String username){
        
        final String followingMemberFollow = queryFactory
                                                .select(follow.member.username)
                                                .from(follow)
                                                .where(follow.followMember.username.eq(username)
                                                            .and(follow.member.id.ne(loginedUserId)))
                                                .fetchFirst();

        final MiniProfileResponse result = queryFactory.select(new QMiniProfileResponse(
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
                                                            postImage.image.imageUrl
                                                    ))
                                                    .from(postImage)
                                                    .where(postImage.post.id.in(postIds))
                                                    .fetch();

        result.setMemberPosts(postImageDTOs);
        result.checkBlock();
        result.setFollowingMemberFollow(followingMemberFollow);
        return result;

    }

    @Override
    public List<SearchedMemberDTO> searchMember(Long loginedUserId, String text){

        final List<SearchedMemberDTO> result = queryFactory
                                                .select(new QSearchedMemberDTO(
                                                    member.username,
                                                    member.name, 
                                                    member.image, 
                                                    JPAExpressions
                                                        .selectFrom(follow)
                                                        .where(follow.member.id.eq(loginedUserId).and(follow.followMember.username.eq(member.username)))
                                                        .exists(),
                                                    JPAExpressions
                                                        .selectFrom(follow)
                                                        .where(follow.member.username.eq(member.username).and(follow.followMember.id.eq(loginedUserId)))
                                                        .exists(),
                                                    JPAExpressions
                                                        .selectFrom(follow)
                                                        .where(follow.member.username.eq(member.username).and(follow.followMember.id.eq(loginedUserId)))
                                                        .exists()))
                                                .from(member)
                                                .where(member.username.contains(text).or(member.name.contains(text))
                                                                .and(member.id.notIn(JPAExpressions
                                                                                        .select(block.blockMember.id)
                                                                                        .from(block)
                                                                                        .where(block.member.id.eq(loginedUserId)))
                                                                .and(member.id.notIn(JPAExpressions
                                                                                        .select(block.member.id)
                                                                                        .from(block)
                                                                                        .where(block.blockMember.id.eq(loginedUserId))))))
                                                .fetch();
        final List<String> resultUsernames = result.stream().map(SearchedMemberDTO::getUsername)
                                            .collect(Collectors.toList());
        final List<FollowDTO> follows = queryFactory
                                            .select(new QFollowDTO(
                                                follow.member.username,
                                                follow.followMember.username
                                            ))
                                            .from(follow)
                                            .where(follow.followMember.username.in(resultUsernames)
                                                        .and(follow.member.id.ne(loginedUserId)))
                                            .fetch();
        if(follows.isEmpty()){
            return result;
        }
        final Map<String, List<FollowDTO>> followsMap = follows.stream()
                                            .collect(Collectors.groupingBy(FollowDTO::getFollowMemberUsername));
        result.forEach(r -> r.setFollowingMemberFollow(followsMap.get(r.getUsername())));

        return result;
    }

    @Override
    public List<Member> findAllByUsernames(List<String> usernames){
        List<Member> result = queryFactory
                                .selectFrom(member)
                                .where(member.username.in(usernames))
                                .fetch();
        return result;
    }

}
