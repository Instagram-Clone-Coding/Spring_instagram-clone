package cloneproject.Instagram.domain.feed.repository;

import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.CommentLike;
import cloneproject.Instagram.domain.member.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    List<CommentLike> findAllByCommentIn(List<Comment> comments);

    List<CommentLike> findAllByComment(Comment comment);

    Optional<CommentLike> findByMemberAndComment(Member member, Comment comment);
}
