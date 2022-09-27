package cloneproject.Instagram.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cloneproject.Instagram.domain.member.dto.RegisterRequest;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.search.repository.SearchMemberRepository;
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
			final String username = MemberUtils.getRandomUsername();
			given(memberRepository.existsByUsername(username)).willReturn(true);

			// when
			final boolean canUseUsername = memberAuthService.checkUsername(username);

			// then
			assertThat(canUseUsername).isFalse();
		}


	}

	private RegisterRequest newInstance() {
		return new RegisterRequest(
			MemberUtils.getRandomUsername(),

		);
	}

}
