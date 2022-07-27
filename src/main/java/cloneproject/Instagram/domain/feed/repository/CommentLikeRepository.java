package cloneproject.Instagram.domain.feed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.CommentLike;
import cloneproject.Instagram.domain.member.entity.Member;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

	List<CommentLike> findAllByCommentIn(List<Comment> comments);

	Optional<CommentLike> findByMemberAndComment(Member member, Comment comment);

}
