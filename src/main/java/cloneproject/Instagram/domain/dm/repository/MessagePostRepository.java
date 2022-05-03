package cloneproject.Instagram.domain.dm.repository;

import cloneproject.Instagram.domain.feed.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.dm.entity.MessagePost;

import java.util.List;

public interface MessagePostRepository extends JpaRepository<MessagePost, Long> {

    List<MessagePost> findAllByPost(Post post);
}
