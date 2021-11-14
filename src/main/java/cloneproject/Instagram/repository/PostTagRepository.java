package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.post.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
