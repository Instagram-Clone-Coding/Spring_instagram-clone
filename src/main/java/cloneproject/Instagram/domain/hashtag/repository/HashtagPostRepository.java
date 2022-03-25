package cloneproject.Instagram.domain.hashtag.repository;

import cloneproject.Instagram.domain.hashtag.entity.HashtagPost;
import cloneproject.Instagram.domain.hashtag.repository.jdbc.HashtagPostRepositoryJdbc;
import cloneproject.Instagram.domain.hashtag.entity.Hashtag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagPostRepository extends JpaRepository<HashtagPost, Long>, HashtagPostRepositoryJdbc {

    List<HashtagPost> findAllByHashtagIn(List<Hashtag> deleteHashtags);
}
