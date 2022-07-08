package cloneproject.Instagram.domain.feed.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.PostLikeDto;
import cloneproject.Instagram.domain.member.dto.LikeMemberDto;

public interface PostLikeRepositoryQuerydsl {

	List<PostLikeDto> findAllPostLikeDtoOfFollowings(Long memberId, List<Long> postIds);

	Page<LikeMemberDto> findPostLikeMembersDtoPage(Pageable pageable, Long postId, Long memberId);

	Page<LikeMemberDto> findCommentLikeMembersDtoPage(Pageable pageable, Long commentId, Long memberId);

}
