package cloneproject.Instagram.domain.feed.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.CommentDTO;

public interface CommentRepositoryQuerydsl {

	Page<CommentDTO> findCommentDtoPage(Long memberId, Long postId, Pageable pageable);

	Page<CommentDTO> findReplyDtoPage(Long memberId, Long commentId, Pageable pageable);
}
