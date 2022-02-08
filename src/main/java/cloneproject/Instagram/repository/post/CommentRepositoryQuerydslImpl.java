package cloneproject.Instagram.repository.post;

import cloneproject.Instagram.dto.comment.CommentDTO;
import cloneproject.Instagram.dto.comment.QCommentDTO;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static cloneproject.Instagram.entity.comment.QComment.comment;
import static cloneproject.Instagram.entity.comment.QCommentLike.commentLike;
import static cloneproject.Instagram.entity.member.QMember.member;

@RequiredArgsConstructor
public class CommentRepositoryQuerydslImpl implements CommentRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentDTO> findCommentDtoPage(Long memberId, Long postId, Pageable pageable) {
        final List<CommentDTO> commentDTOs = queryFactory
                .select(new QCommentDTO(
                        comment.post.id,
                        comment.id,
                        comment.member,
                        comment.content,
                        comment.uploadDate,
                        comment.commentLikes.size(),
                        JPAExpressions
                                .selectFrom(commentLike)
                                .where(commentLike.comment.eq(comment).and(commentLike.member.id.eq(memberId)))
                                .exists(),
                        comment.children.size()
                ))
                .from(comment)
                .where(comment.post.id.eq(postId).and(comment.id.eq(comment.parent.id)))
                .innerJoin(comment.member, member)
                .orderBy(comment.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(commentDTOs, pageable, commentDTOs.size());
    }

    @Override
    public Page<CommentDTO> findReplyDtoPage(Long memberId, Long commentId, Pageable pageable) {
        final List<CommentDTO> commentDTOs = queryFactory
                .select(new QCommentDTO(
                        comment.post.id,
                        comment.id,
                        comment.member,
                        comment.content,
                        comment.uploadDate,
                        comment.commentLikes.size(),
                        JPAExpressions
                                .selectFrom(commentLike)
                                .where(commentLike.comment.eq(comment).and(commentLike.member.id.eq(memberId)))
                                .exists(),
                        comment.children.size()
                ))
                .from(comment)
                .where(comment.parent.id.eq(commentId))
                .innerJoin(comment.member, member)
                .orderBy(comment.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(commentDTOs, pageable, commentDTOs.size());
    }
}
