package cloneproject.Instagram.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.util.domain.member.MemberUtils;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	@Mock
	private MemberRepository memberRepository;

	@Nested
	class LoadUserByUsername {

		@Test
		void memberExist_ReturnUserDetails() {
			// given
			final long memberId = 1L;
			final Member member = MemberUtils.newInstance();
			ReflectionTestUtils.setField(member, "id", memberId);

			given(memberRepository.findByUsername(member.getUsername())).willReturn(Optional.of(member));

			// when
			final UserDetails userDetails = customUserDetailsService.loadUserByUsername(member.getUsername());

			// then
			assertThat(userDetails.getUsername()).isEqualTo(String.valueOf(memberId));
			assertThat(userDetails.getPassword()).isEqualTo(String.valueOf(member.getPassword()));
		}

		@Test
		void memberNotExist_ThrowException() {
			// given
			final String username = MemberUtils.getRandomUsername();

			// when
			final ThrowableAssert.ThrowingCallable executable = () -> customUserDetailsService.loadUserByUsername(
				username);

			// then
			assertThatThrownBy(executable).isInstanceOf(UsernameNotFoundException.class);
		}

	}

}
