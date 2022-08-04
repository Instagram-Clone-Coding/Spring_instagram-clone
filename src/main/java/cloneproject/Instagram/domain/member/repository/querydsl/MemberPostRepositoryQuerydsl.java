package cloneproject.Instagram.domain.member.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.MemberPostDto;

public interface MemberPostRepositoryQuerydsl {


	Page<MemberPostDto> findMemberPostDtos(Long loginMemberId, String username, Pageable pageable);

	Page<MemberPostDto> findMemberSavedPostDtoPage(Long loginMemberId, Pageable pageable);

	Page<MemberPostDto> findMemberTaggedPostDtoPage(Long loginMemberId, String username, Pageable pageable);

}
