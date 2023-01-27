package cloneproject.Instagram.domain.member.service;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.entity.redis.ResetPasswordCode;
import cloneproject.Instagram.domain.member.exception.PasswordResetFailException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.member.repository.redis.ResetPasswordCodeRedisRepository;
import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResetPasswordCodeUserDetailsService implements UserDetailsService {

	private final ResetPasswordCodeRedisRepository resetPasswordCodeRedisRepository;
	private final MemberRepository memberRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) {
		return resetPasswordCodeRedisRepository.findByUsername(username)
			.map(this::createUserDetails)
			.orElseThrow(PasswordResetFailException::new);
	}

	private UserDetails createUserDetails(ResetPasswordCode resetPasswordCode) {
		final Member member = memberRepository.findByUsername(resetPasswordCode.getUsername())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
		final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole().toString());
		return new User(
			String.valueOf(member.getId()),
			resetPasswordCode.getCode(),
			Collections.singleton(grantedAuthority));
	}

}
