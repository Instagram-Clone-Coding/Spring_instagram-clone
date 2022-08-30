package cloneproject.Instagram.domain.member.repository.querydsl;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Nested;
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

	@Nested
	class FindUserProfileByLoginMemberIdAndTargetUsername {

		@Test
		void validArguments_AllFieldsMappedSuccess() {
			// given
			final long postCount = 3;
			final long followerCount = 4;
			final long followingCount = 5;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			preparePosts(member, postCount);
			prepareFollows(member, followerCount, followingCount);

			// when
			final UserProfileResponse userProfileResponse = memberRepository.findUserProfileByLoginMemberIdAndTargetUsername(UNLOGIN_MEMBER_ID,
				member.getUsername());

			// then
			assertThat(userProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
			assertThat(userProfileResponse.getMemberName()).isEqualTo(member.getName());
			assertThat(userProfileResponse.getMemberIntroduce()).isEqualTo(member.getIntroduce());
			assertThat(userProfileResponse.getMemberWebsite()).isEqualTo(member.getWebsite());
			assertThat(userProfileResponse.getMemberImage()).isEqualTo(member.getImage());
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
		void findMyProfile_MeFlagIsTrue() {
			// given
			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			// when
			final UserProfileResponse userProfileResponse = memberRepository.findUserProfileByLoginMemberIdAndTargetUsername(member.getId(),
				member.getUsername());

			// then
			assertThat(userProfileResponse.isMe()).isTrue();
		}

		@Test
		void blockedByTarget_BlockedFlagIsTrueAndCountsAreHidden() {
			// given
			final long postCount = 1;
			final long followerCount = 2;
			final long followingCount = 3;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			final Block block = Block.builder()
				.member(target)
				.blockMember(member)
				.build();
			blockRepository.save(block);

			preparePosts(target, postCount);
			prepareFollows(target, followerCount, followingCount);

			// when
			final UserProfileResponse userProfileResponse = memberRepository.findUserProfileByLoginMemberIdAndTargetUsername(member.getId(),
				target.getUsername());

			// then
			assertThat(userProfileResponse.getMemberUsername()).isEqualTo(target.getUsername());
			assertThat(userProfileResponse.isBlocked()).isTrue();
			assertThat(userProfileResponse.getMemberPostsCount()).isEqualTo(HIDDEN_POST_COUNT);
			assertThat(userProfileResponse.getMemberFollowersCount()).isEqualTo(HIDDEN_FOLLOWER_COUNT);
			assertThat(userProfileResponse.getMemberFollowingsCount()).isEqualTo(HIDDEN_FOLLOWING_COUNT);
		}

		@Test
		void blockingTarget_BlockingFlagIsTrueAndCountsAreHidden() {
			// given
			final long postCount = 1;
			final long followerCount = 2;
			final long followingCount = 3;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			final Block block = Block.builder()
				.member(member)
				.blockMember(target)
				.build();
			blockRepository.save(block);

			preparePosts(target, postCount);
			prepareFollows(target, followerCount, followingCount);

			// when
			final UserProfileResponse userProfileResponse = memberRepository.findUserProfileByLoginMemberIdAndTargetUsername(member.getId(),
				target.getUsername());

			// then
			assertThat(userProfileResponse.getMemberUsername()).isEqualTo(target.getUsername());
			assertThat(userProfileResponse.isBlocking()).isTrue();
			assertThat(userProfileResponse.getMemberPostsCount()).isEqualTo(HIDDEN_POST_COUNT);
			assertThat(userProfileResponse.getMemberFollowersCount()).isEqualTo(HIDDEN_FOLLOWER_COUNT);
			assertThat(userProfileResponse.getMemberFollowingsCount()).isEqualTo(HIDDEN_FOLLOWING_COUNT);
		}

		@Test
		void notBlokcingEachOther_CountsAreNotHidden() {
			// given
			final long postCount = 1;
			final long followerCount = 2;
			final long followingCount = 3;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			preparePosts(target, postCount);
			prepareFollows(target, followerCount, followingCount);

			// when
			final UserProfileResponse userProfileResponse = memberRepository.findUserProfileByLoginMemberIdAndTargetUsername(member.getId(),
				target.getUsername());

			// then
			assertThat(userProfileResponse.getMemberUsername()).isEqualTo(target.getUsername());
			assertThat(userProfileResponse.getMemberPostsCount()).isEqualTo(postCount);
			assertThat(userProfileResponse.getMemberFollowersCount()).isEqualTo(followerCount);
			assertThat(userProfileResponse.getMemberFollowingsCount()).isEqualTo(followingCount);
		}

		@Test
		void followedByTarget_FollowerFlagIsTrue() {
			// given
			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			final Follow follow = Follow.builder()
				.member(target)
				.followMember(member)
				.build();
			followRepository.save(follow);

			// when
			final UserProfileResponse userProfileResponse = memberRepository.findUserProfileByLoginMemberIdAndTargetUsername(member.getId(),
				target.getUsername());

			// then
			assertThat(userProfileResponse.isFollower()).isTrue();
		}

		@Test
		void followingTarget_FollowingFlagTrue() {
			// given
			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			final Follow follow = Follow.builder()
				.member(member)
				.followMember(target)
				.build();
			followRepository.save(follow);

			// when
			final UserProfileResponse userProfileResponse = memberRepository.findUserProfileByLoginMemberIdAndTargetUsername(member.getId(),
				target.getUsername());

			// then
			assertThat(userProfileResponse.isFollowing()).isTrue();
		}

	}

	@Nested
	class FindMiniProfileByLoginMemberIdAndTargetUsername {

		@Test
		void validArguments_AllFieldsMappedSuccess() {
			// given
			final long postCount = 3;
			final long followerCount = 4;
			final long followingCount = 5;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			preparePosts(member, postCount);
			prepareFollows(member, followerCount, followingCount);

			// when
			final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfileByLoginMemberIdAndTargetUsername(UNLOGIN_MEMBER_ID,
				member.getUsername());

			// then
			assertThat(miniProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
			assertThat(miniProfileResponse.getMemberName()).isEqualTo(member.getName());
			assertThat(miniProfileResponse.getMemberWebsite()).isEqualTo(member.getWebsite());
			assertThat(miniProfileResponse.getMemberImage()).isEqualTo(member.getImage());
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
		void findMyProfile_MeFlagIsTrue() {
			// given
			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			// when
			final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfileByLoginMemberIdAndTargetUsername(member.getId(),
				member.getUsername());

			// then
			assertThat(miniProfileResponse.isMe()).isTrue();
		}

		@Test
		void blockedByTarget_BlockedFlagIsTrueAndCountsAreHidden() {
			// given
			final long postCount = 1;
			final long followerCount = 2;
			final long followingCount = 3;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			final Block block = Block.builder()
				.member(target)
				.blockMember(member)
				.build();
			blockRepository.save(block);

			preparePosts(target, postCount);
			prepareFollows(target, followerCount, followingCount);

			// when
			final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfileByLoginMemberIdAndTargetUsername(member.getId(),
				target.getUsername());

			// then
			assertThat(miniProfileResponse.getMemberUsername()).isEqualTo(target.getUsername());
			assertThat(miniProfileResponse.isBlocked()).isTrue();
			assertThat(miniProfileResponse.getMemberPostsCount()).isEqualTo(HIDDEN_POST_COUNT);
			assertThat(miniProfileResponse.getMemberFollowersCount()).isEqualTo(HIDDEN_FOLLOWER_COUNT);
			assertThat(miniProfileResponse.getMemberFollowingsCount()).isEqualTo(HIDDEN_FOLLOWING_COUNT);
		}

		@Test
		void blockingTarget_BlockingFlagIsTrueAndCountsAreHidden() {
			// given
			final long postCount = 1;
			final long followerCount = 2;
			final long followingCount = 3;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			final Block block = Block.builder()
				.member(member)
				.blockMember(target)
				.build();
			blockRepository.save(block);

			preparePosts(target, postCount);
			prepareFollows(target, followerCount, followingCount);

			// when
			final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfileByLoginMemberIdAndTargetUsername(member.getId(),
				target.getUsername());

			// then
			assertThat(miniProfileResponse.getMemberUsername()).isEqualTo(target.getUsername());
			assertThat(miniProfileResponse.isBlocking()).isTrue();
			assertThat(miniProfileResponse.getMemberPostsCount()).isEqualTo(HIDDEN_POST_COUNT);
			assertThat(miniProfileResponse.getMemberFollowersCount()).isEqualTo(HIDDEN_FOLLOWER_COUNT);
			assertThat(miniProfileResponse.getMemberFollowingsCount()).isEqualTo(HIDDEN_FOLLOWING_COUNT);
		}

		@Test
		void notBlockingEachOther_CountsAreNotHidden() {
			// given
			final long postCount = 1;
			final long followerCount = 2;
			final long followingCount = 3;

			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			preparePosts(target, postCount);
			prepareFollows(target, followerCount, followingCount);

			// when
			final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfileByLoginMemberIdAndTargetUsername(member.getId(),
				target.getUsername());

			// then
			assertThat(miniProfileResponse.getMemberUsername()).isEqualTo(target.getUsername());
			assertThat(miniProfileResponse.getMemberPostsCount()).isEqualTo(postCount);
			assertThat(miniProfileResponse.getMemberFollowersCount()).isEqualTo(followerCount);
			assertThat(miniProfileResponse.getMemberFollowingsCount()).isEqualTo(followingCount);
		}

		@Test
		void followedByTarget_FollowerFlagIsTrue() {
			// given
			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			final Follow follow = Follow.builder()
				.member(target)
				.followMember(member)
				.build();
			followRepository.save(follow);

			// when
			final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfileByLoginMemberIdAndTargetUsername(member.getId(),
				target.getUsername());

			// then
			assertThat(miniProfileResponse.isFollower()).isTrue();
		}

		@Test
		void followingTarget_FollowingFlagIsTrue() {
			// given
			final Member member = MemberUtils.newInstance();
			memberRepository.save(member);

			final Member target = MemberUtils.newInstance();
			memberRepository.save(target);

			final Follow follow = Follow.builder()
				.member(member)
				.followMember(target)
				.build();
			followRepository.save(follow);

			// when
			final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfileByLoginMemberIdAndTargetUsername(member.getId(),
				target.getUsername());

			// then
			assertThat(miniProfileResponse.isFollowing()).isTrue();
		}

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
