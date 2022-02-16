package cloneproject.Instagram.repository.post;

import cloneproject.Instagram.entity.comment.RecentComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentCommentRepository extends JpaRepository<RecentComment, Long> {

    List<RecentComment> findAllByPostId(Long postId);
}
