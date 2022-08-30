package cloneproject.Instagram.domain.hashtag.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import cloneproject.Instagram.domain.hashtag.entity.HashtagPost;
import cloneproject.Instagram.domain.hashtag.repository.jdbc.HashtagPostRepositoryJdbc;
import cloneproject.Instagram.domain.hashtag.repository.querydsl.HashtagPostRepositoryQuerydsl;

public interface HashtagPostRepository extends JpaRepository<HashtagPost, Long>, HashtagPostRepositoryJdbc,
	HashtagPostRepositoryQuerydsl {

	List<HashtagPost> findAllByPost(Post post);

	List<HashtagPost> findByHashtagAndPost(Hashtag hashtag, Post post);

	List<HashtagPost> findAllByPostAndHashtagIn(Post post, List<Hashtag> hashtags);

	@Query("select hp from HashtagPost hp join fetch hp.post where hp.hashtag.id = :hashtagId")
	List<HashtagPost> findAllWithPostByHashtagId(Pageable pageable, @Param("hashtagId") Long hashtagId);

}
