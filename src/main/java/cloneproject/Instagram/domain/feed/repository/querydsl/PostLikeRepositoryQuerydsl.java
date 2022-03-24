package cloneproject.Instagram.domain.feed.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.member.dto.LikeMembersDTO;

public interface PostLikeRepositoryQuerydsl {

    Page<LikeMembersDTO> findLikeMembersDtoPageByPostIdAndMemberId(Pageable pageable, Long postId, Long memberId);

    Page<LikeMembersDTO> findLikeMembersDtoPageByCommentIdAndMemberId(Pageable pageable, Long commentId, Long memberId);
}
