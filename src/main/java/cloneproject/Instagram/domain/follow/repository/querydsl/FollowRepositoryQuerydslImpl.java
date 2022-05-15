package cloneproject.Instagram.domain.follow.repository.querydsl;

import static cloneproject.Instagram.domain.follow.entity.QFollow.*;
import static cloneproject.Instagram.domain.member.entity.QMember.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import cloneproject.Instagram.domain.follow.dto.FollowDTO;
import cloneproject.Instagram.domain.follow.dto.FollowerDTO;
import cloneproject.Instagram.domain.follow.dto.QFollowDTO;
import cloneproject.Instagram.domain.follow.dto.QFollowerDTO;
import cloneproject.Instagram.domain.member.dto.MemberDTO;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FollowRepositoryQuerydslImpl implements FollowRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;
	private final MemberStoryRedisRepository memberStoryRedisRepository;

	@Override
	public List<FollowerDTO> getFollowings(Long loginedMemberId, Long memberId) {

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
	public List<FollowerDTO> getFollowers(Long loginedMemberId, Long memberId) {

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

	@Override
	public Map<String, List<FollowDTO>> getFollowingMemberFollowMap(Long loginId, List<String> usernames) {
		final List<FollowDTO> follows = queryFactory
			.select(new QFollowDTO(
				follow.member.username,
				follow.followMember.username
			))
			.from(follow)
			.where(follow.followMember.username.in(usernames)
				.and(follow.member.id.in(
					JPAExpressions
						.select(follow.followMember.id)
						.from(follow)
						.where(follow.member.id.eq(loginId))
				)))
			.fetch();
		return follows.stream()
			.collect(Collectors.groupingBy(FollowDTO::getFollowMemberUsername));
	}

}
