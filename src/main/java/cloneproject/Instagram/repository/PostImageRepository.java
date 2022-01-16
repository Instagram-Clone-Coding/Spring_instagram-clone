package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.post.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findAllByPostId(Long postId);
}
