package cloneproject.Instagram.domain.feed.repository.querydsl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.PostDto;

public interface PostRepositoryQuerydsl {

	Page<PostDto> findPostDtoPageOfFollowingMembersOrHashtagsByMemberId(Long memberId, Pageable pageable);

	Optional<PostDto> findPostDtoByPostIdAndMemberId(Long postId, Long memberId);

	Optional<PostDto> findPostDtoWithoutLoginByPostId(Long postId);

	Page<PostDto> findPostDtoPageByMemberIdAndPostIdIn(Pageable pageable, Long memberId, List<Long> postIds);

}
