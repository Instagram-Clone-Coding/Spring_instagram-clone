package cloneproject.Instagram.repository;


import java.util.List;

import cloneproject.Instagram.dto.member.MiniProfileResponse;
import cloneproject.Instagram.dto.member.UserProfileResponse;
import cloneproject.Instagram.entity.member.Member;

public interface MemberRepositoryQuerydsl {
    
    UserProfileResponse getUserProfile(Long loginedUserId, String username);
    MiniProfileResponse getMiniProfile(Long loginedUserId, String username);
    List<Member> findAllByUsernames(List<String> usernames);

}
