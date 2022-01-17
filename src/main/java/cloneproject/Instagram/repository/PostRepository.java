package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryQuerydsl, PostRepositoryJdbc {

    Optional<Post> findByIdAndMemberId(Long postId, Long memberId);
}
