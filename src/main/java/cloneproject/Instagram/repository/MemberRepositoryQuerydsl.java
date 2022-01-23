package cloneproject.Instagram.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.dto.member.MiniProfileResponse;
import cloneproject.Instagram.dto.member.SearchedMemberDTO;
import cloneproject.Instagram.dto.member.UserProfileResponse;
import cloneproject.Instagram.dto.post.MemberPostDTO;

public interface MemberRepositoryQuerydsl {
    
    UserProfileResponse getUserProfile(Long loginedUserId, String username);
    MiniProfileResponse getMiniProfile(Long loginedUserId, String username);
    List<SearchedMemberDTO> searchMember(Long loginedUserId, String text);
    List<MemberPostDTO> getRecent15PostDTOs(Long loginedUserId, String username);
    Page<MemberPostDTO> getMemberPostDto(Long loginedUserId, String username, Pageable pageable);

}
