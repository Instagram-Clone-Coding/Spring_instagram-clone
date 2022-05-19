package cloneproject.Instagram.domain.feed.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.CommentDto;

public interface CommentRepositoryQuerydsl {

	List<CommentDto> findAllRecentCommentDto(Long memberId, List<Long> postIds);

	Page<CommentDto> findCommentDtoPage(Long memberId, Long postId, Pageable pageable);

	Page<CommentDto> findReplyDtoPage(Long memberId, Long commentId, Pageable pageable);

}
