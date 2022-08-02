package cloneproject.Instagram.domain.member.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.MemberPostDto;
import cloneproject.Instagram.domain.member.entity.Member;

public interface MemberPostRepositoryQuerydsl {


	Page<MemberPostDto> findMemberPostDtos(Long loginMemberId, String username, Pageable pageable);

	Page<MemberPostDto> findMemberSavedPostDtos(Long loginMemberId, Pageable pageable);

	Page<MemberPostDto> findMemberTaggedPostDtos(Long loginMemberId, String username, Pageable pageable);

}
