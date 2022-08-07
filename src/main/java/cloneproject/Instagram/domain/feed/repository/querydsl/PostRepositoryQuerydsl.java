package cloneproject.Instagram.domain.feed.repository.querydsl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.PostDto;
import cloneproject.Instagram.domain.feed.dto.PostResponse;

public interface PostRepositoryQuerydsl {

	Page<PostDto> findPostDtoPage(Long memberId, Pageable pageable);

	Optional<PostResponse> findPostResponse(Long postId, Long memberId);

	Optional<PostResponse> findPostResponseWithoutLogin(Long postId);

	Page<PostDto> findPostDtoPage(Pageable pageable, Long memberId, List<Long> postIds);

}
