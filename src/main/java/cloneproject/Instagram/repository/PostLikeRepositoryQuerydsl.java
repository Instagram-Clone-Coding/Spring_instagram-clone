package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.member.LikeMembersDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostLikeRepositoryQuerydsl {

    Page<LikeMembersDTO> findLikeMembersDtoPageByPostIdAndMemberId(Pageable pageable, Long postId, Long memberId);

    Page<LikeMembersDTO> findLikeMembersDtoPageByCommentIdAndMemberId(Pageable pageable, Long commentId, Long memberId);
}
