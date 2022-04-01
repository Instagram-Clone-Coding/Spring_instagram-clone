package cloneproject.Instagram.domain.feed.repository;

import cloneproject.Instagram.domain.feed.dto.PostTagDTO;
import cloneproject.Instagram.domain.feed.entity.PostImage;
import cloneproject.Instagram.domain.feed.entity.PostTag;

import cloneproject.Instagram.domain.feed.repository.jdbc.PostTagRepositoryJdbc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long>, PostTagRepositoryJdbc {

	List<PostTag> findAllByPostImageIn(List<PostImage> postImages);

	@Query("select new cloneproject.Instagram.domain.feed.dto.PostTagDTO("
		+ "pt.postImage.id, pt.id, pt.tag) "
		+ "from PostTag pt "
		+ "where pt.postImage.id in :postImageIds")
	List<PostTagDTO> findAllPostTagDto(@Param(value = "postImageIds") List<Long> postImageIds);
}
