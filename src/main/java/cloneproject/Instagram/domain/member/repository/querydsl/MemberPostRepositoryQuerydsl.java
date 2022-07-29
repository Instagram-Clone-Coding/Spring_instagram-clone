package cloneproject.Instagram.domain.member.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.MemberPostDto;
import cloneproject.Instagram.domain.member.entity.Member;

public interface MemberPostRepositoryQuerydsl {


	Page<MemberPostDto> findMemberPostDtos(Member loginMember, String username, Pageable pageable);

	Page<MemberPostDto> findMemberSavedPostDtos(Long loginUserId, Pageable pageable);

	Page<MemberPostDto> findMemberTaggedPostDtos(Member loginMember, String username, Pageable pageable);

}
