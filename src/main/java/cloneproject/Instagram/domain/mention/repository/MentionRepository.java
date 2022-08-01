package cloneproject.Instagram.domain.mention.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.mention.entity.Mention;
import cloneproject.Instagram.domain.mention.repository.jdbc.MentionRepositoryJdbc;

public interface MentionRepository extends JpaRepository<Mention, Long>, MentionRepositoryJdbc {

	List<Mention> findAllByPost(Post post);

	List<Mention> findAllByCommentIn(List<Comment> comments);

}
