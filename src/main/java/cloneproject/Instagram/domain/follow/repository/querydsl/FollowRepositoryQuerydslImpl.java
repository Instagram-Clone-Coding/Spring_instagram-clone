package cloneproject.Instagram.domain.follow.repository.querydsl;

import static cloneproject.Instagram.domain.follow.entity.QFollow.*;
import static cloneproject.Instagram.domain.member.entity.QMember.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.follow.dto.FollowDto;
import cloneproject.Instagram.domain.follow.dto.FollowerDto;
import cloneproject.Instagram.domain.follow.dto.QFollowDto;
import cloneproject.Instagram.domain.follow.dto.QFollowerDto;
import cloneproject.Instagram.domain.follow.entity.Follow;

@RequiredArgsConstructor
public class FollowRepositoryQuerydslImpl implements FollowRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<FollowerDto> findFollowings(Long loginId, Long memberId) {
		return findFollowerDtoInMemberIdList(loginId, findFollowingIdList(memberId));
	}

	@Override
	public List<FollowerDto> findFollowers(Long loginId, Long memberId) {
		return findFollowerDtoInMemberIdList(loginId, findFollowerIdList(memberId));
	}

	@Override
	public Map<String, List<FollowDto>> findFollowingMemberFollowMap(Long loginId, List<String> usernames) {
		final List<FollowDto> follows = queryFactory
			.select(new QFollowDto(
				follow.member.username,
				follow.followMember.username))
			.from(follow)
			.where(follow.followMember.username.in(usernames)
				.and(follow.member.id.in(findFollowingIdList(loginId))))
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
				.and(follow.member.id.in(findFollowingIdList(loginId))))
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

	private List<FollowerDto> findFollowerDtoInMemberIdList(Long loginId, JPQLQuery<Long> memberIdList) {
		return queryFactory
			.select(new QFollowerDto(
				member,
				JPAExpressions
					.selectFrom(follow)
					.where(follow.member.id.eq(loginId).and(follow.followMember.eq(member)))
					.exists(),
				JPAExpressions
					.selectFrom(follow)
					.where(follow.member.eq(member).and(follow.followMember.id.eq(loginId)))
					.exists(),
				member.id.eq(loginId)))
			.from(member)
			.where(member.id.in(memberIdList))
			.fetch();
	}

	private JPQLQuery<Long> findFollowingIdList(Long memberId) {
		return JPAExpressions
			.select(follow.followMember.id)
			.from(follow)
			.where(follow.member.id.eq(memberId));
	}

	private JPQLQuery<Long> findFollowerIdList(Long memberId) {
		return JPAExpressions
			.select(follow.member.id)
			.from(follow)
			.where(follow.followMember.id.eq(memberId));
	}

}
