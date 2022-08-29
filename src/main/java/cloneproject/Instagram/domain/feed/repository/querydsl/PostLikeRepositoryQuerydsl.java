package cloneproject.Instagram.domain.feed.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.PostLikeCountDto;
import cloneproject.Instagram.domain.feed.dto.PostLikeDto;
import cloneproject.Instagram.domain.member.dto.LikeMemberDto;
import cloneproject.Instagram.domain.member.entity.Member;

public interface PostLikeRepositoryQuerydsl {

	List<PostLikeDto> findAllPostLikeDtoInFollowings(Long memberId, List<Long> postIds, List<Member> followings);

	Page<LikeMemberDto> findPostLikeMembersDtoPage(Pageable pageable, Long postId, Long memberId);

	Page<LikeMemberDto> findPostLikeMembersDtoPageInFollowings(Pageable pageable, Long postId, Long memberId,
		List<Member> followings);

	Page<LikeMemberDto> findCommentLikeMembersDtoPage(Pageable pageable, Long commentId, Long memberId);

	List<PostLikeCountDto> findAllPostLikeCountDtoOfFollowingsLikedPostByMemberAndPostIdIn(Member member,
		List<Long> postIds);

}
