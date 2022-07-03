package cloneproject.Instagram.domain.dm.repository;

import cloneproject.Instagram.domain.feed.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.dm.entity.MessagePost;

import java.util.List;

public interface MessagePostRepository extends JpaRepository<MessagePost, Long> {

    @Query("select mp from MessagePost mp join fetch mp.post where mp.id in :messageIds")
    List<MessagePost> findAllWithPostByIdIn(@Param("messageIds") List<Long> messageIds);

    List<MessagePost> findAllByPost(Post post);

}
