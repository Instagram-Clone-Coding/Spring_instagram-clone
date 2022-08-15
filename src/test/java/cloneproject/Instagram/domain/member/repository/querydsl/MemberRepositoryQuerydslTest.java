package cloneproject.Instagram.domain.member.repository.querydsl;

import static org.assertj.core.api.Assertions.*;

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
	void findUserProfile_MemberNotBlocking_FindUserProfile() {
		// given
		final long postCount = 3;
		final long followerCount = 4;
		final long followingCount = 5;

		final Member followingMember = MemberUtils.newInstance();
		memberRepository.save(followingMember);

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		Follow follow = Follow.builder()
			.member(member)
			.followMember(followingMember)
			.build();
		followRepository.save(follow);

		prepareProfileDetail(member, postCount, followerCount, followingCount);

		// when
		final UserProfileResponse userProfileResponse = memberRepository.findUserProfile(followingMember.getId(),
			member.getUsername());

		// then
		assertThat(userProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
		assertThat(userProfileResponse.getMemberPostsCount()).isEqualTo(postCount);
		assertThat(userProfileResponse.getMemberFollowersCount()).isEqualTo(followerCount);
		assertThat(userProfileResponse.getMemberFollowingsCount()).isEqualTo(followingCount + 1);
		assertThat(userProfileResponse.isFollower()).isTrue();
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

		Block block = Block.builder()
			.member(member)
			.blockMember(blockedMember)
			.build();
		blockRepository.save(block);

		prepareProfileDetail(member, postCount, followerCount, followingCount);

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
	void findMiniProfile_MemberNotBlocking_FindMiniProfile() {
		// given
		final long postCount = 3;
		final long followerCount = 4;
		final long followingCount = 5;

		final Member followingMember = MemberUtils.newInstance();
		memberRepository.save(followingMember);

		final Member member = MemberUtils.newInstance();
		memberRepository.save(member);

		Follow follow = Follow.builder()
			.member(member)
			.followMember(followingMember)
			.build();
		followRepository.save(follow);

		prepareProfileDetail(member, postCount, followerCount, followingCount);

		// when
		final MiniProfileResponse miniProfileResponse = memberRepository.findMiniProfile(followingMember.getId(),
			member.getUsername());

		// then
		assertThat(miniProfileResponse.getMemberUsername()).isEqualTo(member.getUsername());
		assertThat(miniProfileResponse.getMemberPostsCount()).isEqualTo(postCount);
		assertThat(miniProfileResponse.getMemberFollowersCount()).isEqualTo(followerCount);
		assertThat(miniProfileResponse.getMemberFollowingsCount()).isEqualTo(followingCount + 1);
		assertThat(miniProfileResponse.isFollower()).isTrue();
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

		Block block = Block.builder()
			.member(member)
			.blockMember(blockedMember)
			.build();
		blockRepository.save(block);

		prepareProfileDetail(member, postCount, followerCount, followingCount);

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

	private void prepareProfileDetail(Member member, long postCount, long followerCount, long followingCount) {
		for (long count = 1; count <= postCount; count++) {
			final Post post = PostUtils.newInstance(member);
			postRepository.save(post);
		}

		for (long count = 1; count <= followerCount; count++) {
			final Member followerMember = MemberUtils.newInstance();
			memberRepository.save(followerMember);

			final Follow follow = Follow.builder()
				.member(followerMember)
				.followMember(member)
				.build();
			followRepository.save(follow);
		}

		for (long count = 1; count <= followingCount; count++) {
			final Member followingMember = MemberUtils.newInstance();
			memberRepository.save(followingMember);

			final Follow follow = Follow.builder()
				.member(member)
				.followMember(followingMember)
				.build();
			followRepository.save(follow);
		}
	}

}
