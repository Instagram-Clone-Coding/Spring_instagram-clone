package cloneproject.Instagram.domain.feed.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.CommentDTO;

public interface CommentRepositoryQuerydsl {

	List<CommentDTO> findAllRecentCommentDto(Long memberId, List<Long> postIds);

	Page<CommentDTO> findCommentDtoPage(Long memberId, Long postId, Pageable pageable);

	Page<CommentDTO> findReplyDtoPage(Long memberId, Long commentId, Pageable pageable);

}
