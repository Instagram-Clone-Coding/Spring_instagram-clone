package cloneproject.Instagram.domain.member.service;

import static org.assertj.core.api.Assertions.*;

import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;
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

			final String targetUsername = RandomStringUtils.random(15, true, true);
			final Member member = mock(Member.class);
			final Member target = mock(Member.class);
			// final Member member = MemberUtils.newInstance();
			// final Member target = MemberUtils.newInstance();

			given(member.getId()).willReturn(memberId);
			given(target.getId()).willReturn(targetId);
			// ReflectionTestUtils.setField(member, MEMBER_ID_VARIABLE_NAME, memberId);
			// ReflectionTestUtils.setField(target, MEMBER_ID_VARIABLE_NAME, targetId);

			given(authUtil.getLoginMember()).willReturn(member);
			given(memberRepository.findByUsername(targetUsername)).willReturn(Optional.of(target));

			// when
			boolean didBlock = blockService.block(targetUsername);

			assertThat(didBlock).isTrue();
			then(blockRepository).should().save(any());
		}

		@Test
		void targetNotExist_ThrowException() {
			// given
			final long memberId = 1L;
			final String randomUsername = RandomStringUtils.random(15, true, true);
			final Member member = MemberUtils.newInstance();

			ReflectionTestUtils.setField(member, MEMBER_ID_VARIABLE_NAME, memberId);

			given(authUtil.getLoginMember()).willReturn(member);

			// when
			final Throwable throwable = catchThrowable(() -> blockService.block(randomUsername));

			// then
			assertThat(throwable).isInstanceOf(EntityNotFoundException.class);
		}

	}

}
