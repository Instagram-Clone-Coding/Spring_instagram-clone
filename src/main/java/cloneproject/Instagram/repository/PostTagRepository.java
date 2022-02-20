package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.post.PostImage;
import cloneproject.Instagram.entity.post.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    List<PostTag> findAllByPostImageIn(List<PostImage> postImages);
}
