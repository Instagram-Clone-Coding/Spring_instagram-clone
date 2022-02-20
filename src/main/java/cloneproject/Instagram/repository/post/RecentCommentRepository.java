package cloneproject.Instagram.repository.post;

import cloneproject.Instagram.entity.comment.RecentComment;
import cloneproject.Instagram.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecentCommentRepository extends JpaRepository<RecentComment, Long> {

    @Query("select rc from RecentComment rc join fetch rc.comment where rc.post.id = :id")
    List<RecentComment> findAllWithCommentByPostId(@Param("id") Long id);

    List<RecentComment> findAllByPost(Post post);
}
