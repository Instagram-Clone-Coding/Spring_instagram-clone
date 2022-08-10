package cloneproject.Instagram.domain.mention.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.mention.entity.Mention;
import cloneproject.Instagram.domain.mention.repository.jdbc.MentionRepositoryJdbc;

public interface MentionRepository extends JpaRepository<Mention, Long>, MentionRepositoryJdbc {

	List<Mention> findAllByPost(Post post);

	List<Mention> findAllByCommentIn(List<Comment> comments);

	@Query("select m from Mention m join fetch m.target where m.post.id = :postId")
	List<Mention> findAllWithTargetByPostId(@Param("postId") Long postId);

}
