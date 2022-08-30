package cloneproject.Instagram.domain.feed.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.PostLikeCountDto;
import cloneproject.Instagram.domain.feed.dto.PostLikeDto;
import cloneproject.Instagram.domain.member.dto.LikeMemberDto;
import cloneproject.Instagram.domain.member.entity.Member;

public interface PostLikeRepositoryQuerydsl {

	List<PostLikeDto> findAllPostLikeDtoOfFollowingsByMemberIdAndPostIdIn(Long memberId, List<Long> postIds);

	Page<LikeMemberDto> findPostLikeMembersDtoPage(Pageable pageable, Long postId, Long memberId);

	Page<LikeMemberDto> findPostLikeMembersDtoPageOfFollowingsByMemberIdAndPostId(Pageable pageable, Long memberId, Long postId);

	Page<LikeMemberDto> findCommentLikeMembersDtoPage(Pageable pageable, Long commentId, Long memberId);

	List<PostLikeCountDto> findAllPostLikeCountDtoOfFollowingsLikedPostByMemberAndPostIdIn(Member member,
		List<Long> postIds);

}
