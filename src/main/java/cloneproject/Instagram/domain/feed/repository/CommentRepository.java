package cloneproject.Instagram.domain.feed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.repository.querydsl.CommentRepositoryQuerydsl;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryQuerydsl {

	@Query(value = "select c from Comment c join fetch c.member where c.id = :id")
	Optional<Comment> findWithMemberById(@Param("id") Long id);

	@Query(value = "select c from Comment c join fetch c.post p join fetch p.member where c.id = :id")
	Optional<Comment> findWithPostAndMemberById(@Param("id") Long id);

	List<Comment> findAllByPost(Post post);

	Optional<Comment> findFirstByPostAndIdNotInOrderByIdDesc(Post post, List<Long> ids);

	List<Comment> findAllByParent(Comment comment);

	List<Comment> findTop2ByPostIdOrderByIdDesc(Long postId);

}
