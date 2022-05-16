package cloneproject.Instagram.domain.member.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.MemberPostDTO;

public interface MemberPostRepositoryQuerydsl {

	Page<MemberPostDTO> getMemberPostDTOs(String username, Pageable pageable);

	Page<MemberPostDTO> getMemberSavedPostDTOs(Long loginUserId, Pageable pageable);

	Page<MemberPostDTO> getMemberTaggedPostDTOs(String username, Pageable pageable);

}
