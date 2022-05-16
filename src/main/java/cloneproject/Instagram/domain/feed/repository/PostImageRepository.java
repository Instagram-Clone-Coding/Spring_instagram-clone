package cloneproject.Instagram.domain.feed.repository;

import cloneproject.Instagram.domain.feed.dto.PostImageDTO;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostImage;

import cloneproject.Instagram.domain.feed.repository.jdbc.PostImageRepositoryJdbc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long>, PostImageRepositoryJdbc {

	List<PostImage> findAllByPostId(Long postId);

	List<PostImage> findAllByPost(Post post);

	@Query("select new cloneproject.Instagram.domain.feed.dto.PostImageDTO("
		+ "pi.post.id, pi.id, pi.image.imageUrl, pi.altText) "
		+ "from PostImage pi "
		+ "where pi.post.id in :postIds")
	List<PostImageDTO> findAllPostImageDto(@Param(value = "postIds") List<Long> postIds);

}
