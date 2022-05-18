package cloneproject.Instagram.domain.feed.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.PostLikeDto;
import cloneproject.Instagram.domain.member.dto.LikeMembersDto;

public interface PostLikeRepositoryQuerydsl {

	List<PostLikeDto> findAllPostLikeDtoOfFollowings(Long memberId, List<Long> postIds);

	Page<LikeMembersDto> findPostLikeMembersDtoPage(Pageable pageable, Long postId, Long memberId);

	Page<LikeMembersDto> findCommentLikeMembersDtoPage(Pageable pageable, Long commentId, Long memberId);

}
