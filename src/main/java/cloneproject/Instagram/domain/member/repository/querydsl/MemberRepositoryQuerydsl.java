package cloneproject.Instagram.domain.member.repository.querydsl;

import java.util.List;

import cloneproject.Instagram.domain.member.dto.MiniProfileResponse;
import cloneproject.Instagram.domain.member.dto.UserProfileResponse;
import cloneproject.Instagram.domain.member.entity.Member;

public interface MemberRepositoryQuerydsl {

	UserProfileResponse findUserProfile(Long loginUserId, String username);

	MiniProfileResponse findMiniProfile(Long loginUserId, String username);

	List<Member> findAllByUsernames(List<String> usernames);

}
