package cloneproject.Instagram.repository.post;

import cloneproject.Instagram.entity.post.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryQuerydsl, PostRepositoryJdbc {

    Optional<Post> findByIdAndMemberId(Long postId, Long memberId);

    @Query("select p from Post p join fetch p.member where p.id = :postId")
    Optional<Post> findWithMemberById(@Param("postId") Long postId);
}
