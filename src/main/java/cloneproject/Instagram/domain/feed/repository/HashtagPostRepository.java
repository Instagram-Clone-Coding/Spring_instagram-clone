package cloneproject.Instagram.domain.feed.repository;

import cloneproject.Instagram.domain.feed.entity.HashtagPost;
import cloneproject.Instagram.domain.feed.repository.jdbc.HashtagPostRepositoryJdbc;
import cloneproject.Instagram.domain.hashtag.entity.Hashtag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagPostRepository extends JpaRepository<HashtagPost, Long>, HashtagPostRepositoryJdbc {

    List<HashtagPost> findAllByHashtagIn(List<Hashtag> deleteHashtags);
}
