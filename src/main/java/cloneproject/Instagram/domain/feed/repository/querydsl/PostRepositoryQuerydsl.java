package cloneproject.Instagram.domain.feed.repository.querydsl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.PostDto;

public interface PostRepositoryQuerydsl {

	Page<PostDto> findPostDtoPage(Long memberId, Pageable pageable);

	Optional<PostDto> findPostDto(Long postId, Long memberId);

	Optional<PostDto> findPostDtoWithoutLogin(Long postId);

	Page<PostDto> findPostDtoPage(Pageable pageable, Long memberId, List<Long> postIds);

}
