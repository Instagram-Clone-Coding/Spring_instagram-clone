package cloneproject.Instagram.domain.hashtag.repository;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import cloneproject.Instagram.domain.hashtag.entity.HashtagPost;
import cloneproject.Instagram.domain.hashtag.repository.jdbc.HashtagPostRepositoryJdbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagPostRepository extends JpaRepository<HashtagPost, Long>, HashtagPostRepositoryJdbc {

    List<HashtagPost> findAllByPost(Post post);

	List<HashtagPost> findByHashtagAndPost(Hashtag hashtag, Post post);

	List<HashtagPost> findAllByPostAndHashtagIn(Post post, List<Hashtag> hashtags);

	Page<HashtagPost> findAllByHashtagOrderByPostIdDesc(Pageable pageable, Hashtag hashtag);
}
