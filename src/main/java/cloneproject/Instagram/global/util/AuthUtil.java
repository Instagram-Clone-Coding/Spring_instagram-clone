package cloneproject.Instagram.global.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class AuthUtil {

	private final MemberRepository memberRepository;

	public Long getLoginMemberIdOrNull() {
		try {
			final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
			return Long.valueOf(memberId);
		} catch (Exception e) {
			return -1L;
		}
	}

	public Long getLoginMemberId() {
		try {
			final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
			return Long.valueOf(memberId);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public Member getLoginMember() {
		try {
			final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
			return memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
}
