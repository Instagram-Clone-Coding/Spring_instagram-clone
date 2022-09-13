package cloneproject.Instagram.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.BlockMyselfFailException;
import cloneproject.Instagram.domain.member.exception.UnblockFailException;
import cloneproject.Instagram.domain.member.exception.UnblockMyselfFailException;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.error.exception.EntityAlreadyExistException;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;
import cloneproject.Instagram.util.domain.member.FollowUtils;
import cloneproject.Instagram.util.domain.member.MemberUtils;

@ExtendWith(MockitoExtension.class)
public class BlockServiceTest {

	private final String MEMBER_ID_VARIABLE_NAME = "id";

	@InjectMocks
	private BlockService blockService;

	@Mock
	private AuthUtil authUtil;

	@Mock
	private BlockRepository blockRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FollowRepository followRepository;

	@Nested
	class Block {

		@Test
		void validArguments_BlockTarget() {
			// given
			final long memberId = 1L;
			final long targetId = 2L;

			final String targetUsername = MemberUtils.getRandomUsername();
			final Member member = MemberUtils.newInstance();
			final Member target = MemberUtils.newInstance();

			ReflectionTestUtils.setField(member, MEMBER_ID_VARIABLE_NAME, memberId);
			ReflectionTestUtils.setField(target, MEMBER_ID_VARIABLE_NAME, targetId);

			given(authUtil.getLoginMember()).willReturn(member);
			given(memberRepository.findByUsername(targetUsername)).willReturn(Optional.of(target));

			// when
			final boolean didBlock = blockService.block(targetUsername);

			// then
			assertThat(didBlock).isTrue();
		}

		@Test
		void followingTarget_DeleteFollow() {
			// given
			final long memberId = 1L;
			final long targetId = 2L;

			final String targetUsername = MemberUtils.getRandomUsername();
			final Member member = MemberUtils.newInstance();
			final Member target = MemberUtils.newInstance();
			final Follow follow = FollowUtils.of(member, target);

			ReflectionTestUtils.setField(member, MEMBER_ID_VARIABLE_NAME, memberId);
			ReflectionTestUtils.setField(target, MEMBER_ID_VARIABLE_NAME, targetId);

			given(authUtil.getLoginMember()).willReturn(member);
			given(memberRepository.findByUsername(targetUsername)).willReturn(Optional.of(target));
			given(followRepository.findByMemberIdAndFollowMemberId(memberId, targetId)).willReturn(Optional.of(follow));

			// when
			final boolean didBlock = blockService.block(targetUsername);

			// then
			assertThat(didBlock).isTrue();
			then(followRepository).should().delete(follow);
		}

		@Test
		void followedByTarget_DeleteFollow() {
			// given
			final long memberId = 1L;
			final long targetId = 2L;

			final String targetUsername = MemberUtils.getRandomUsername();
			final Member member = MemberUtils.newInstance();
			final Member target = MemberUtils.newInstance();
			final Follow follow = FollowUtils.of(target, member);

			ReflectionTestUtils.setField(member, MEMBER_ID_VARIABLE_NAME, memberId);
			ReflectionTestUtils.setField(target, MEMBER_ID_VARIABLE_NAME, targetId);

			given(authUtil.getLoginMember()).willReturn(member);
			given(memberRepository.findByUsername(targetUsername)).willReturn(Optional.of(target));
			given(followRepository.findByMemberIdAndFollowMemberId(memberId, targetId)).willReturn(Optional.empty());
			given(followRepository.findByMemberIdAndFollowMemberId(targetId, memberId)).willReturn(Optional.of(follow));

			// when
			final boolean didBlock = blockService.block(targetUsername);

			// then
			assertThat(didBlock).isTrue();
			then(followRepository).should().delete(follow);
		}

