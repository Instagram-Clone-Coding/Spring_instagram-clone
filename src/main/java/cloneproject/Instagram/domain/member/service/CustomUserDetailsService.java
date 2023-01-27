package cloneproject.Instagram.domain.member.service;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private static final String errorMessage = "일치하는 계정이 없습니다";
	private final MemberRepository memberRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) {
		return memberRepository.findByUsername(username)
			.map(this::createUserDetails)
			.orElseThrow(() -> new UsernameNotFoundException(errorMessage));
	}

	private UserDetails createUserDetails(Member member) {
		final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole().toString());
		return new User(
			String.valueOf(member.getId()),
			member.getPassword(),
			Collections.singleton(grantedAuthority));
	}

}
