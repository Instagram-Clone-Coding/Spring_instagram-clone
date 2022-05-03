package cloneproject.Instagram.domain.member.repository.querydsl;

import cloneproject.Instagram.domain.feed.dto.PostImageDTO;
import cloneproject.Instagram.domain.member.dto.MiniProfileResponse;
import cloneproject.Instagram.domain.member.dto.UserProfileResponse;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;
import cloneproject.Instagram.domain.member.dto.*;
import cloneproject.Instagram.domain.member.entity.QMember;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import cloneproject.Instagram.domain.feed.dto.QPostImageDTO;
import lombok.RequiredArgsConstructor;

import static cloneproject.Instagram.domain.follow.entity.QFollow.follow;
import static cloneproject.Instagram.domain.member.entity.QMember.member;
import static cloneproject.Instagram.domain.member.entity.QBlock.block;
import static cloneproject.Instagram.domain.feed.entity.QPost.post;
import static cloneproject.Instagram.domain.feed.entity.QPostImage.postImage;

import java.util.List;

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
										.where(follow.member.id
												.eq(loginedUserId)))))
				.fetchFirst();

		final UserProfileResponse result = queryFactory
				.select(new QUserProfileResponse(
						member.username,
						member.name,
						member.website,
						member.image,
						JPAExpressions
								.selectFrom(follow)
								.where(follow.member.id.eq(loginedUserId)
										.and(follow.followMember.username
												.eq(username)))
								.exists(),
						JPAExpressions
								.selectFrom(follow)
								.where(follow.member.username.eq(username)
										.and(follow.followMember.id
												.eq(loginedUserId)))
								.exists(),
						JPAExpressions
								.selectFrom(block)
								.where(block.member.id.eq(loginedUserId)
										.and(block.blockMember.username
												.eq(username)))
								.exists(),
						JPAExpressions
								.selectFrom(block)
								.where(block.member.username.eq(username).and(
										block.blockMember.id.eq(loginedUserId)))
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
						member.id.eq(loginedUserId)))
				.from(member)
				.where(member.username.eq(username))
				.fetchOne();

		final Member member = queryFactory
				.selectFrom(QMember.member)
				.where(QMember.member.username.eq(username))
				.fetchOne();
		result.setHasStory(memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0);
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
										.where(follow.member.id
												.eq(loginedUserId)))))
				.fetchFirst();

		final MiniProfileResponse result = queryFactory
				.select(new QMiniProfileResponse(
						member.username,
						member.name,
						member.image,
						JPAExpressions
								.selectFrom(follow)
								.where(follow.member.id.eq(loginedUserId)
										.and(follow.followMember.username
												.eq(username)))
								.exists(),
						JPAExpressions
								.selectFrom(follow)
								.where(follow.member.username.eq(username)
										.and(follow.followMember.id
												.eq(loginedUserId)))
								.exists(),
						JPAExpressions
								.selectFrom(block)
								.where(block.member.id.eq(loginedUserId)
										.and(block.blockMember.username
												.eq(username)))
								.exists(),
						JPAExpressions
								.selectFrom(block)
								.where(block.member.username.eq(username).and(
										block.blockMember.id.eq(loginedUserId)))
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
						member.id.eq(loginedUserId)))
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
						postImage.altText))
				.from(postImage)
				.where(postImage.post.id.in(postIds))
				.fetch();

		final Member member = queryFactory
				.selectFrom(QMember.member)
				.where(QMember.member.username.eq(username))
				.fetchOne();
		result.setHasStory(memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0);
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