		@Test
		void targetNotExist_ThrowException() {
			// given
			final long memberId = 1L;
			final String randomUsername = MemberUtils.getRandomUsername();
			final Member member = MemberUtils.newInstance();

			ReflectionTestUtils.setField(member, MEMBER_ID_VARIABLE_NAME, memberId);

			given(authUtil.getLoginMember()).willReturn(member);

			// when
			final ThrowingCallable executable = () -> blockService.block(randomUsername);

			// then
			assertThatThrownBy(executable).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void blockMyself_ThrowException() {
			// given
			final long memberId = 1L;
			final long targetId = 2L;

			final String memberUsername = MemberUtils.getRandomUsername();
			final Member member = MemberUtils.newInstance();

			ReflectionTestUtils.setField(member, MEMBER_ID_VARIABLE_NAME, memberId);

			given(authUtil.getLoginMember()).willReturn(member);
			given(memberRepository.findByUsername(memberUsername)).willReturn(Optional.of(member));

			// when
			final ThrowingCallable executable = () -> blockService.block(memberUsername);

			// then
			assertThatThrownBy(executable).isInstanceOf(BlockMyselfFailException.class);
		}

		@Test
		void blockedTargetAlready_ThrowException() {
			// given
			final long memberId = 1L;
			final long targetId = 2L;

			final String targetUsername = MemberUtils.getRandomUsername();
			final Member member = MemberUtils.newInstance();
			final Member target = MemberUtils.newInstance();

			ReflectionTestUtils.setField(member, MEMBER_ID_VARIABLE_NAME, memberId);
			ReflectionTestUtils.setField(target, MEMBER_ID_VARIABLE_NAME, targetId);

			given(authUtil.getLoginMember()).willReturn(member);
			given(memberRepository.findByUsername(targetUsername)).willReturn(Optional.of(target));
			given(blockRepository.existsByMemberIdAndBlockMemberId(memberId, targetId)).willReturn(true);

			// when
			final ThrowingCallable executable = () -> blockService.block(targetUsername);

			// then
			assertThatThrownBy(executable).isInstanceOf(EntityAlreadyExistException.class);
		}

	}

	@Nested
	class Unblock {

		@Test
		void blockingTarget_UnlockTarget() {
			// given
			final long memberId = 1L;
			final long targetId = 2L;

			final String targetUsername = MemberUtils.getRandomUsername();
			final Member member = MemberUtils.newInstance();
			final Member target = MemberUtils.newInstance();

			final cloneproject.Instagram.domain.member.entity.Block block = cloneproject.Instagram.domain.member.entity.Block.builder()
				.member(member)
				.blockMember(target)
				.build();

			ReflectionTestUtils.setField(member, MEMBER_ID_VARIABLE_NAME, memberId);
			ReflectionTestUtils.setField(target, MEMBER_ID_VARIABLE_NAME, targetId);

			given(authUtil.getLoginMemberId()).willReturn(memberId);
			given(memberRepository.findByUsername(targetUsername)).willReturn(Optional.of(target));
			given(blockRepository.findByMemberIdAndBlockMemberId(memberId, targetId)).willReturn(Optional.of(block));

			// when
			final boolean didUnblock = blockService.unblock(targetUsername);

			// then
			assertThat(didUnblock).isTrue();
			then(blockRepository).should().delete(block);
		}

		@Test
		void targetNotExist_ThrowException() {
			// given
			final long memberId = 1L;
			final String randomUsername = MemberUtils.getRandomUsername();

			given(authUtil.getLoginMemberId()).willReturn(memberId);

			// when
			final ThrowingCallable executable = () -> blockService.unblock(randomUsername);

			// then
			assertThatThrownBy(executable).isInstanceOf(EntityNotFoundException.class);
		}

		@Test
		void unblockMyself_ThrowException() {
			// given
			final long memberId = 1L;
			final String memberUsername = MemberUtils.getRandomUsername();

			final Member member = MemberUtils.newInstance();

			ReflectionTestUtils.setField(member, MEMBER_ID_VARIABLE_NAME, memberId);

			given(authUtil.getLoginMemberId()).willReturn(memberId);
			given(memberRepository.findByUsername(memberUsername)).willReturn(Optional.of(member));

			// when
			final ThrowingCallable executable = () -> blockService.unblock(memberUsername);

			// then
			assertThatThrownBy(executable).isInstanceOf(UnblockMyselfFailException.class);
		}

		@Test
		void notBlockingTarget_ThrowException() {
			// given
			final long memberId = 1L;
			final long targetId = 2L;

			final String targetUsername = MemberUtils.getRandomUsername();
			final Member member = MemberUtils.newInstance();
			final Member target = MemberUtils.newInstance();

			ReflectionTestUtils.setField(member, MEMBER_ID_VARIABLE_NAME, memberId);
			ReflectionTestUtils.setField(target, MEMBER_ID_VARIABLE_NAME, targetId);

			given(authUtil.getLoginMemberId()).willReturn(memberId);
			given(memberRepository.findByUsername(targetUsername)).willReturn(Optional.of(target));

			// when
			final ThrowingCallable executable = () -> blockService.unblock(targetUsername);

			// then
			assertThatThrownBy(executable).isInstanceOf(UnblockFailException.class);
		}

	}

}
