package cloneproject.Instagram.repository.post;

import cloneproject.Instagram.entity.post.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryQuerydsl, PostRepositoryJdbc {

    Optional<Post> findByIdAndMemberId(Long postId, Long memberId);
    Page<Post> findByMemberId(Long memberId, Pageable pageable);
    List<Post> findTop3ByMemberIdOrderByUploadDateDesc(Long memberId);    
    int countByMemberId(Long memberId);

}
