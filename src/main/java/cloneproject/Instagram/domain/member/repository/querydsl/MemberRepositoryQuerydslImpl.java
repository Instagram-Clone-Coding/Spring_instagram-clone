package cloneproject.Instagram.domain.member.repository.querydsl;

import static cloneproject.Instagram.domain.feed.entity.QPost.*;
import static cloneproject.Instagram.domain.follow.entity.QFollow.*;
import static cloneproject.Instagram.domain.member.entity.QBlock.*;
import static cloneproject.Instagram.domain.member.entity.QMember.*;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.member.dto.MiniProfileResponse;
import cloneproject.Instagram.domain.member.dto.QMiniProfileResponse;
import cloneproject.Instagram.domain.member.dto.QUserProfileResponse;
import cloneproject.Instagram.domain.member.dto.UserProfileResponse;
import cloneproject.Instagram.domain.member.entity.Member;

@RequiredArgsConstructor
public class MemberRepositoryQuerydslImpl implements MemberRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public UserProfileResponse findUserProfile(Long loginUserId, String username) {
		return queryFactory
			.select(new QUserProfileResponse(
				member.username,
				member.name,
				member.website,
				member.image,
				isFollowing(loginUserId, username),
				isFollower(loginUserId, username),
				isBlocking(loginUserId, username),
				isBlocked(loginUserId, username),
				member.introduce,
				getPostCount(username),
				getFollowingCount(username),
				getFollowerCount(username),
				member.id.eq(loginUserId)))
			.from(member)
			.where(member.username.eq(username))
			.fetchOne();
	}

	@Override
	public MiniProfileResponse findMiniProfile(Long loginUserId, String username) {
		return queryFactory
			.select(new QMiniProfileResponse(
				member.username,
				member.name,
				member.image,
				isFollowing(loginUserId, username),
				isFollower(loginUserId, username),
				isBlocking(loginUserId, username),
				isBlocked(loginUserId, username),
				getPostCount(username),
				getFollowingCount(username),
				getFollowerCount(username),
				member.id.eq(loginUserId)))
			.from(member)
			.where(member.username.eq(username))
			.fetchOne();
	}

	@Override
	public List<Member> findAllByUsernames(List<String> usernames) {
		return queryFactory
			.selectFrom(member)
			.where(member.username.in(usernames))
			.fetch();
	}

	private JPQLQuery<Long> getPostCount(String targetUsername) {
		return JPAExpressions
			.select(post.count())
			.from(post)
			.where(post.member.username.eq(targetUsername));
	}

	private JPQLQuery<Long> getFollowingCount(String targetUsername) {
		return JPAExpressions
			.select(follow.count())
			.from(follow)
			.where(follow.member.username.eq(targetUsername));
	}

	private JPQLQuery<Long> getFollowerCount(String targetUsername) {
		return JPAExpressions
			.select(follow.count())
			.from(follow)
			.where(follow.followMember.username.eq(targetUsername));
	}

	private BooleanExpression isFollowing(Long loginUserId, String targetUsername) {
		return JPAExpressions
			.selectFrom(follow)
			.where(follow.member.id.eq(loginUserId)
				.and(follow.followMember.username.eq(targetUsername)))
			.exists();
	}

	private BooleanExpression isFollower(Long loginUserId, String targetUsername) {
		return JPAExpressions
			.selectFrom(follow)
			.where(follow.member.username.eq(targetUsername)
				.and(follow.followMember.id.eq(loginUserId)))
			.exists();
	}

	private BooleanExpression isBlocking(Long loginUserId, String targetUsername) {
		return JPAExpressions
			.selectFrom(block)
			.where(block.member.id.eq(loginUserId)
				.and(block.blockMember.username.eq(targetUsername)))
			.exists();
	}

	private BooleanExpression isBlocked(Long loginUserId, String targetUsername) {
		return JPAExpressions
			.selectFrom(block)
			.where(block.member.username.eq(targetUsername).and(
				block.blockMember.id.eq(loginUserId)))
			.exists();
	}

}
