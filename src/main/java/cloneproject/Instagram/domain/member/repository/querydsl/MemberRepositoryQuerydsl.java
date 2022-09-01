package cloneproject.Instagram.domain.member.repository.querydsl;

import cloneproject.Instagram.domain.member.dto.MiniProfileResponse;
import cloneproject.Instagram.domain.member.dto.UserProfileResponse;

public interface MemberRepositoryQuerydsl {

	UserProfileResponse findUserProfileByLoginMemberIdAndTargetUsername(Long loginMemberId, String username);

	MiniProfileResponse findMiniProfileByLoginMemberIdAndTargetUsername(Long loginMemberId, String username);

}
