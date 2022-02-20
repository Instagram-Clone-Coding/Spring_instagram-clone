package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.comment.Comment;
import cloneproject.Instagram.entity.mention.Mention;
import cloneproject.Instagram.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentionRepository extends JpaRepository<Mention, Long>, MentionRepositoryJdbc {

    List<Mention> findAllByPost(Post post);

    List<Mention> findAllByComment(Comment comment);
}
