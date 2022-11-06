package cloneproject.Instagram.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cloneproject.Instagram.domain.member.entity.redis.RegisterCode;
import cloneproject.Instagram.domain.member.entity.redis.ResetPasswordCode;
import cloneproject.Instagram.domain.member.exception.EmailNotConfirmedException;
import cloneproject.Instagram.domain.member.exception.PasswordResetFailException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.member.repository.redis.RegisterCodeRedisRepository;
import cloneproject.Instagram.domain.member.repository.redis.ResetPasswordCodeRedisRepository;
import cloneproject.Instagram.infra.email.EmailService;
import cloneproject.Instagram.util.domain.member.MemberUtils;

@ExtendWith(MockitoExtension.class)
public class EmailCodeServiceTest {

	@InjectMocks
	private EmailCodeService emailCodeService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private RegisterCodeRedisRepository registerCodeRedisRepository;

	@Mock
	private ResetPasswordCodeRedisRepository resetPasswordCodeRedisRepository;

	@Mock
	private EmailService emailService;

	// TODO 추가 작성
	// ! static resource 불러오는 방식 해결
	@Nested
	class SendRegisterCode {

		@Test
		void validArguments_SendAndSaveCode() {
			// given
			final String username = MemberUtils.getRandomUsername();
			final String email = RandomStringUtils.random(20, true, true) + "email.com";

			// when
			emailCodeService.sendRegisterCode(username, email);

			// then
			then(registerCodeRedisRepository).should().save(any());
		}

	}

	@Nested
	class CheckRegisterCode {

		@Test
		void validArguments_DeleteCodeAndReturnTrue() {
			// given
			final RegisterCode registerCode = newRegisterCode();
			given(registerCodeRedisRepository.findByUsername(registerCode.getUsername())).willReturn(
				Optional.of(registerCode));

			// when
			final boolean result = emailCodeService.checkRegisterCode(registerCode.getUsername(),
				registerCode.getEmail(), registerCode.getCode());

			// then
			assertThat(result).isTrue();
			then(registerCodeRedisRepository).should().delete(any());
		}

		@Test
		void registerCodeNotExist_ThrowException() {
			// given
			final RegisterCode registerCode = newRegisterCode();

			// when
			final ThrowingCallable executable = () -> emailCodeService.checkRegisterCode(registerCode.getUsername(),
				registerCode.getEmail(), registerCode.getCode());

			// then
			assertThatThrownBy(executable).isInstanceOf(EmailNotConfirmedException.class);
		}

		@Test
		void registerCodeNotMatch_ReturnFalse() {
			// given
			final RegisterCode registerCode = newRegisterCode();
			given(registerCodeRedisRepository.findByUsername(registerCode.getUsername())).willReturn(
				Optional.of(registerCode));
			final String anotherCode = RandomStringUtils.random(30, true, true);

			// when
			final boolean result = emailCodeService.checkRegisterCode(registerCode.getUsername(),
				registerCode.getEmail(), anotherCode);

			// then
			assertThat(result).isFalse();
		}

		@Test
		void emailNotMatch_ReturnFalse() {
			// given
			final RegisterCode registerCode = newRegisterCode();
			given(registerCodeRedisRepository.findByUsername(registerCode.getUsername())).willReturn(
				Optional.of(registerCode));
			final String anotherEmail = RandomStringUtils.random(9, true, true) + "@email.com";

			// when
			final boolean result = emailCodeService.checkRegisterCode(registerCode.getUsername(),
				anotherEmail, registerCode.getCode());

			// then
			assertThat(result).isFalse();
		}

	}

	// TODO sendResetPasswordCode

	@Nested
	class CheckResetPasswordCode {

		@Test
		void validArguments_ReturnTrue() {
			// given
			final ResetPasswordCode resetPasswordCode = newResetPasswordCode();
			given(resetPasswordCodeRedisRepository.findByUsername(resetPasswordCode.getUsername())).willReturn(
				Optional.of(resetPasswordCode));

			// when
			final boolean result = emailCodeService.checkResetPasswordCode(resetPasswordCode.getUsername(),
				resetPasswordCode.getCode());

			// then
			assertThat(result).isTrue();
		}

		@Test
		void resetPasswordCodeNotExist_ThrowException() {
			// given
			final ResetPasswordCode resetPasswordCode = newResetPasswordCode();

			// when
			final ThrowingCallable executable = () -> emailCodeService.checkResetPasswordCode(
				resetPasswordCode.getUsername(), resetPasswordCode.getCode());

			// then
			assertThatThrownBy(executable).isInstanceOf(EmailNotConfirmedException.class);
		}

		@Test
		void resetPasswordCodeNotMatch_ReturnFalse() {
			// given
			final ResetPasswordCode resetPasswordCode = newResetPasswordCode();
			given(resetPasswordCodeRedisRepository.findByUsername(resetPasswordCode.getUsername())).willReturn(
				Optional.of(resetPasswordCode));
			final String anotherCode = RandomStringUtils.random(29, true, true);

			// when
			final boolean result = emailCodeService.checkResetPasswordCode(resetPasswordCode.getUsername(),
				anotherCode);

			// then
			assertThat(result).isFalse();
		}

	}

	@Nested
	class DeleteResetPasswordCode {

		@Test
		void validArguments_DeleteCode() {
			// given
			final ResetPasswordCode resetPasswordCode = newResetPasswordCode();
			given(resetPasswordCodeRedisRepository.findByUsername(resetPasswordCode.getUsername())).willReturn(
				Optional.of(resetPasswordCode));

			// when
			emailCodeService.deleteResetPasswordCode(resetPasswordCode.getUsername());

			// then
			then(resetPasswordCodeRedisRepository).should().delete(any());
		}

		@Test
		void resetPasswordCodeNotExist_ThrowException() {
			// given
			final ResetPasswordCode resetPasswordCode = newResetPasswordCode();

			// when
			final ThrowingCallable executable = () -> emailCodeService.deleteResetPasswordCode(
				resetPasswordCode.getUsername());

			// then
			assertThatThrownBy(executable).isInstanceOf(PasswordResetFailException.class);
		}

	}

	private RegisterCode newRegisterCode() {
		return RegisterCode.builder()
			.code(RandomStringUtils.random(30, true, true))
			.email(RandomStringUtils.random(10, true, true) + "@email.com")
			.username(RandomStringUtils.random(6, true, true))
			.build();
	}

	private ResetPasswordCode newResetPasswordCode() {
		return ResetPasswordCode.builder()
			.code(RandomStringUtils.random(30, true, true))
			.username(RandomStringUtils.random(6, true, true))
			.build();
	}

}
