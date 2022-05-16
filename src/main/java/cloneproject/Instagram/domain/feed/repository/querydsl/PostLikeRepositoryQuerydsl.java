package cloneproject.Instagram.domain.feed.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.PostLikeDTO;
import cloneproject.Instagram.domain.member.dto.LikeMembersDTO;

public interface PostLikeRepositoryQuerydsl {

	List<PostLikeDTO> findAllPostLikeDtoOfFollowings(Long memberId, List<Long> postIds);

	Page<LikeMembersDTO> findPostLikeMembersDtoPage(Pageable pageable, Long postId, Long memberId);

	Page<LikeMembersDTO> findCommentLikeMembersDtoPage(Pageable pageable, Long commentId, Long memberId);

}
