package cloneproject.Instagram.domain.member.repository.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.repository.PostRepository;
import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.member.dto.MiniProfileResponse;
import cloneproject.Instagram.domain.member.dto.UserProfileResponse;
import cloneproject.Instagram.domain.member.entity.Block;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.config.QuerydslConfig;
import cloneproject.Instagram.util.domain.feed.PostUtils;
import cloneproject.Instagram.util.domain.member.FollowUtils;
import cloneproject.Instagram.util.domain.member.MemberUtils;

@DataJpaTest
@Import(QuerydslConfig.class)
public class MemberRepositoryQuerydslTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private BlockRepository blockRepository;

	@Test
	void findUserProfile_MemberNotBlocking_FindUserProfileWithDetail() {
		// given
		final long postCount = 3;
		final long followerCount = 4;
		final long followingCount = 5;

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		preparePosts(member, postCount);
		prepareFollow(member, followerCount, followingCount);

		// when
		final UserProfileResponse userProfileResponse = memberRepository.findUserProfile(-1L, member.getUsername());

		// then
		assertThat(userProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
		assertThat(userProfileResponse.getMemberPostsCount()).isEqualTo(postCount);
		assertThat(userProfileResponse.getMemberFollowersCount()).isEqualTo(followerCount);
		assertThat(userProfileResponse.getMemberFollowingsCount()).isEqualTo(followingCount);
	}

	@Test
	void findUserProfile_MemberBlocking_FindUserProfileWithoutDetail() {
		// given
		final long postCount = 1;
		final long followerCount = 2;
		final long followingCount = 3;

		final Member blockedMember = MemberUtils.newInstance();
		memberRepository.save(blockedMember);

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final Block block = Block.builder()
			.member(member)
			.blockMember(blockedMember)
			.build();
		blockRepository.save(block);

		preparePosts(member, postCount);
		prepareFollow(member, followerCount, followingCount);

		// when
		final UserProfileResponse userProfileResponse = memberRepository.findUserProfile(blockedMember.getId(),
			member.getUsername());

		// then
		assertThat(userProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
		assertThat(userProfileResponse.isBlocked()).isTrue();
		assertThat(userProfileResponse.getMemberPostsCount()).isEqualTo(0);
		assertThat(userProfileResponse.getMemberFollowersCount()).isEqualTo(0);
		assertThat(userProfileResponse.getMemberFollowingsCount()).isEqualTo(0);
	}

	@Test
	void findUserProfile_MemberIsFollowing_FindUserProfileWithFollowerFlagTrue() {
		// given
		final Member followingMember = MemberUtils.newInstance();
		memberRepository.save(followingMember);

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final Follow follow = Follow.builder()
			.member(member)
			.followMember(followingMember)
			.build();
		followRepository.save(follow);

		// when
		final UserProfileResponse userProfileResponse = memberRepository.findUserProfile(followingMember.getId(),
			member.getUsername());

		// then
		assertThat(userProfileResponse.isFollower()).isTrue();
	}

	@Test
	void findMiniProfile_MemberNotBlocking_FindMiniProfileWithDetail() {
		// given
		final long postCount = 3;
		final long followerCount = 4;
		final long followingCount = 5;

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		preparePosts(member, postCount);
		prepareFollow(member, followerCount, followingCount);

		// when
		final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfile(-1L, member.getUsername());

		// then
		assertThat(miniProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
		assertThat(miniProfileResponse.getMemberPostsCount()).isEqualTo(postCount);
		assertThat(miniProfileResponse.getMemberFollowersCount()).isEqualTo(followerCount);
		assertThat(miniProfileResponse.getMemberFollowingsCount()).isEqualTo(followingCount);
	}

	@Test
	void findMiniProfile_MemberBlocking_FindMiniProfileWithoutDetail() {
		// given
		final long postCount = 1;
		final long followerCount = 2;
		final long followingCount = 3;

		final Member blockedMember = MemberUtils.newInstance();
		memberRepository.save(blockedMember);

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final Block block = Block.builder()
			.member(member)
			.blockMember(blockedMember)
			.build();
		blockRepository.save(block);

		preparePosts(member, postCount);
		prepareFollow(member, followerCount, followingCount);

		// when
		final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfile(blockedMember.getId(),
			member.getUsername());

		// then
		assertThat(miniProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
		assertThat(miniProfileResponse.isBlocked()).isTrue();
		assertThat(miniProfileResponse.getMemberPostsCount()).isEqualTo(0);
		assertThat(miniProfileResponse.getMemberFollowersCount()).isEqualTo(0);
		assertThat(miniProfileResponse.getMemberFollowingsCount()).isEqualTo(0);
	}

	@Test
	void findMiniProfile_MemberIsFollowing_FindUserProfileWithFollowerFlagTrue() {
		// given
		final Member followingMember = MemberUtils.newInstance();
		memberRepository.save(followingMember);

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final Follow follow = Follow.builder()
			.member(member)
			.followMember(followingMember)
			.build();
		followRepository.save(follow);

		// when
		final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfile(followingMember.getId(),
			member.getUsername());

		// then
		assertThat(miniProfileResponse.isFollower()).isTrue();
	}

	private void preparePosts(Member member, long postCount) {
		final List<Post> posts = PostUtils.newInstances(member, postCount);
		postRepository.saveAll(posts);
	}

	private void prepareFollow(Member member, long followerCount, long followingCount) {
		final List<Member> followerMembers = MemberUtils.newDistinctInstances(followerCount);
		memberRepository.saveAll(followerMembers);

		final List<Follow> followers = FollowUtils.newFollowerInstances(followerMembers, member);
		followRepository.saveAll(followers);

		final List<Member> followingMembers = MemberUtils.newDistinctInstances(followingCount);
		memberRepository.saveAll(followingMembers);

		final List<Follow> followings = FollowUtils.newFollowingInstances(member, followingMembers);
		followRepository.saveAll(followings);
	}

}
