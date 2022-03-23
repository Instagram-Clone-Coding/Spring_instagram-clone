package cloneproject.Instagram.util;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final MemberRepository memberRepository;

    public static Long getLoginedMemberIdOrNull() {
        try {
            final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
            return Long.valueOf(memberId);
        } catch (Exception e) {
            return -1L;
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
