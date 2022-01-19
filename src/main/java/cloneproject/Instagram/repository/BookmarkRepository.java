package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.post.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByMemberIdAndPostId(Long memberId, Long postId);
}
