package cloneproject.Instagram.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.dto.post.MemberPostDTO;

public interface MemberPostRepositoryQuerydsl {
    
    List<MemberPostDTO> getRecent15PostDTOs(Long loginedUserId, String username);
    Page<MemberPostDTO> getMemberPostDto(Long loginedUserId, String username, Pageable pageable);

    List<MemberPostDTO> getRecent15SavedPostDTOs(Long loginedUserId);
    Page<MemberPostDTO> getMemberSavedPostDto(Long loginedUserId, Pageable pageable);

    List<MemberPostDTO> getRecent15TaggedPostDTOs(Long loginedUserId, String username);
    Page<MemberPostDTO> getMemberTaggedPostDto(Long loginedUserId, String username, Pageable pageable);
    
}
