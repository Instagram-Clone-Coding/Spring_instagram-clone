package cloneproject.Instagram.domain.feed.repository;

import cloneproject.Instagram.domain.feed.entity.PostImage;
import cloneproject.Instagram.domain.feed.entity.PostTag;

import cloneproject.Instagram.domain.feed.repository.jdbc.PostTagRepositoryJdbc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long>, PostTagRepositoryJdbc {

	List<PostTag> findAllByPostImageIn(List<PostImage> postImages);
}
