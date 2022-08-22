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

	private static final long UNLOGIN_MEMBER_ID = -1L;
	private static final int HIDDEN_POST_COUNT = 0;
	private static final int HIDDEN_FOLLOWING_COUNT = 0;
	private static final int HIDDEN_FOLLOWER_COUNT = 0;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private BlockRepository blockRepository;

	@Test
	void findUserProfile_ValidArguments_AllFieldsMappedSuccess() {
		// given
		final long postCount = 3;
		final long followerCount = 4;
		final long followingCount = 5;

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		preparePosts(member, postCount);
		prepareFollows(member, followerCount, followingCount);

		// when
		final UserProfileResponse userProfileResponse = memberRepository.findUserProfile(UNLOGIN_MEMBER_ID,
			member.getUsername());

		// then
		assertThat(userProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
		assertThat(userProfileResponse.getMemberPostsCount()).isEqualTo(postCount);
		assertThat(userProfileResponse.getMemberFollowersCount()).isEqualTo(followerCount);
		assertThat(userProfileResponse.getMemberFollowingsCount()).isEqualTo(followingCount);
		assertThat(userProfileResponse.isMe()).isFalse();
		assertThat(userProfileResponse.isFollowing()).isFalse();
		assertThat(userProfileResponse.isFollower()).isFalse();
		assertThat(userProfileResponse.isBlocking()).isFalse();
		assertThat(userProfileResponse.isBlocked()).isFalse();
	}

	@Test
	void findUserProfile_RequestMyProfile_FindWithIsMeFlagTrue() {
		// given
		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		// when
		final UserProfileResponse userProfileResponse = memberRepository.findUserProfile(member.getId(),
			member.getUsername());

		// then
		assertThat(userProfileResponse.isMe()).isTrue();
	}

	@Test
	void findUserProfile_MemberIsBlocking_FindUserProfileWithCountHidden() {
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
		prepareFollows(member, followerCount, followingCount);

		// when
		final UserProfileResponse userProfileResponse = memberRepository.findUserProfile(blockedMember.getId(),
			member.getUsername());

		// then
		assertThat(userProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
		assertThat(userProfileResponse.isBlocked()).isTrue();
		assertThat(userProfileResponse.getMemberPostsCount()).isEqualTo(HIDDEN_POST_COUNT);
		assertThat(userProfileResponse.getMemberFollowersCount()).isEqualTo(HIDDEN_FOLLOWER_COUNT);
		assertThat(userProfileResponse.getMemberFollowingsCount()).isEqualTo(HIDDEN_FOLLOWING_COUNT);
	}

	@Test
	void findUserProfile_IsBlockingMember_FindWithBlockingFlagTrue() {
		// given
		final Member blockingMember = MemberUtils.newInstance();
		memberRepository.save(blockingMember);

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final Block block = Block.builder()
			.member(blockingMember)
			.blockMember(member)
			.build();
		blockRepository.save(block);

		// when
		final UserProfileResponse userProfileResponse = memberRepository.findUserProfile(blockingMember.getId(),
			member.getUsername());

		// then
		assertThat(userProfileResponse.isBlocking()).isTrue();
	}

	@Test
	void findUserProfile_MemberIsFollowing_FindWithFollowerFlagTrue() {
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
	void findUserProfile_IsFollowingMember_FindWithFollowingFlagTrue() {
		// given
		final Member followerMember = MemberUtils.newInstance();
		memberRepository.save(followerMember);

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final Follow follow = Follow.builder()
			.member(followerMember)
			.followMember(member)
			.build();
		followRepository.save(follow);

		// when
		final UserProfileResponse userProfileResponse = memberRepository.findUserProfile(followerMember.getId(),
			member.getUsername());

		// then
		assertThat(userProfileResponse.isFollowing()).isTrue();
	}

	@Test
	void findMiniProfile_ValidArguments_AllFieldsMappedSuccess() {
		// given
		final long postCount = 3;
		final long followerCount = 4;
		final long followingCount = 5;

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		preparePosts(member, postCount);
		prepareFollows(member, followerCount, followingCount);

		// when
		final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfile(UNLOGIN_MEMBER_ID,
			member.getUsername());

		// then
		assertThat(miniProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
		assertThat(miniProfileResponse.getMemberPostsCount()).isEqualTo(postCount);
		assertThat(miniProfileResponse.getMemberFollowersCount()).isEqualTo(followerCount);
		assertThat(miniProfileResponse.getMemberFollowingsCount()).isEqualTo(followingCount);
		assertThat(miniProfileResponse.isMe()).isFalse();
		assertThat(miniProfileResponse.isFollowing()).isFalse();
		assertThat(miniProfileResponse.isFollower()).isFalse();
		assertThat(miniProfileResponse.isBlocking()).isFalse();
		assertThat(miniProfileResponse.isBlocked()).isFalse();
	}

	@Test
	void findMiniProfile_RequestMyProfile_FindWithIsMeFlagTrue() {
		// given
		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		// when
		final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfile(member.getId(),
			member.getUsername());

		// then
		assertThat(miniProfileResponse.isMe()).isTrue();
	}

	@Test
	void findMiniProfile_MemberIsBlocking_FindUserProfileWithCountHidden() {
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
		prepareFollows(member, followerCount, followingCount);

		// when
		final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfile(blockedMember.getId(),
			member.getUsername());

		// then
		assertThat(miniProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
		assertThat(miniProfileResponse.isBlocked()).isTrue();
		assertThat(miniProfileResponse.getMemberPostsCount()).isEqualTo(HIDDEN_POST_COUNT);
		assertThat(miniProfileResponse.getMemberFollowersCount()).isEqualTo(HIDDEN_FOLLOWER_COUNT);
		assertThat(miniProfileResponse.getMemberFollowingsCount()).isEqualTo(HIDDEN_FOLLOWING_COUNT);
	}

	@Test
	void findMiniProfile_IsBlockingMember_FindWithBlockingFlagTrue() {
		// given
		final Member blockingMember = MemberUtils.newInstance();
		memberRepository.save(blockingMember);

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final Block block = Block.builder()
			.member(blockingMember)
			.blockMember(member)
			.build();
		blockRepository.save(block);

		// when
		final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfile(blockingMember.getId(),
			member.getUsername());

		// then
		assertThat(miniProfileResponse.isBlocking()).isTrue();
	}

	@Test
	void findMiniProfile_MemberIsFollowing_FindWithFollowerFlagTrue() {
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

	@Test
	void findMiniProfile_IsFollowingMember_FindWithFollowingFlagTrue() {
		// given
		final Member followerMember = MemberUtils.newInstance();
		memberRepository.save(followerMember);

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		final Follow follow = Follow.builder()
			.member(followerMember)
			.followMember(member)
			.build();
		followRepository.save(follow);

		// when
		final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfile(followerMember.getId(),
			member.getUsername());

		// then
		assertThat(miniProfileResponse.isFollowing()).isTrue();
	}

	private void preparePosts(Member member, long postCount) {
		final List<Post> posts = PostUtils.newInstances(member, postCount);
		postRepository.saveAll(posts);
	}

	private void prepareFollows(Member member, long followerCount, long followingCount) {
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
