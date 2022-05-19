package cloneproject.Instagram.domain.member.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.MemberPostDto;

public interface MemberPostRepositoryQuerydsl {

	Page<MemberPostDto> getMemberPostDtos(String username, Pageable pageable);

	Page<MemberPostDto> getMemberSavedPostDtos(Long loginUserId, Pageable pageable);

	Page<MemberPostDto> getMemberTaggedPostDtos(String username, Pageable pageable);

}
