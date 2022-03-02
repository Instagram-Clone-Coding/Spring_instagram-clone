package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.hashtag.Hashtag;
import cloneproject.Instagram.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>, HashtagRepositoryJdbc {

    List<Hashtag> findAllByPostAndNameIn(Post post, Set<String> names);
}
