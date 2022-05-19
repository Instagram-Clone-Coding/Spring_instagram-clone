package cloneproject.Instagram.domain.feed.repository.querydsl;

import cloneproject.Instagram.domain.feed.dto.PostDto;
import cloneproject.Instagram.domain.feed.dto.PostResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryQuerydsl {

	Page<PostDto> findPostDtoPage(Long memberId, Pageable pageable);

	Optional<PostResponse> findPostResponse(Long postId, Long memberId);

	Page<PostDto> findPostDtoPage(Pageable pageable, Long memberId, List<Long> postIds);

}
