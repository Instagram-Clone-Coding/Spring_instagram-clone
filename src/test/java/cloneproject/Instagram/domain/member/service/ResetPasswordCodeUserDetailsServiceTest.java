package cloneproject.Instagram.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.entity.redis.ResetPasswordCode;
import cloneproject.Instagram.domain.member.exception.PasswordResetFailException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.member.repository.redis.ResetPasswordCodeRedisRepository;
import cloneproject.Instagram.util.domain.member.MemberUtils;

@ExtendWith(MockitoExtension.class)
public class ResetPasswordCodeUserDetailsServiceTest {

	@InjectMocks
	private ResetPasswordCodeUserDetailsService resetPasswordCodeUserDetailsService;

	@Mock
	private ResetPasswordCodeRedisRepository resetPasswordCodeRedisRepository;

	@Mock
	private MemberRepository memberRepository;

	@Nested
	class LoadUserByUsername {

		@Test
		void memberExist_ReturnUserDetails() {
			// given
			final long memberId = 1L;
			final String code = RandomStringUtils.random(20, true, true);
			final Member member = MemberUtils.newInstance();
			final ResetPasswordCode resetPasswordCode = ResetPasswordCode.builder()
				.code(code)
				.username(member.getUsername())
				.build();
			ReflectionTestUtils.setField(member, "id", memberId);

			given(memberRepository.findByUsername(member.getUsername())).willReturn(Optional.of(member));
			given(resetPasswordCodeRedisRepository.findByUsername(member.getUsername())).willReturn(
				Optional.of(resetPasswordCode));

			// when
			final UserDetails userDetails = resetPasswordCodeUserDetailsService.loadUserByUsername(
				member.getUsername());

			// then
			assertThat(userDetails.getUsername()).isEqualTo(String.valueOf(memberId));
			assertThat(userDetails.getPassword()).isEqualTo(code);
		}

		@Test
		void memberNotExist_ThrowException() {
			// given
			final String username = MemberUtils.getRandomUsername();

			// when
			final ThrowableAssert.ThrowingCallable executable = () -> resetPasswordCodeUserDetailsService.loadUserByUsername(
				username);

			// then
			assertThatThrownBy(executable).isInstanceOf(PasswordResetFailException.class);
		}

	}

}
