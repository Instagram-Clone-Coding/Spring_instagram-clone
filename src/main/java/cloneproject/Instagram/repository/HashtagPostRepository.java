package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.hashtag.Hashtag;
import cloneproject.Instagram.entity.post.HashtagPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagPostRepository extends JpaRepository<HashtagPost, Long>, HashtagPostRepositoryJdbc {

    List<HashtagPost> findAllByHashtagIn(List<Hashtag> deleteHashtags);
}
