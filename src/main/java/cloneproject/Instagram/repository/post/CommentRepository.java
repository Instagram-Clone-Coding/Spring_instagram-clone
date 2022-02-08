package cloneproject.Instagram.repository.post;

import cloneproject.Instagram.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryQuerydsl{

    @Query(value = "select p from Comment p join fetch p.member where p.id = :id")
    Optional<Comment> findWithMemberById(@Param("id") Long id);
}
