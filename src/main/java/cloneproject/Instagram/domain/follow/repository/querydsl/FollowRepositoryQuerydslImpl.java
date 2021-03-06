package cloneproject.Instagram.domain.follow.repository.querydsl;

import static cloneproject.Instagram.domain.follow.entity.QFollow.*;
import static cloneproject.Instagram.domain.member.entity.QMember.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.follow.dto.FollowDto;
import cloneproject.Instagram.domain.follow.dto.FollowerDto;
import cloneproject.Instagram.domain.follow.dto.QFollowDto;
import cloneproject.Instagram.domain.follow.dto.QFollowerDto;
import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;

@RequiredArgsConstructor
public class FollowRepositoryQuerydslImpl implements FollowRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;
	private final MemberStoryRedisRepository memberStoryRedisRepository;

	@Override
	public List<FollowerDto> findFollowings(Long loginedMemberId, Long memberId) {
		final List<FollowerDto> followerDtos = queryFactory
			.select(new QFollowerDto(
				member,
				JPAExpressions
					.selectFrom(follow)
					.where(follow.member.id.eq(loginedMemberId).and(follow.followMember.eq(member)))
					.exists(),
				JPAExpressions
					.selectFrom(follow)
					.where(follow.member.eq(member).and(follow.followMember.id.eq(loginedMemberId)))
					.exists(),
				member.id.eq(loginedMemberId)))
			.from(member)
			.where(member.id.in(JPAExpressions
				.select(follow.followMember.id)
				.from(follow)
				.where(follow.member.id.eq(memberId))))
			.fetch();

		followerDtos.forEach(follower -> {
			final MemberDto member = follower.getMember();
			final boolean hasStory = memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0;
			member.setHasStory(hasStory);
		});

		return followerDtos;
	}

	@Override
	public List<FollowerDto> findFollowers(Long loginedMemberId, Long memberId) {
		final List<FollowerDto> followerDtos = queryFactory
			.select(new QFollowerDto(
				member,
				JPAExpressions
					.selectFrom(follow)
					.where(follow.member.id.eq(loginedMemberId).and(follow.followMember.eq(member)))
					.exists(),
				JPAExpressions
					.selectFrom(follow)
					.where(follow.member.eq(member).and(follow.followMember.id.eq(loginedMemberId)))
					.exists(),
				member.id.eq(loginedMemberId)))
			.from(member)
			.where(member.id.in(JPAExpressions
				.select(follow.member.id)
				.from(follow)
				.where(follow.followMember.id.eq(memberId))))
			.fetch();

		followerDtos.forEach(follower -> {
			final MemberDto member = follower.getMember();
			final boolean hasStory = memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0;
			member.setHasStory(hasStory);
		});

		return followerDtos;
	}

	@Override
	public Map<String, List<FollowDto>> findFollowingMemberFollowMap(Long loginId, List<String> usernames) {
		final List<FollowDto> follows = queryFactory
			.select(new QFollowDto(
				follow.member.username,
				follow.followMember.username))
			.from(follow)
			.where(follow.followMember.username.in(usernames)
				.and(follow.member.id.in(
					JPAExpressions
						.select(follow.followMember.id)
						.from(follow)
						.where(follow.member.id.eq(loginId)))))
			.fetch();
		return follows.stream()
			.collect(Collectors.groupingBy(FollowDto::getFollowMemberUsername));
	}

	@Override
	public List<FollowDto> findFollowingMemberFollowList(Long loginId, String username) {
		return queryFactory
			.select(new QFollowDto(
				follow.member.username,
				follow.followMember.username))
			.from(follow)
			.where(follow.followMember.username.eq(username)
				.and(follow.member.id.in(
					JPAExpressions
						.select(follow.followMember.id)
						.from(follow)
						.where(follow.member.id.eq(loginId)))))
			.fetch();
	}

	@Override
	public List<Follow> findFollows(Long memberId, List<Long> agentIds) {
		return queryFactory
			.selectFrom(follow)
			.where(isFollowing(memberId, agentIds))
			.innerJoin(follow.member, member).fetchJoin()
			.innerJoin(follow.followMember, member).fetchJoin()
			.fetch();
	}

	private BooleanExpression isFollowing(Long memberId, List<Long> agentIds) {
		return follow.member.id.eq(memberId).and(
			follow.followMember.id.in(agentIds));
	}

}
