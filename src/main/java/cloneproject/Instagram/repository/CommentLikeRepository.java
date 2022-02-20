package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.comment.Comment;
import cloneproject.Instagram.entity.comment.CommentLike;
import cloneproject.Instagram.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    List<CommentLike> findAllByCommentIn(List<Comment> comments);

    List<CommentLike> findAllByComment(Comment comment);

    Optional<CommentLike> findByMemberAndComment(Member member, Comment comment);
}
