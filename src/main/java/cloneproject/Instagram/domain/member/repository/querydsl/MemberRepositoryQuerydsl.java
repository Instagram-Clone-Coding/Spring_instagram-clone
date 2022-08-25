package cloneproject.Instagram.domain.member.repository.querydsl;

import cloneproject.Instagram.domain.member.dto.MiniProfileResponse;
import cloneproject.Instagram.domain.member.dto.UserProfileResponse;

public interface MemberRepositoryQuerydsl {

	UserProfileResponse findUserProfile(Long loginUserId, String username);

	MiniProfileResponse findMiniProfile(Long loginUserId, String username);

}
