package cloneproject.Instagram.domain.member.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.MemberPostDto;

public interface MemberPostRepositoryQuerydsl {

	Page<MemberPostDto> findMemberPostDtos(String username, Pageable pageable);

	Page<MemberPostDto> findMemberSavedPostDtos(Long loginUserId, Pageable pageable);

	Page<MemberPostDto> findMemberTaggedPostDtos(String username, Pageable pageable);

}
