package cloneproject.Instagram.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.*;
import static org.mockito.BDDMockito.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cloneproject.Instagram.domain.member.dto.RegisterRequest;
import cloneproject.Instagram.domain.member.dto.UpdatePasswordRequest;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.AccountMismatchException;
import cloneproject.Instagram.domain.member.exception.PasswordEqualWithOldException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.search.repository.SearchMemberRepository;
import cloneproject.Instagram.global.error.exception.EntityAlreadyExistException;
import cloneproject.Instagram.global.util.AuthUtil;
import cloneproject.Instagram.global.util.JwtUtil;
import cloneproject.Instagram.infra.location.LocationService;
import cloneproject.Instagram.util.domain.member.MemberUtils;

@ExtendWith(MockitoExtension.class)
public class MemberAuthServiceTest {

	@InjectMocks
	private MemberAuthService memberAuthService;

	@Mock
	private AuthUtil authUtil;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private EmailCodeService emailCodeService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private RefreshTokenService refreshTokenService;

	@Mock
	private LocationService locationService;

	@Mock
	private SearchMemberRepository searchMemberRepository;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Nested
	class CheckUsername {

		@Test
		void UsernameExist_ReturnFalse() {
			// given
			final String username = MemberUtils.getRandomUsername();
			given(memberRepository.existsByUsername(username)).willReturn(true);

			// when
			final boolean canUseUsername = memberAuthService.checkUsername(username);

			// then
			assertThat(canUseUsername).isFalse();
		}

		@Test
		void UsernameNotExist_ReturnTrue() {
			// given
			final String username = MemberUtils.getRandomUsername();

			// when
			final boolean canUseUsername = memberAuthService.checkUsername(username);

			// then
			assertThat(canUseUsername).isTrue();
		}

	}

	@Nested
	class Register {

		@Test
		void validArguments_MemberRegistered() {
			// given
			final RegisterRequest registerRequest = newRegisterRequest();
			given(memberRepository.existsByUsername(registerRequest.getUsername())).willReturn(true);

			// when
			final ThrowingCallable executable = () -> memberAuthService.register(registerRequest);

			// then
			assertThatThrownBy(executable).isInstanceOf(EntityAlreadyExistException.class);
		}

		@Test
		void usernameExist_ThrowException() {
			// given
			final RegisterRequest registerRequest = newRegisterRequest();
			given(emailCodeService.checkRegisterCode(
				registerRequest.getUsername(),
				registerRequest.getEmail(),
				registerRequest.getCode())).willReturn(true);

			// when
			final boolean didRegister = memberAuthService.register(registerRequest);

			// then
			assertThat(didRegister).isTrue();
		}

		@Test
		void checkRegisterCodeFail_returnFalse() {
			// given
			final RegisterRequest registerRequest = newRegisterRequest();
			given(emailCodeService.checkRegisterCode(
				registerRequest.getUsername(),
				registerRequest.getEmail(),
				registerRequest.getCode())).willReturn(false);

			// when
			final boolean didRegister = memberAuthService.register(registerRequest);

			// then
			assertThat(didRegister).isFalse();
		}

		private RegisterRequest newRegisterRequest() {
			return new RegisterRequest(
				RandomStringUtils.random(20, true, true),
				RandomStringUtils.random(20, true, true),
				RandomStringUtils.random(20, true, true),
				RandomStringUtils.random(20, true, true) + "example.com",
				RandomStringUtils.random(6, true, true)
			);
		}

	}

	@Nested
	class SendEmailConfirmation {

		@Test
		void validArguments_SendEmail() {
			// given
			final String username = MemberUtils.getRandomUsername();
			final String email = RandomStringUtils.random(20, true, true) + "example.com";

			// when
			memberAuthService.sendEmailConfirmation(username, email);

			// then
			then(emailCodeService).should().sendRegisterCode(username, email);
		}

		@Test
		void usernameExist_ThrowException() {
			// given
			final String username = MemberUtils.getRandomUsername();
			final String email = RandomStringUtils.random(20, true, true) + "example.com";
			given(memberRepository.existsByUsername(username)).willReturn(true);

			// when
			final ThrowingCallable executable = () -> memberAuthService.sendEmailConfirmation(username, email);

			// then
			assertThatThrownBy(executable).isInstanceOf(EntityAlreadyExistException.class);
		}

	}

	@Nested
	class UpdatePassword {

		@Test
		void validArguments_UpdatePassword() {
			// given
			final Member member = MemberUtils.newInstance();
			final UpdatePasswordRequest updatePasswordRequest = newUpdatePasswordRequest();

			given(authUtil.getLoginMember()).willReturn(member);
			given(bCryptPasswordEncoder.matches(updatePasswordRequest.getOldPassword(), member.getPassword())).willReturn(true);

			// when
			memberAuthService.updatePassword(updatePasswordRequest);

			// then
			then(memberRepository).should().save(member);
		}

		@Test
		void wrongOldPassword_ThrowException() {
			// given
			final Member member = MemberUtils.newInstance();
			final UpdatePasswordRequest updatePasswordRequest = newUpdatePasswordRequest();

			given(authUtil.getLoginMember()).willReturn(member);
			given(bCryptPasswordEncoder.matches(updatePasswordRequest.getOldPassword(), member.getPassword())).willReturn(false);

			// when
			final ThrowingCallable executable = () -> memberAuthService.updatePassword(updatePasswordRequest);

			// then
			assertThatThrownBy(executable).isInstanceOf(AccountMismatchException.class);
		}

		@Test
		void NewAndOldPasswordAreEqual_ThrowException() {
			// given
			final Member member = MemberUtils.newInstance();
			final UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(
				member.getPassword(),
				member.getPassword()
			);

			given(authUtil.getLoginMember()).willReturn(member);
			given(bCryptPasswordEncoder.matches(updatePasswordRequest.getOldPassword(), member.getPassword())).willReturn(true);

			// when
			final ThrowingCallable executable = () -> memberAuthService.updatePassword(updatePasswordRequest);

			// then
			assertThatThrownBy(executable).isInstanceOf(PasswordEqualWithOldException.class);
		}

		private UpdatePasswordRequest newUpdatePasswordRequest() {
			return new UpdatePasswordRequest(
				RandomStringUtils.random(20, true, true),
				RandomStringUtils.random(20, true, true)
			);
		}

	}

}
