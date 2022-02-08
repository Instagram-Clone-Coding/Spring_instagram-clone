package cloneproject.Instagram.repository.post;

import cloneproject.Instagram.dto.comment.CommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryQuerydsl {

    Page<CommentDTO> findCommentDtoPage(Long memberId, Long postId, Pageable pageable);
    Page<CommentDTO> findReplyDtoPage(Long memberId, Long commentId, Pageable pageable);
}
